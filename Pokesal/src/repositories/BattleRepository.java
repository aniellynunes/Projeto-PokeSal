package repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entities.BattleLog;
import enums.Moves;
import interfaces.BattleDB;

/**
 * repositório de lutas e rivalidade
 */
public class BattleRepository implements BattleDB {
	private final List<BattleLog> lutas = new ArrayList<>();

	/** Registra o resultado da batalha. */
	@Override
	public void salvar(BattleLog entidade) {
		lutas.add(entidade);
	}

	/** Mostra o historico do torneio. */
	@Override
	public List<BattleLog> listarTodos() {
		return new ArrayList<>(lutas);
	}

	/**
	 * Busca o golpe que finalizou o perdedor em confronto anterior contra o mesmo
	 * oponente.
	 */
	@Override
	public Optional<Moves> findLastHit(String loser, String winner) {
		for (int i = lutas.size() - 1; i >= 0; i--) {
			BattleLog l = lutas.get(i);
			if (l.getLoser().equalsIgnoreCase(loser) && l.getWinner().equalsIgnoreCase(winner)) {
				return Optional.ofNullable(l.getLastHit());
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<BattleLog> buscarPorId(String id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
