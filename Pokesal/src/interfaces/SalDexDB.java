package interfaces;

/**
 * Repositório global da PokéDex Acadêmica da UCSal (armazena nomes dos Pokesals catalogados).
 */
public interface SalDexDB extends Crud<String, String> {
    /** Verifica se o Pokesal já foi catalogado anteriormente no torneio. */
    boolean discovered(String nomePokesal);
}
