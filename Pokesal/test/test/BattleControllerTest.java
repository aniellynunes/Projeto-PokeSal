package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import controller.BattleController;
import entities.BattleLog;
import entities.Enemy;
import entities.Pokesal;
import entities.Trainer;
import enums.Campos;
import enums.Items;
import enums.Moves;
import enums.Type;
import interfaces.BattleDB;
import interfaces.BattleEngine;
import interfaces.CampoService;
import interfaces.D100;
import interfaces.SalDexDB;
import repositories.BattleRepository;
import repositories.SalDexRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("BattleController com Mockito")

class BattleControllerTest {

	private static final Instant DIA = Instant.parse("2026-11-16T12:00:00Z");
	private static final Instant NOITE = Instant.parse("2026-11-16T22:00:00Z");
	private static final LocalTime HORA_DIA = LocalTime.of(12, 0);
	private static final LocalTime HORA_NOITE = LocalTime.of(22, 0);

	private static InputStream stdinOriginal;

	@Mock
	private D100 d100;
	@Mock
	private CampoService campoService;
	@Mock
	private Clock clock;
	@Mock
	private BattleDB battleDB;
	@Mock
	private SalDexDB salDex;
	@Mock
	private BattleEngine registro;

	@InjectMocks
	private controller.BattleController controller;

	private Trainer ash;
	private Enemy gary;

	@BeforeAll
	static void silenciarStdin() {
		stdinOriginal = System.in;
		System.setIn(new ByteArrayInputStream(new byte[0]));
	}

	@AfterAll
	static void restaurarStdin() {
		System.setIn(stdinOriginal);
	}

	@BeforeEach
	void criarCombatentes() {
		ash = treinador("Ash", pokesal("Squirt", 100, 20, 50));
		gary = new Enemy("Gary", pokesal("Totodile", 100, 20, 40));
	}

	// =====================================================================
	// Testes obrigatórios
	// =====================================================================

