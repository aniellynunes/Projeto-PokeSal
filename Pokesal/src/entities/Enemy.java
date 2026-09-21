package entities;

/**
 * Representa um treinador oponente (inimigo) no jogo, que herda as características
 * Possui lógica própria de tomada de decisão.
 *
 */
public class Enemy extends Trainer {

  /**
   * Constrói um novo oponente com um nome e um Pokesal inicial já atribuído.
   *
   * @param nome O nome do oponente.
   * @param pokesal, o Pokesal que o oponente utilizará em batalha.
   */
  public Enemy(String nome, Pokesal pokesal) {
    super(nome);
    this.choosePokesal(pokesal);
  }

  /**
   * Determina a ação a ser realizada pelo oponente no turno atual com base em sua inteligência.
   *
   * @return Um valor inteiro representando a ação escolhida, ataque ou item.
   */
  public int action() {
    // usar item
    if (this.getChoosedPokesal().getHp() < 10 && !this.getBag().isEmpty()) {
      return 2;
    }
    // ataca
    return 1;
  }
}
