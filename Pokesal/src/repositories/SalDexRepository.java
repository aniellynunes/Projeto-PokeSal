package repositories;

import interfaces.SalDexDB;
import java.util.*;

/**
 * Implementação em memória (inicia vazia) da PokéDex Acadêmica da UCSal.
 */
public class SalDexRepository implements SalDexDB {
	private final Set<String> catalogados = new HashSet<>();

	/** Salva o nome do Pokesal catalogado. */
	@Override
	public void salvar(String nomePokesal) {
		catalogados.add(nomePokesal.toLowerCase());
	}

	/** Busca um Pokesal catalogado pelo nome. */
	@Override
	public Optional<String> buscarPorId(String id) {
		return catalogados.contains(id.toLowerCase()) ? Optional.of(id) : Optional.empty();
	}

	/** Lista todos os Pokesals catalogados. */
	@Override
	public List<String> listarTodos() {
		return new ArrayList<>(catalogados);
	}

	/** Verifica se o Pokesal já consta no repositório. */
	@Override
	public boolean discovered(String nomePokesal) {
		return catalogados.contains(nomePokesal.toLowerCase());
	}
}
