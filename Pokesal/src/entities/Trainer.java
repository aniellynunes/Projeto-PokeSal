package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um treinador no jogo, responsável por seu Pokesal escolhido.
 *
 */
public class Trainer {
  private String nome;
  private Pokesal choosedPokesal;
  private List<Item> bag;

  /**
   * Nome do treinador.
   */
  public Trainer(String nome) {
    this.nome = nome;
    this.bag = new ArrayList<>();
  }

  /**
   * O Pokesal atualmente selecionado pelo treinador.
   */
  public void choosePokesal(Pokesal pokesal) {
    this.choosedPokesal = pokesal;
  }

  /** Lista de itens contidos na mochila do treinador. */
  public void buyItem(Item item) {
    if (bag.size() < 2) {
      bag.add(item);
    } else {
      System.out.println("A mochila está cheia!");
    }
  }

  public String getNome() {
    return nome;
  }

  public Pokesal getChoosedPokesal() {
    return choosedPokesal;
  }

  public List<Item> getBag() {
    return bag;
  }
}
