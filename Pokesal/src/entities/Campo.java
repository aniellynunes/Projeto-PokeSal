package entities;

import java.util.Random;

/**
 * Representa o campo/cenário onde a batalha acontece.
 * O cenário é gerado aleatoriamente e gera vantagens ou
 * recuperação de vida dependendo do tipo do pokesal.
 *
 */
public class Campo {
  private String cenario;

  /**
   * Instância o Campo, sorteando aleatoriamente um cenário,
   * entre as opções disponíveis.
   */
  public Campo() {
    String[] cenarios = { "asfalto quente", "poca de chuva", "piso molhado", "canteiro central" };
    Random r = new Random();
    this.cenario = cenarios[r.nextInt(cenarios.length)];
  }

  /**
   * Avalia a vantagem de campo para um determinado Pokesal com base em seu tipo elemental.
   *
   * @param p recebe o benefício do campo.
   * @return O multiplicador de vantagem dado pelo campo.
   */
  public double vantagemCampo(Pokesal p) {
    if (cenario.equals("asfalto quente") && p.getTipo().equals("fogo")) {
      return 1.15;
    }
    if ((cenario.equals("poca de chuva") || cenario.equals("piso molhado")) && p.getTipo().equals("agua")) {
      return 1.10;
    }

    if (cenario.equals("canteiro Central") && p.getTipo().equals("planta")) {
      int cura = (int) (p.getHp() * 0.05);
      p.setHp(Math.min(p.getMaxHp(), p.getHp() + cura));
      System.out.println(p.getNome() + " recuperou " + cura + " de HP pelo Canteiro Central!");
    }
    return 1;

  }

  public String getCenario() {
    return cenario;
  }
}
