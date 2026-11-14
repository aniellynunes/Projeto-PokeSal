package application;

import controller.BattleController;
import controller.TournamentController;
import entities.Enemy;
import entities.Trainer;
import entities.Pokesal;
import enums.Campos;
import enums.Items;
import interfaces.BattleDB;
import interfaces.BattleEngine;
import interfaces.CampoService;
import interfaces.D100;
import interfaces.SalDexDB;
import java.time.Clock;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import repositories.BattleRepository;
import repositories.SalDexRepository;

/**
 * Ponto de entrada da aplicação no terminal. Instancia os colaboradores reais,
 * configura os treinadores e inicia o torneio.
 */
public class Main {

	/**
	 * Configura dependências, cria o jogador e os 7 oponentes e inicia o
	 * campeonato.
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Random random = new Random();

		D100 d100Real = new D100() {
			@Override
			public int rolarDado100() {
				return random.nextInt(100) + 1;
			}

			@Override
			public boolean testarChance(double pct) {
				return (random.nextDouble() * 100.0) < pct;
			}
		};

		CampoService campoReal = hora -> (hora.getHour() >= 6 && hora.getHour() < 18) ? Campos.ASFALTO_QUENTE
				: Campos.CANTEIRO_CENTRAL;

		BattleEngine loggerTerminal = new BattleEngine() {
			@Override
			public void registrarAtaque(String atacante, String golpe) {
				System.out.println("\n>> " + atacante + " usou " + golpe + "!");
			}

			@Override
			public void registrarDano(String alvo, int dmg, boolean crit, boolean errou) {
				if (errou) {
					System.out.println("   " + alvo + " desviou! O ataque errou.");
				} else {
					String msgCrit = crit ? " (ACERTO CRÍTICO!)" : "";
					System.out.println("   " + alvo + " sofreu " + dmg + " de dano" + msgCrit);
				}
			}

			@Override
			public void registrarEfeitoStatus(String pokesal, String status) {
				if (!"NENHUM".equalsIgnoreCase(status)) {
					System.out.println("   [STATUS] " + pokesal + " está " + status);
				}
			}

			@Override
			public void registrarEfeitoTerreno(String terreno, String efeito) {
				System.out.println("   [ARENA - " + terreno + "] " + efeito);
			}

			@Override
			public void registrarUsoItem(String treinador, String item) {
				System.out.println("   [ITEM] " + treinador + " utilizou " + item);
			}
		};

		BattleDB battleDB = new BattleRepository();
		SalDexDB salDexDB = new SalDexRepository();
		BattleController battle = new BattleController(d100Real, campoReal, Clock.systemDefaultZone(), battleDB,
				salDexDB, loggerTerminal);
		TournamentController torneio = new TournamentController(battle);

		List<Pokesal> iniciais = Pokesal.getIniciais();
		System.out.println("=== TORNEIO DO ESTACIONAMENTO UCSAL PITUAÇU ===");
		System.out.println("Escolha seu Pokesal inicial:");
		for (int i = 0; i < iniciais.size(); i++) {
			System.out.println((i + 1) + " - " + iniciais.get(i).getNome());
		}

		int escolha = sc.hasNextInt() ? sc.nextInt() : 1;
		int indice = Math.max(0, Math.min(escolha - 1, iniciais.size() - 1));

		Trainer player = new Trainer("Player");
		player.choosePokesal(Pokesal.clonar(iniciais.get(indice)));
		player.addItem(Items.POTION);
		player.addItem(Items.X_ATK);

		List<Enemy> inimigos = List.of(criarInimigo("Cynthia", iniciais.get(0)), criarInimigo("Alder", iniciais.get(1)),
				criarInimigo("Lance", iniciais.get(2)), criarInimigo("Prof Nogueira", iniciais.get(0)),
				criarInimigo("Dyantha", iniciais.get(1)), criarInimigo("Dandi", iniciais.get(2)),
				criarInimigo("Skyla", iniciais.get(0)));

		var campeao = torneio.startTWLosers(player, inimigos);

		System.out.println("\n=============================================");
		System.out.println("CAMPEÃO DO TORNEIO: " + campeao.getNome());
		System.out.println("SalDex do Player: " + player.getPSalDex());
		System.out.println("Total de Lutas Registradas: " + battleDB.listarTodos().size());
		System.out.println("=============================================");
		sc.close();
	}

	/**
	 * Fábrica auxiliar para instanciar um oponente já equipado com 1 Pokesal e 2
	 * itens.
	 */
	private static Enemy criarInimigo(String nome, Pokesal base) {
		Enemy enemy = new Enemy(nome, Pokesal.clonar(base));
		enemy.addItem(Items.POTION);
		enemy.addItem(Items.X_ATK);
		return enemy;
	}
}
