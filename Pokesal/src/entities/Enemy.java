package entities;

import enums.Moves;

/**
 * Representa um treinador oponente controlado por heurística de decisão.
 */
public class Enemy extends Trainer {

	public Enemy(String nome, Pokesal pokesal) {
		super(nome);
		this.choosePokesal(pokesal);
	}

	/** Decide entre atacar (1) ou usar item de cura (2) quando o HP está baixo. */
	public int action() {
		if (getChoosedPokesal().getHp() < 15.0 && !getBag().isEmpty()) {
			return 2;
		}
		return 1;
	}

	/**
	 * Seleciona o golpe com maior vantagem elemental contra o alvo ou dispara o
	 * Sal-Move.
	 */
	public Moves chooseMove(Pokesal alvo) {
		Pokesal eMove = getChoosedPokesal();
		if (eMove.isSalMoveDisponivel()) {
			eMove.useSalMove();
			return eMove.obterSalMove();
		}

		Moves melhor = eMove.getGolpes().get(0);
		double maiorDano = -1;
		for (Moves g : eMove.getGolpes()) {
			double estimativa = g.getPoder() * Pokesal.calcularMultiplicadortype(g.getTipo(), alvo.getType());
			if (estimativa > maiorDano) {
				maiorDano = estimativa;
				melhor = g;
			}
		}
		return melhor;
	}
}
