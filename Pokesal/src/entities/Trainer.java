package entities;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private String nome;
    private Pokesal choosedPokesal;
    private List<Item> bag;

    public Trainer(String nome) {
        this.nome = nome;
        this.bag = new ArrayList<>();
    }

    public void choosePokesal(Pokesal pokesal) {
        this.choosedPokesal = pokesal;
    }

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

	
