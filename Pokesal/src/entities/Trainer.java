package entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import enums.Items;

/**
 * Representa um treinador participante do torneio com 1 Pokesal, até 2 itens e
 * sua própria PokéDex pessoal de criaturas catalogadas.
 */
public class Trainer {
	private final String nome;
	private Pokesal choosedPokesal;
	private final List<Items> bag;
	private final Set<String> pSalDex;
	private int itensUtilizados;

	public Trainer(String nome) {
		this.nome = nome;
		this.bag = new ArrayList<>();
		this.pSalDex = new HashSet<>();
		this.itensUtilizados = 0;
	}

	/**
	 * Define o Pokesal ativo do treinador e o registra automaticamente em sua
	 * PokéDex pessoal.
	 */
	public void choosePokesal(Pokesal pokesal) {
		this.choosedPokesal = pokesal;
		if (pokesal != null) {
			this.pSalDex.add(pokesal.getNome());
		}
	}

	/** Adiciona um Pokesal catalogado à PokéDex pessoal do treinador. */
	public void registrarSaldex(String alvo) {
		this.pSalDex.add(alvo);
	}

	/** Adiciona um item à mochila respeitando o limite máximo de 2 unidades. */
	public void addItem(Items item) {
		if (bag.size() < 2) {
			bag.add(item);
		}
	}

	/** Remove e contabiliza o uso do primeiro item disponível na mochila. */
	public Items consumirPrimeiroItem() {
		if (!bag.isEmpty()) {
			itensUtilizados++;
			return bag.remove(0);
		}
		return null;
	}

	public String getNome() {
		return nome;
	}

	public Pokesal getChoosedPokesal() {
		return choosedPokesal;
	}

	public List<Items> getBag() {
		return bag;
	}

	public Set<String> getPSalDex() {
		return pSalDex;
	}

	public int getUsedItens() {
		return itensUtilizados;
	}
}
