package application;

import entities.Pokesal;
import entities.Trainer;
import entities.Enemy;
import entities.Item;
import entities.Battle;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Parte principal do Pokesal.
 * Gerencia a interface de terminal com o usuário, registrando o nome do
 * jogador, permitindo a seleção do Pokesal inicial e a escolha de itens gratuitos.
 */
public class Main {

  /** Método principal que executa o jogo. */
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    System.out.print("Digite o seu nome de Treinador: ");
    String nomePlayer = sc.nextLine();

    List<Pokesal> iniciais = Pokesal.getIniciais();
    System.out.println("Escolha seu pokemon inicial");
    System.out.println("1 BulbaSal (Planta)");
    System.out.println("2 CharSal (Fogo)");
    System.out.println("3 SquirtSal (Agua)");
    System.out.println("4 ChikoSal (Planta)");
    System.out.println("5 CyndaSal (Fogo)");
    System.out.println("6 TotoSal (Agua)");
    System.out.println("Digite o número da sua escolha: ");

    int option = sc.nextInt();
    Pokesal choosen = null;
    while (choosen == null) {
      switch (option) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
          choosen = Pokesal.escolha(iniciais.get(option - 1));
          break;
        default:
          System.out.println("Opção inválida! Escolha entre as opcoes de 1 a 6");
          break;
      }
      Trainer player = new Trainer(nomePlayer);
      player.choosePokesal(choosen);
      System.out.println("-> " + player.getChoosedPokesal().getNome() + " adicionado na equipe!");

      System.out.println("pegue 2 itens gratuitamente");
      while (player.getBag().size() < 2) {
        System.out.println("[1] Potion (Cura 20 HP)");
        System.out.println("[2] X-Atk (Buff atk em 15%)");
        System.out.println("[3] X-Def (Buff def em 15%)");
        System.out.println("[4] X-Spd (Buff spd em 15%)");

        int opcaoItem = sc.nextInt();
        Item item = null;

        switch (opcaoItem) {
          case 1:
            item = new Item("potion");
            break;
          case 2:
            item = new Item("x-atk");
            break;
          case 3:
            item = new Item("x-def");
            break;
          case 4:
            item = new Item("x-spd");
            break;
          default:
            System.out.println("Opção inválida! Escolha um número de 1 a 4.");
            break;
        }

        if (item != null) {
          player.getBag().add(item);
          System.out.println("obteve 1" + item.getNomeItem());
        }
      }

      Random r = new Random();
      int randomPokesal = r.nextInt(iniciais.size());
      Pokesal pEnemy = Pokesal.escolha(iniciais.get(randomPokesal));

      Enemy enemy = new Enemy("Alder", pEnemy);

      enemy.getBag().add(new Item("potion"));
      enemy.getBag().add(new Item("potion"));

      randomPokesal = r.nextInt(iniciais.size());
      Pokesal pEnemy2 = Pokesal.escolha(iniciais.get(randomPokesal));
      Enemy enemy2 = new Enemy("Cynthia", pEnemy2);
      enemy2.getBag().add(new Item("potion"));
      enemy2.getBag().add(new Item("potion"));

      System.out.println("\nPreparando o campo de batalha...");
      Battle b = new Battle();

      b.iniciarBatalha(player, enemy);
      b.iniciarBatalha(player, enemy2);

    }
    sc.close();
  }
}
