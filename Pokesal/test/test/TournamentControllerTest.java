package test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import entities.Enemy;
import entities.Pokesal;
import entities.Trainer;
import enums.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import controller.BattleController;
import controller.TournamentController;

/**
 * Suíte Mockito do {@link TournamentController}. O {@link BattleController} é
 * mockado: cada teste decide, por stubbing, quem vence cada luta, e verifica
 * quem avança de fase.
 *
 * <p>
 * Matchers x valores exatos: quando o teste afirma <i>contra quem</i> o jogador
 * lutou, os objetos são passados exatos (identidade do rival é a regra). Quando
 * só importa a quantidade de lutas, usa-se {@code eq(jogador)} + {@code any(Enemy.class)}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TournamentController com Mockito")
class TournamentControllerTest {

	@Mock
	private BattleController battleController;

	@InjectMocks
	private TournamentController torneio;

	private Trainer jogador;
	private Enemy rival1;
	private Enemy rival2;
	private Enemy rival3;

	@BeforeEach
	void criarParticipantes() {
		jogador = new Trainer("Player");
		jogador.choosePokesal(pokesal("Squirt"));
		rival1 = new Enemy("Cynthia", pokesal("BulbaSal"));
		rival2 = new Enemy("Alder", pokesal("CharSal"));
		rival3 = new Enemy("Lance", pokesal("TotoSal"));
	}

	// =====================================================================
	// Teste obrigatório
	// =====================================================================

	@Test
	@DisplayName("Chaveamento: vencedores avançam e o primeiro algoz encerra a chave")
	void testChaveamentoTorneioAvancaVencedores() {
		when(battleController.iniciarBatalha(jogador, rival1)).thenReturn(jogador);
		when(battleController.iniciarBatalha(jogador, rival2)).thenReturn(jogador);
		when(battleController.iniciarBatalha(jogador, rival3)).thenReturn(rival3);

		Trainer campeao = torneio.startTournament(jogador, List.of(rival1, rival2, rival3));

		assertSame(rival3, campeao);
		// as fases acontecem na ordem do chaveamento
		InOrder fases = inOrder(battleController);
		fases.verify(battleController).iniciarBatalha(jogador, rival1);
		fases.verify(battleController).iniciarBatalha(jogador, rival2);
		fases.verify(battleController).iniciarBatalha(jogador, rival3);
		fases.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Chaveamento: jogador vence todas as fases e é campeão")
	void testJogadorVenceTodasAsFasesECampeao() {
		when(battleController.iniciarBatalha(jogador, rival1)).thenReturn(jogador);
		when(battleController.iniciarBatalha(jogador, rival2)).thenReturn(jogador);
		when(battleController.iniciarBatalha(jogador, rival3)).thenReturn(jogador);

		Trainer campeao = torneio.startTournament(jogador, List.of(rival1, rival2, rival3));

		assertSame(jogador, campeao);
		// só a quantidade de lutas importa: matchers
		verify(battleController, times(3)).iniciarBatalha(eq(jogador), any(Enemy.class));
	}

	@Test
	@DisplayName("Chaveamento: jogador eliminado não enfrenta os próximos rivais")
	void testPlayerEliminadoNoChaveamentoSimplesNLutaMais() {
		when(battleController.iniciarBatalha(jogador, rival1)).thenReturn(rival1);

		Trainer campeao = torneio.startTournament(jogador, List.of(rival1, rival2, rival3));

		assertSame(rival1, campeao);
		verify(battleController, times(1)).iniciarBatalha(jogador, rival1);
		verify(battleController, never()).iniciarBatalha(any(Trainer.class), eq(rival2));
		verify(battleController, never()).iniciarBatalha(any(Trainer.class), eq(rival3));
	}

	@Test
	@DisplayName("Torneio de 8 participantes: 7 lutas contra qualquer rival")
	void testPlayerTemSeteLutasEmTorneioDeOito() {
		List<Enemy> sete = new ArrayList<>();
		for (int i = 1; i <= 7; i++) {
			sete.add(new Enemy("Rival" + i, pokesal("Sal" + i)));
		}
		// qualquer rival perde para o jogador: o rival específico não importa => matcher
		when(battleController.iniciarBatalha(eq(jogador), any(Enemy.class))).thenReturn(jogador);

		Trainer campeao = torneio.startTournament(jogador, sete);

		assertSame(jogador, campeao);
		verify(battleController, times(7)).iniciarBatalha(eq(jogador), any(Enemy.class));
	}

	@Test
	@DisplayName("Número inválido de participantes é rejeitado antes de qualquer luta")
	void testNumeroInvalidoDeParticipantesAnulaTorneio() {
		List<Enemy> doisRivais = List.of(rival1, rival2); // 3 participantes no total

		assertThrows(IllegalArgumentException.class, () -> torneio.startTournament(jogador, doisRivais));
		assertThrows(IllegalArgumentException.class, () -> torneio.startTWLosers(jogador, doisRivais));

		verifyNoInteractions(battleController);
	}

	// =====================================================================
	// Requisito autoral 1 (parte do torneio): Losers Bracket + revanche
	// que dá origem à Mecânica de Vingança
	// =====================================================================

	@Test
	@DisplayName("Losers Bracket: 1ª derrota gera revanche contra o mesmo oponente")
	void testLosersBracketConcedeRevanche() {
		// retornos em sequência: perde a 1ª luta contra rival1 e ganha a revanche
		when(battleController.iniciarBatalha(jogador, rival1)).thenReturn(rival1, jogador);
		when(battleController.iniciarBatalha(jogador, rival2)).thenReturn(jogador);
		when(battleController.iniciarBatalha(jogador, rival3)).thenReturn(jogador);

		Trainer campeao = torneio.startTWLosers(jogador, List.of(rival1, rival2, rival3));

		assertSame(jogador, campeao);
		verify(battleController, times(2)).iniciarBatalha(jogador, rival1);
		verify(battleController, times(1)).iniciarBatalha(jogador, rival2);
		verify(battleController, times(1)).iniciarBatalha(jogador, rival3);
	}

	@Test
	@DisplayName("Losers Bracket: 2ª derrota elimina o jogador")
	void testDerrotaLosersEliminaPlayer() {
		when(battleController.iniciarBatalha(jogador, rival1)).thenReturn(rival1, rival1);

		Trainer campeao = torneio.startTWLosers(jogador, List.of(rival1, rival2, rival3));

		assertSame(rival1, campeao);
		verify(battleController, times(2)).iniciarBatalha(jogador, rival1);
		verify(battleController, never()).iniciarBatalha(any(Trainer.class), eq(rival2));
		verify(battleController, never()).iniciarBatalha(any(Trainer.class), eq(rival3));
	}

	// =====================================================================
	// Helpers
	// =====================================================================

	private static Pokesal pokesal(String nome) {
		return new Pokesal(nome, Type.AGUA, 100, 10.0, 20.0, 40.0);
	}
	
	
	@Test
	@DisplayName("Losers Bracket com 8 participantes: vence invicto sem acionar repescagem")
	void testLosersBracketOitoMatchUpsSeguidas() {
		List<Enemy> sete = new ArrayList<>();
		for (int i = 1; i <= 7; i++) {
			sete.add(new Enemy("Rival" + i, pokesal("Sal" + i)));
		}
		when(battleController.iniciarBatalha(eq(jogador), any(Enemy.class))).thenReturn(jogador);

		Trainer win = torneio.startTWLosers(jogador, sete);

		assertSame(jogador, win);
		verify(battleController, times(7)).iniciarBatalha(eq(jogador), any(Enemy.class));
	}

	
}
