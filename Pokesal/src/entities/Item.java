package entities;

/**
 * Representa um item utilizável no jogo que pode aplicar efeitos e alterações de status.
 *
 */
public class Item {

  private String nomeItem;

  /** Nome do item. */
  public Item(String nomeItem) {
    super();
    this.nomeItem = nomeItem;
  }

  public String getNomeItem() {
    return nomeItem;
  }

  /**
   * Aplica o efeito do item fornecido com base no nome do item.
   * Possui os efeitos possíveis para uso.
   *
   */
  public void efeitoItem(Pokesal pokesal) {
    if (this.nomeItem.equalsIgnoreCase("potion")) {
      double newHp = pokesal.getHp() + 20;
      if (newHp > pokesal.getMaxHp()) {
        newHp = pokesal.getMaxHp();
        pokesal.setHp(newHp);
        System.out.println(pokesal.getNome() + " recuperou 20 de HP!");

      } else if (this.nomeItem.equals("x-atk")) {
        int newAtk = (int) (pokesal.getAtk() * 1.15);
        pokesal.setAtk(newAtk);
        System.out.println("O Ataque de " + pokesal.getNome() + " subiu em 15%!");

      } else if (this.nomeItem.equals("x-def")) {
        int newDef = (int) (pokesal.getDef() * 1.15);
        pokesal.setDef(newDef);
        System.out.println("A Defesa de " + pokesal.getNome() + " subiu em 15%!");

      } else if (this.nomeItem.equals("x-spd")) {
        int newSpd = (int) (pokesal.getSpd() * 1.15);
        pokesal.setSpd(newSpd);
        System.out.println("Velocidade de " + pokesal.getNome() + " subiu em 15%!");
      }
    }
  }
}