	@Test
	@DisplayName("Crítico: dano x1.5 quando o gerador mockado devolve crítico")
	void testAcertoCriticoComGeradorMockado() {
		when(d100.rolarDado100()).thenReturn(50);
		// valor exato: 6.25 é a regra de negócio do crítico
		when(d100.testarChance(6.25)).thenReturn(true);

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		// base: 40 * (20 / (20 + 20)) * 1.0 * 1.0 = 20; crítico: 20 * 1.5 = 30
		assertEquals(70.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarDano("Totodile", 30, true, false);
		verify(d100).testarChance(6.25);
	}

	@Test
	@DisplayName("Sem crítico: dano base (contraste do teste de crítico)")
	void testDanoSemCritico() {
		when(d100.rolarDado100()).thenReturn(50);
		when(d100.testarChance(6.25)).thenReturn(false);

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		assertEquals(80.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarDano("Totodile", 20, false, false);
	}

	@Test
	@DisplayName("Erro: precisão falha => dano zero e HP do alvo inalterado")
	void testGolpeErraQuandoPrecisaoFalha() {
		// HYDRO_PUMP tem precisão 80; rolar 95 > 80 => erra
		when(d100.rolarDado100()).thenReturn(95);

		controller.executarAtaque(ash, gary, Moves.HYDRO_PUMP, Campos.CANTEIRO_CENTRAL);

		assertEquals(100.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarAtaque("Squirt", "HYDRO_PUMP");
		verify(registro).registrarDano("Totodile", 0, false, true);
		// errou: não há teste de crítico, nem status, nem consulta de vingança
		verify(d100, never()).testarChance(anyDouble());
		verify(registro, never()).registrarEfeitoStatus(anyString(), anyString());
		verifyNoInteractions(battleDB);
	}

	@Test
	@DisplayName("Retornos em sequência do dado: erra e depois acerta")
	void testRolagensConsecutivasErraDepoisAcerta() {
		// 1ª chamada devolve 95 (erra), 2ª devolve 50 (acerta)
		when(d100.rolarDado100()).thenReturn(95, 50);
		// aqui a chance de crítico é irrelevante: matcher
		when(d100.testarChance(anyDouble())).thenReturn(false);

		controller.executarAtaque(ash, gary, Moves.HYDRO_PUMP, Campos.CANTEIRO_CENTRAL);
		assertEquals(100.0, gary.getChoosedPokesal().getHp(), 0.001);

		controller.executarAtaque(ash, gary, Moves.HYDRO_PUMP, Campos.CANTEIRO_CENTRAL);
		// 110 * (20 / 40) = 55
		assertEquals(45.0, gary.getChoosedPokesal().getHp(), 0.001);

		verify(d100, times(2)).rolarDado100();
		verify(registro, times(2)).registrarAtaque("Squirt", "HYDRO_PUMP");
		verify(registro, times(1)).registrarDano("Totodile", 0, false, true);
		verify(registro, times(1)).registrarDano("Totodile", 55, false, false);
	}

	@Test
	@DisplayName("Asfalto quente só causa dano térmico durante o dia")
	void testAsfaltoQuenteSoDeDia() throws Exception {
		prepararDadosSemCritico();

		// ---------- DIA (12:00): relógio e serviço de terreno mockados ----------
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		Trainer jogadorDia = treinador("Ash", pokesal("Squirt", 100, 20, 50));
		Enemy rivalDia = new Enemy("Gary", pokesal("Totodile", 100, 20, 40));

		controller.iniciarBatalha(jogadorDia, rivalDia);

		verify(registro, atLeastOnce()).registrarEfeitoTerreno("ASFALTO_QUENTE", "Dano térmico aplicado");
		verify(registro, never()).registrarEfeitoTerreno("ASFALTO_QUENTE", "Inativo durante a noite");
		// por turno: -20 do golpe do jogador e -5 do asfalto; o jogador cai no 2º turno
		// (45 + 45 + 5 + 5 >= 100), logo o rival termina com 100 - 2 * (20 + 5) = 50
		assertEquals(50.0, rivalDia.getChoosedPokesal().getHp(), 0.001);
		assertEquals(2, capturarResultadoSalvo().getTurnos());

		clearInvocations(registro, battleDB);

		// ---------- NOITE (22:00): mesma arena, relógio diferente ----------
		prepararArena(NOITE, HORA_NOITE, Campos.ASFALTO_QUENTE);
		Trainer jogadorNoite = treinador("Ash", pokesal("Squirt", 100, 20, 50));
		Enemy rivalNoite = new Enemy("Gary", pokesal("Totodile", 100, 20, 40));

		controller.iniciarBatalha(jogadorNoite, rivalNoite);

		verify(registro, atLeastOnce()).registrarEfeitoTerreno("ASFALTO_QUENTE", "Inativo durante a noite");
		verify(registro, never()).registrarEfeitoTerreno("ASFALTO_QUENTE", "Dano térmico aplicado");
		// sem os 5 de dano por turno a luta dura 3 turnos e o rival termina com
		// 100-20*3 = 40
		assertEquals(40.0, rivalNoite.getChoosedPokesal().getHp(), 0.001);
		assertEquals(3, capturarResultadoSalvo().getTurnos());
	}

	@Test
	@DisplayName("Ordem das chamadas ao registro de batalha em um turno")
	void testSequenciaDeEventosTurno() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();

		controller.iniciarBatalha(ash, rival);

		InOrder ordem = inOrder(registro, salDex, battleDB);
		ordem.verify(registro).registrarAtaque("Squirt", "WATER_GUN");
		ordem.verify(registro).registrarDano("Totodile", 20, false, false);
		ordem.verify(registro).registrarEfeitoStatus("Totodile", "NENHUM");
		ordem.verify(registro).registrarEfeitoTerreno("ASFALTO_QUENTE", "Dano térmico aplicado");
		ordem.verify(registro).registrarUsoItem("Gary", "POTION");
		// depois do último evento do turno, vêm SalDex e persistência (entre mocks)
		ordem.verify(salDex).salvar("Totodile");
		ordem.verify(battleDB).salvar(any(BattleLog.class));

		// nenhum evento fora dessa sequência foi registrado
		verifyNoMoreInteractions(registro);
	}

	@Test
	@DisplayName("ArgumentCaptor: resultado salvo com vencedor, perdedor e turnos")
	void testResultadoBatalhaSalvo() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();

		controller.iniciarBatalha(ash, rival);

		ArgumentCaptor<BattleLog> captor = ArgumentCaptor.forClass(BattleLog.class);
		verify(battleDB, times(1)).salvar(captor.capture());
		BattleLog salvo = captor.getValue();

		assertEquals("Ash", salvo.getWinner());
		assertEquals("Gary", salvo.getLoser());
		assertEquals(1, salvo.getTurnos());
		assertEquals(Moves.WATER_GUN, salvo.getLastHit());
		assertEquals("Squirt vs Totodile", salvo.getPokesalsUtilizados());
	}

	@Test
	@DisplayName("Falha no repositório não invalida a batalha")
	void testFalhaNoRepositorioNaoInvalidaBatalha() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();
		// salvar é void: doThrow. O tipo do BattleLog é irrelevante aqui: matcher
		doThrow(new IOException("disco cheio")).when(battleDB).salvar(any(BattleLog.class));

		Trainer vencedor = controller.iniciarBatalha(ash, rival);

		assertSame(ash, vencedor);
		assertTrue(ash.getChoosedPokesal().getHp() > 0);
		assertEquals(0.0, rival.getChoosedPokesal().getHp(), 0.001);
		verify(battleDB, times(1)).salvar(any(BattleLog.class));
	}

