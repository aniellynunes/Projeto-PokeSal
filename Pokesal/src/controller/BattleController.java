package controller;

import entities.*;
import enums.Campos;
import enums.Items;
import enums.Moves;
import enums.Type;
import interfaces.*;
import java.time.Clock;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controlador principal do Game Loop de batalha estilo GBA. Coordena turnos,
 * cálculos de combate, eventos de clima e persistência de resultados.
 */
public class BattleController {

	private final D100 d100;
	private final CampoService bField;
	private final Clock clock;
	private final BattleDB battleDB;
	private final SalDexDB salDex;
	private final BattleEngine log;
	private final Scanner sc;

	public BattleController(D100 d100, CampoService bField, Clock clock, BattleDB battleDB, SalDexDB salDex,
			BattleEngine log) {
		this.d100 = d100;
		this.bField = bField;
		this.clock = clock;
		this.battleDB = battleDB;
		this.salDex = salDex;
		this.log = log;
		this.sc = new Scanner(System.in);
	}

	/** Gerencia o loop de turnos até que um dos Pokesals fique sem HP. */
	public Trainer iniciarBatalha(Trainer player, Enemy enemy) {
		LocalTime horaAtual = LocalTime.now(clock);
		Campos campo = bField.getField(horaAtual);

		Pokesal pPlayer = player.getChoosedPokesal();
		Pokesal pEnemy = enemy.getChoosedPokesal();
		List.of(pPlayer, pEnemy).forEach(Pokesal::fullRestore);

		int turnos = 0;
		Moves ultimoGolpe = pPlayer.getGolpes().get(0);

		while (pPlayer.getHp() > 0 && pEnemy.getHp() > 0) {
			turnos++;
			System.out
					.print("\n" + enemy.getNome() + "' " + pEnemy.getNome() + pEnemy.getHp() + "/" + pEnemy.getMaxHp());
			System.out.print(
					"\n" + player.getNome() + "' " + pPlayer.getNome() + pPlayer.getHp() + "/" + pPlayer.getMaxHp());
			List.of(pPlayer, pEnemy).forEach(this::selfDmg);
			if (pPlayer.getHp() <= 0 || pEnemy.getHp() <= 0) {
				break;
			}

			int acaoPlayer = playerAction(player);
			int acaoEnemy = enemy.action();

			Moves movePlayer = (acaoPlayer == 1) ? selectMove(player) : null;
			Moves moveEnemy = (acaoEnemy == 1) ? enemy.chooseMove(pPlayer) : null;

			Items itemPlayer = (acaoPlayer == 2) ? usarItem(player) : null;
			Items itemEnemy = (acaoEnemy == 2) ? usarItem(enemy) : null;

			boolean playerFirst = pPlayer.getSpd() >= pEnemy.getSpd();
			if (playerFirst) {
				ultimoGolpe = processarTurno(player, enemy, acaoPlayer, movePlayer, campo, ultimoGolpe);
				if (pEnemy.getHp() > 0) {
					ultimoGolpe = processarTurno(enemy, player, acaoEnemy, moveEnemy, campo, ultimoGolpe);
				}
			} else {
				ultimoGolpe = processarTurno(enemy, player, acaoEnemy, moveEnemy, campo, ultimoGolpe);
				if (pPlayer.getHp() > 0) {
					ultimoGolpe = processarTurno(player, enemy, acaoPlayer, movePlayer, campo, ultimoGolpe);
				}
			}

			aplicarEfeitosTerrenoFimTurno(pPlayer, pEnemy, campo, horaAtual);

			Optional.ofNullable(itemPlayer).ifPresent(item -> log.registrarUsoItem(player.getNome(), item.name()));
			Optional.ofNullable(itemEnemy).ifPresent(item -> log.registrarUsoItem(enemy.getNome(), item.name()));
		}

		Trainer winner = (pPlayer.getHp() > 0) ? player : enemy;
		Trainer loser = winner.equals(player) ? enemy : player;

		registrarSalDex(winner, loser.getChoosedPokesal());
		saveResult(winner, loser, ultimoGolpe, turnos);

		return winner;
	}

	/** Encapsula a execução do ataque caso a ação escolhida seja ofensiva. */
	private Moves processarTurno(Trainer atacante, Trainer defensor, int acao, Moves golpe, Campos campo,
			Moves ultimoGolpe) {
		if (acao == 1 && golpe != null) {
			executarAtaque(atacante, defensor, golpe, campo);
			return golpe;
		}
		return ultimoGolpe;
	}

	/** Lê a ação principal escolhida pelo jogador no turno. */
	private int playerAction(Trainer player) {
		System.out.print("\n[" + player.getNome() + "] 1-Atacar | 2-Usar Item: ");
		return sc.hasNextInt() ? sc.nextInt() : 1;
	}

