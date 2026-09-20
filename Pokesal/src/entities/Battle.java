package entities;

import java.util.Random;
import java.util.Scanner;

public class Battle {

  private final Random ra = new Random();
  private final Scanner sc = new Scanner(System.in);

  public void iniciarBatalha(Trainer player, Enemy enemy) {
    Campo campo = new Campo();
    Pokesal pPlayer = player.getChoosedPokesal();
    Pokesal pEnemy = enemy.getChoosedPokesal();

    System.out.println("Arena Sorteada: " + campo.getCenario());

    do {

      System.out.println(enemy.getNome() + "'s " + pEnemy.getNome() + "\n " + (int) pEnemy.getHp() + "/" + (int) pEnemy.getMaxHp());
      System.out.println(player.getNome() + "'s " + pPlayer.getNome() + "\n " + (int) pPlayer.getHp() + "/"	+ (int) pPlayer.getMaxHp());

      // round actions
      int playerAction = playerEscolha(player);
      int enemyAction = enemy.action();

      if (playerAction == 2) {
        usarItem(player, pPlayer);
      } else {
        System.out.println(player.getNome() + "'s " + pPlayer.getNome() + " vai atacar");
      }
      if (enemyAction == 2) {
        usarItem(enemy, pEnemy);
      } else {
        System.out.println(enemy.getNome() + "'s " + pEnemy.getNome() + "vai atacar");
      }

      boolean greaterSpd = pPlayer.getSpd() >= pEnemy.getSpd();

      // attack order
      if (greaterSpd) {
        if (playerAction == 1 && pPlayer.getHp() > 0 && pEnemy.getHp() > 0) {
          atacar(pPlayer, pEnemy, campo);
        }
        if (enemyAction == 1 && pEnemy.getHp() > 0 && pPlayer.getHp() > 0) {
          atacar(pEnemy, pPlayer, campo);
        }
      } else {
        if (enemyAction == 1 && pEnemy.getHp() > 0 && pPlayer.getHp() > 0) {
          atacar(pEnemy, pPlayer, campo);
        }
        if (playerAction == 1 && pPlayer.getHp() > 0 && pEnemy.getHp() > 0) {
          atacar(pPlayer, pEnemy, campo);
        }
      }

      // round end
      curarFimDeTurno(pPlayer, campo);
      curarFimDeTurno(pEnemy, campo);

    } while (pPlayer.getHp() > 0 && pEnemy.getHp() > 0); // battle condition

    // battle log
    resultadoBatalha(pPlayer, pEnemy);
    pPlayer.heal();
  }

  public void atacar(Pokesal atacante, Pokesal defensor, Campo campo) {
    int roll = ra.nextInt(20) + 1;
    double buffHit = buffPrecisao(campo, atacante);
    roll *= buffHit;
    int dano = (int) atacante.getAtk();
    double vantagemTipo = atacante.multiplicadorTipo(atacante.getTipo(), defensor.getTipo());
    double buffCampo = buffDmgCampo(campo, atacante);
    double reduzirDano = 20.0 / (20.0 + defensor.getDef());
    double danoFinal = dano * reduzirDano * vantagemTipo * buffCampo;

    if (roll > 10) {
      if (roll >= 18) {
        danoFinal *= 1.5;
        System.out.println("DANO CRITICO! \n" + atacante.getNome() + " causou " + (int) danoFinal + " de dano");
      } else {
        System.out.println(atacante.getNome() + " causou " + (int) danoFinal + " de dano");
      }
    } else {
      danoFinal = 0;
      System.out.println(atacante.getNome() + " errou o ataque");

    }

    defensor.receberDano(danoFinal);

  }

  // fire type bonus
  private double buffDmgCampo(Campo campo, Pokesal p) {
    if (campo.getCenario().equals("asfalto quente") && p.getTipo().equalsIgnoreCase("fogo")) {
      return 1.15;
    }
    return campo.vantagemCampo(p);
  }

  // water type bonus on hit success rate
  private double buffPrecisao(Campo campo, Pokesal p) {
    if (campo.getCenario().equals("poca de chuva") || campo.getCenario().equalsIgnoreCase("piso molhado") && p.getTipo().equalsIgnoreCase("agua")) {
      return 1.1;
    }
    return campo.vantagemCampo(p);
  }

  // grass type buff--> healing after round ends
  private void curarFimDeTurno(Pokesal p, Campo campo) {
    if (p.getHp() > 0 && campo.getCenario().equals("canteiro central") && p.getTipo().equals("planta")) {
      int cura = (int) (p.getMaxHp() * 0.05);
      if (cura <= 0) {
        cura = 1;
        double newHp = p.getHp() + cura;
        if (newHp > p.getMaxHp()) {
          newHp = p.getMaxHp();
        }
        p.setHp(newHp);
      }
    }
  }

  // item use logic
  private void usarItem(Trainer treinador, Pokesal pokesal) {
    if (!treinador.getBag().isEmpty()) {
      Item item = treinador.getBag().remove(0);
      item.efeitoItem(pokesal);
      System.out.println(treinador.getNome() + " usou " + item.getNomeItem());

    }
  }

  // battle options
  private int playerEscolha(Trainer player) {
    while (true) {
      System.out.println("\n 1-Atacar | 2-Usar Item");
      if (sc.hasNextInt()) {
        int input = sc.nextInt();
        if (input == 1 || input == 2) {
          return input;
        } else {
          System.out.println("escolha invalida");
          sc.next();
        }
      }
    }
  }

  public void resultadoBatalha(Pokesal pPlayer, Pokesal pEnemy) {
    if (pPlayer.getHp() <= 0) {
      System.out.println("O Pokesal adversário venceu a batalha.");
    } else if (pEnemy.getHp() <= 0) {
      System.out.println("Você venceu a batalha.");
    }
  }
}