	@Test
	@DisplayName("Batalha interrompida: repositório nunca é chamado")
	void testRepositorioNaoChamadoSeBatalhaNaoTerminou() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		// sequência: o 1º ataque rola normalmente, o 2º quebra o dado no meio do turno
		when(d100.rolarDado100()).thenReturn(50).thenThrow(new IllegalStateException("dado quebrou"));
		when(d100.testarChance(anyDouble())).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> controller.iniciarBatalha(ash, gary));

		// a luta começou (houve ataque), mas ninguém venceu: nada é persistido
		verify(registro, atLeastOnce()).registrarAtaque(anyString(), anyString());
		verify(battleDB, never()).salvar(any(BattleLog.class));
		verify(salDex, never()).salvar(anyString());
		assertTrue(ash.getChoosedPokesal().getHp() > 0);
		assertTrue(gary.getChoosedPokesal().getHp() > 0);
	}

	// Teste requisito autoral 1
	@Test
	@DisplayName("Vingança: golpe igual ao que derrotou o defensor causa metade do dano")
	void testVingancaReduzDanoDoGolpeDeNocaute() throws Exception {
		prepararDadosSemCritico();
		// ordem dos argumentos importa: (perdedor, vencedor) => valores exatos
		when(battleDB.findLastHit("Gary", "Ash")).thenReturn(Optional.of(Moves.WATER_GUN));

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		// 20 * 0.5 = 10
		assertEquals(90.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarDano("Totodile", 10, false, false);
		verify(battleDB, times(1)).findLastHit("Gary", "Ash");
	}

	@Test
	@DisplayName("Vingança: golpe diferente do que derrotou não reduz o dano")
	void testVingancaNaoSeAplicaQndMoveIsDiferent() throws Exception {
		prepararDadosSemCritico();
		when(battleDB.findLastHit("Gary", "Ash")).thenReturn(Optional.of(Moves.SURF));

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		assertEquals(80.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarDano("Totodile", 20, false, false);
	}

	@Test
	@DisplayName("Vingança combinada com crítico: 20 * 0.5 * 1.5 = 15")
	void testVingancaComCritico() throws Exception {
		when(d100.rolarDado100()).thenReturn(50);
		when(d100.testarChance(6.25)).thenReturn(true);
		when(battleDB.findLastHit("Gary", "Ash")).thenReturn(Optional.of(Moves.WATER_GUN));

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		assertEquals(85.0, gary.getChoosedPokesal().getHp(), 0.001);
		verify(registro).registrarDano("Totodile", 15, true, false);
	}

	// Teste requisito autoral 2
	@Test
	@DisplayName("SalDex: Pokesal derrotado é catalogado ao fim da batalha")
	void testSalDexCatalogaPokesalDerrotado() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();
		when(salDex.discovered("Totodile")).thenReturn(false);
		when(salDex.listarTodos()).thenReturn(List.of("Squirt"));

		controller.iniciarBatalha(ash, rival);

		ArgumentCaptor<String> nomeCatalogado = ArgumentCaptor.forClass(String.class);
		verify(salDex, times(1)).salvar(nomeCatalogado.capture());
		assertEquals("Totodile", nomeCatalogado.getValue());
		assertTrue(ash.getPSalDex().contains("Totodile"));
	}

	@Test
	@DisplayName("SalDex: Sal-Move cataloga o próprio Pokesal do atacante")
	void testSalMoveCatalogaPokesalAtacante() throws Exception {
		prepararDadosSemCritico();
		when(salDex.discovered("Squirt")).thenReturn(false);

		// HYDRO_PUMP é o Sal-Move de um Pokesal do tipo ÁGUA
		controller.executarAtaque(ash, gary, Moves.HYDRO_PUMP, Campos.CANTEIRO_CENTRAL);

		verify(registro).registrarDano("Totodile", 55, false, false);
		verify(salDex, times(1)).salvar("Squirt");
		verify(salDex, never()).salvar("Totodile");
	}

	@Test
	@DisplayName("SalDex: Pokesal já catalogado não é salvo de novo")
	void testSalDexNaoDuplicaPokesalJaCatalogado() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();
		when(salDex.discovered("Totodile")).thenReturn(true);

		controller.iniciarBatalha(ash, rival);

		verify(salDex, times(1)).discovered("Totodile");
		verify(salDex, never()).listarTodos();
		verify(salDex, never()).salvar(anyString());
		// a PokéDex pessoal do treinador é atualizada independentemente do catálogo
		// global
		assertTrue(ash.getPSalDex().contains("Totodile"));
	}

	@Test
	@DisplayName("SalDex: falha ao catalogar não interrompe a batalha nem o salvamento")
	void testFalhaNoSalDexNaoInterrompeABatalha() throws Exception {
		prepararArena(DIA, HORA_DIA, Campos.ASFALTO_QUENTE);
		prepararDadosSemCritico();
		Enemy rival = criarRivalFragilComPocao();
		doThrow(new IllegalStateException("catálogo indisponível")).when(salDex).salvar(anyString());

		Trainer vencedor = controller.iniciarBatalha(ash, rival);

		assertSame(ash, vencedor);
		verify(salDex, times(1)).salvar("Totodile");
		verify(battleDB, times(1)).salvar(any(BattleLog.class));
	}

	private static Pokesal pokesal(String nome, double hp, double def, double spd) {
		return new Pokesal(nome, Type.AGUA, hp, 10.0, def, spd);
	}

	private static Trainer treinador(String nome, Pokesal pokesal) {
		Trainer t = new Trainer(nome);
		t.choosePokesal(pokesal);
		return t;
	}

	/**
	 * Rival com 10 de HP e uma poção: cai no primeiro golpe de WATER_GUN (20 de
	 * dano), então, batalha de 1 turno.
	 */
	private static Enemy criarRivalFragilComPocao() {
		Enemy rival = new Enemy("Gary", pokesal("Totodile", 10, 20, 40));
		rival.addItem(Items.POTION);
		return rival;
	}

	/** Fixa o relógio mockado e o terreno devolvido para aquela hora exata. */
	private void prepararArena(Instant instante, LocalTime hora, Campos campo) {
		when(clock.instant()).thenReturn(instante);
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(campoService.getField(hora)).thenReturn(campo);
	}

	/**
	 * Dado sempre em 50 (acerta qualquer golpe de precisão >= 50) e sem crítico.
	 */
	private void prepararDadosSemCritico() {
		when(d100.rolarDado100()).thenReturn(50);
		when(d100.testarChance(anyDouble())).thenReturn(false);
	}

	private BattleLog capturarResultadoSalvo() throws Exception {
		ArgumentCaptor<BattleLog> captor = ArgumentCaptor.forClass(BattleLog.class);
		verify(battleDB, times(1)).salvar(captor.capture());
		return captor.getValue();
	}

	@Test
	@DisplayName("Canteiro Central regenera HP de Pokesal do tipo GRAMA no fim do turno")
	void testCanteiroCentralRegeneraTipoGrama() {
		prepararArena(DIA, HORA_DIA, Campos.CANTEIRO_CENTRAL);
		prepararDadosSemCritico();

		Trainer p1 = treinador("Ash", new Pokesal("BulbaSal", Type.GRAMA, 40, 12, 10, 45));
		Enemy p2 = new Enemy("Gary", new Pokesal("CharSal", Type.FOGO, 100, 100, 100, 600));

		Trainer win = controller.iniciarBatalha(p1, p2);

		assertSame(p2, win);
		verify(registro, atLeastOnce()).registrarEfeitoTerreno("CANTEIRO_CENTRAL", "Cura vegetal aplicada");
	}

	@Test
	@DisplayName("Poça de Chuva e Piso Molhado dão bônus a Água, mas sem dano passivo no turno")
	void testTerrenosTipoAguaSemEfeitoPassivo() {
		prepararArena(DIA, HORA_DIA, Campos.POCA_DE_CHUVA);
		prepararDadosSemCritico();
		Enemy rival = new Enemy("Gary", pokesal("Totodile", 10, 20, 40));

		controller.iniciarBatalha(ash, rival);

		verify(registro, atLeastOnce()).registrarEfeitoTerreno("POCA_DE_CHUVA", "Sem efeito passivo");
		assertEquals(1.10, ash.getChoosedPokesal().obterVantagemTerreno(Campos.PISO_MOLHADO), 0.001);
	}

	@Test
	@DisplayName("Jogador pode usar item da mochila e usar Sal-Move")
	void testPlayerUseItemAndSalMove() {
		// Turno 1: 2 (Poção) | Turnos 2, 3, 4: 1 e 1 (3x EMBER) | Turno 5: 1 e 3 (FIRE_BLAST)
		String cmds = "2\n1\n1\n1\n1\n1\n1\n1\n3\n";
		System.setIn(new ByteArrayInputStream(cmds.getBytes(StandardCharsets.UTF_8)));

		BattleController ctrl = new BattleController(d100, campoService, clock, battleDB, salDex, registro);
		prepararArena(NOITE, HORA_NOITE, Campos.CANTEIRO_CENTRAL);
		prepararDadosSemCritico();

		// Sem dano térmico: 3x EMBER causa 3 * 40 = 120 de dano no BulbaSal (150 HP), sobrando 30 HP para o Sal-Move finalizar
		Trainer p1 = treinador("Ash", new Pokesal("CharSal", Type.FOGO, 200, 20, 20, 80));
		p1.addItem(Items.POTION);
		Enemy p2 = new Enemy("Gary", new Pokesal("BulbaSal", Type.GRAMA, 150, 10, 20, 30));

		Trainer win = ctrl.iniciarBatalha(p1, p2);

		assertSame(p1, win);
		verify(registro, atLeastOnce()).registrarUsoItem("Ash", "POTION");
		verify(registro, atLeastOnce()).registrarAtaque("CharSal", "FIRE_BLAST");
	}

	
	@Test
	@DisplayName("Estresse: errar 3 golpes seguidos causa dano de recuo no turno seguinte")
	void testRecuoStress() {
		prepararArena(NOITE, HORA_NOITE, Campos.CANTEIRO_CENTRAL);
		when(d100.rolarDado100()).thenReturn(99, 99, 99, 99, 99, 99, 10);
		when(d100.testarChance(anyDouble())).thenReturn(false);

		Trainer p1 = treinador("Ash", new Pokesal("Squirt", Type.AGUA, 50, 20, 50, 0));
		Enemy rival = new Enemy("Gary", new Pokesal("Totodile", Type.AGUA, 15, 20, 40, 10));

		controller.iniciarBatalha(p1, rival);

		assertTrue(p1.getChoosedPokesal().getHp() < 50.0);
	}

	@Test
	@DisplayName("Status negativo ativo no defensor é exibido no registro de dano")
	void testRegistrarEfeitoStatusNoDefensor() {
		prepararDadosSemCritico();
		gary.getChoosedPokesal().setEfeitoStatus("QUEIMADO");

		controller.executarAtaque(ash, gary, Moves.WATER_GUN, Campos.CANTEIRO_CENTRAL);

		assertEquals("QUEIMADO", gary.getChoosedPokesal().getEfeitoStatus());
		verify(registro).registrarEfeitoStatus("Totodile", "QUEIMADO");
	}

	@Test
	@DisplayName("Inimigo dispara seu Sal-Move automaticamente assim que a barra carrega")
	void testEnemySalMoveUse() {
		Pokesal sal = new Pokesal("CharSal", Type.FOGO, 100, 15, 10, 60);
		sal.registrarAcertoAtaque();
		sal.registrarAcertoAtaque();
		sal.registrarAcertoAtaque();

		Enemy lance = new Enemy("Lance", sal);
		Moves golpe = lance.chooseMove(ash.getChoosedPokesal());

		assertEquals(Moves.FIRE_BLAST, golpe);
		assertFalse(sal.isSalMoveDisponivel());
	}

	@Test
	@DisplayName("Itens de batalha: X_ATK, X_DEF, X_SPD e POTION alteram atributos do Pokesal")
	void testUseXItensOnPokesal() {
		Pokesal sal = Pokesal.clonar(Pokesal.getIniciais().get(0));
		sal.setHp(10);

		sal.aplicarItem(Items.POTION);
		sal.aplicarItem(Items.X_ATK);
		sal.aplicarItem(Items.X_DEF);
		sal.aplicarItem(Items.X_SPD);

		assertEquals(30.0, sal.getHp(), 0.001);
		assertEquals(17.0, sal.getAtk(), 0.001);
		assertEquals(15.0, sal.getDef(), 0.001);
		assertEquals(50.0, sal.getSpd(), 0.001);
	}

	@Test
	@DisplayName("Vantagens elementais: Fogo, Água e Grama respeitam multiplicadores x2 e x0.5")
	void testMultiplicadoresElementais() {
		assertEquals(2.0, Pokesal.calcularMultiplicadortype(Type.FOGO, Type.GRAMA), 0.001);
		assertEquals(2.0, Pokesal.calcularMultiplicadortype(Type.AGUA, Type.FOGO), 0.001);
		assertEquals(2.0, Pokesal.calcularMultiplicadortype(Type.GRAMA, Type.AGUA), 0.001);

		assertEquals(0.5, Pokesal.calcularMultiplicadortype(Type.GRAMA, Type.FOGO), 0.001);
		assertEquals(0.5, Pokesal.calcularMultiplicadortype(Type.FOGO, Type.AGUA), 0.001);
		assertEquals(0.5, Pokesal.calcularMultiplicadortype(Type.AGUA, Type.GRAMA), 0.001);
	}

	@Test
	@DisplayName("Mochila do treinador respeita o limite máximo de 2 itens")
	void testLimiteBag() {
		Trainer red = new Trainer("Red");
		red.choosePokesal(null);
		assertNull(red.consumirPrimeiroItem());

		red.addItem(Items.POTION);
		red.addItem(Items.X_ATK);
		red.addItem(Items.X_DEF);

		assertEquals(2, red.getBag().size());
		assertNotNull(red.consumirPrimeiroItem());
		assertEquals(1, red.getUsedItens());
	}

	@Test
	@DisplayName("Repositórios em memória salvam e consultam lutas e Pokesals")
	void testRepositories() {
		BattleRepository bRepo = new BattleRepository();
		bRepo.salvar(new BattleLog("Ash", "Gary", Moves.SURF, 3, "Squirt vs Totodile"));

		assertEquals(1, bRepo.listarTodos().size());
		assertEquals(Optional.of(Moves.SURF), bRepo.findLastHit("Gary", "Ash"));
		assertEquals(Optional.empty(), bRepo.findLastHit("Ash", "Gary"));
		assertEquals(Optional.empty(), bRepo.buscarPorId("1"));

		SalDexRepository sRepo = new SalDexRepository();
		sRepo.salvar("CharSal");

		assertTrue(sRepo.discovered("charsal"));
		assertEquals(Optional.of("CharSal"), sRepo.buscarPorId("CharSal"));
		assertEquals(Optional.empty(), sRepo.buscarPorId("Mew"));
		assertEquals(1, sRepo.listarTodos().size());
	}

}