	/** Calcula precisão, dano, multiplicadores elementais, Vingança e crítico. */
	public void executarAtaque(Trainer tAtacante, Trainer tDefensor, Moves golpe, Campos campo) {
		Pokesal atacante = tAtacante.getChoosedPokesal();
		Pokesal defensor = tDefensor.getChoosedPokesal();

		log.registrarAtaque(atacante.getNome(), golpe.name());

		double chanceAcerto = golpe.getPrecisao() * atacante.obterVantagemTerreno(campo);
		if (d100.rolarDado100() > chanceAcerto) {
			log.registrarDano(defensor.getNome(), 0, false, true);
			atacante.registrarErroAtaque();
			return;
		}

		double danoBase = golpe.getPoder() * (20.0 / (20.0 + defensor.getDef()))
				* Pokesal.calcularMultiplicadortype(golpe.getTipo(), defensor.getType())
				* atacante.obterVantagemTerreno(campo);

		double multiplicadorVinganca = battleDB.findLastHit(tDefensor.getNome(), tAtacante.getNome())
				.filter(trauma -> trauma.equals(golpe)).map(trauma -> 0.5).orElse(1.0);

		boolean critico = d100.testarChance(6.25);
		double danoCalculado = danoBase * multiplicadorVinganca * (critico ? 1.5 : 1.0);

		int danoFinal = (int) Math.round(danoCalculado);
		defensor.receberDano(danoFinal);

		String statusAtual = Optional.ofNullable(defensor.getEfeitoStatus()).filter(s -> !s.equalsIgnoreCase("NENHUM"))
				.orElse("NENHUM");

		log.registrarDano(defensor.getNome(), danoFinal, critico, false);
		log.registrarEfeitoStatus(defensor.getNome(), statusAtual);

		if (golpe.equals(atacante.obterSalMove())) {
			registrarSalDex(tAtacante, atacante);
		}
		atacante.registrarAcertoAtaque();
	}

	/** Consome o item da mochila e aplica o modificador no Pokesal. */
	private Items usarItem(Trainer treinador) {
		Items item = treinador.consumirPrimeiroItem();
		if (item != null) {
			treinador.getChoosedPokesal().aplicarItem(item);
		}
		return item;
	}

	/** Avalia o clima e o horário para aplicar efeitos passivos ao fim do turno. */
	private void aplicarEfeitosTerrenoFimTurno(Pokesal p1, Pokesal p2, Campos campo, LocalTime hora) {
		boolean ehDia = hora.getHour() >= 6 && hora.getHour() < 18;
		List<Pokesal> combatentes = List.of(p1, p2);

		if (campo.equals(Campos.ASFALTO_QUENTE)) {
			if (ehDia) {
				combatentes.forEach(p -> p.receberDano(5));
				log.registrarEfeitoTerreno(campo.name(), "Dano térmico aplicado");
			} else {
				log.registrarEfeitoTerreno(campo.name(), "Inativo durante a noite");
			}
		} else if (campo.equals(Campos.CANTEIRO_CENTRAL)) {
			combatentes.stream().filter(p -> p.getType().equals(Type.GRAMA))
					.forEach(p -> p.curar(Math.max(1, Math.floor(p.getMaxHp() * 0.05))));
			log.registrarEfeitoTerreno(campo.name(), "Cura vegetal aplicada");
		} else {
			log.registrarEfeitoTerreno(campo.name(), "Sem efeito passivo");
		}
	}

	/**
	 * Aplica o dano de recuo caso a barra de estresse tenha estourado no turno
	 * anterior.
	 */
	private void selfDmg(Pokesal pokesal) {
		if (pokesal.getStressAtivo()) {
			pokesal.receberDano(Math.round(pokesal.getAtk() * 0.5));
			pokesal.setStressAtivo(false);
		}
	}

	/** Registra pokesal na salDex. */
	private void registrarSalDex(Trainer player, Pokesal alvo) {
		String nomeAlvo = alvo.getNome();
		player.registrarSaldex(nomeAlvo);

		try {
			boolean registrado = salDex.discovered(nomeAlvo)
					|| salDex.listarTodos().stream().anyMatch(nome -> nome.equalsIgnoreCase(nomeAlvo));

			if (!registrado) {
				salDex.salvar(nomeAlvo);
			}
		} catch (Exception ignored) {
		}
	}

	/** Salva o resultado da batalha */
	private void saveResult(Trainer winner, Trainer loser, Moves lastHit, int turnos) {
		try {
			String pokesals = winner.getChoosedPokesal().getNome() + " vs " + loser.getChoosedPokesal().getNome();
			BattleLog log = new BattleLog(winner.getNome(), loser.getNome(), lastHit, turnos, pokesals);
			battleDB.salvar(log);
		} catch (Exception e) {
			System.err.println("Erro ao salvar resultado no repositório, prosseguindo torneio.");
		}
	}

	/** Exibe os golpes do pokesal */
	private Moves selectMove(Trainer player) {
		Pokesal p = player.getChoosedPokesal();
		List<Moves> golpes = p.getGolpes();

		System.out.println("\n--- GOLPES DE " + p.getNome() + " ---");
		for (int i = 0; i < golpes.size(); i++) {
			System.out.println((i + 1) + " - " + golpes.get(i).name());
		}

		if (p.isSalMoveDisponivel()) {
			System.out.println("3 - [SAL-MOVE] " + p.obterSalMove().name());
		}

		int escolha = sc.hasNextInt() ? sc.nextInt() : 1;
		if (escolha == 3 && p.isSalMoveDisponivel()) {
			p.useSalMove();
			return p.obterSalMove();
		}
		return golpes.get(Math.max(0, Math.min(escolha - 1, golpes.size() - 1)));

	}
}
