package interfaces;

/**
 * Registra os eventos de combate na ordem exata exigida pelo fluxo do turno.
 */
public interface BattleEngine {
    /** Registra a intenção de ataque. */
    void registrarAtaque(String atacante, String golpe);
    /** Registra o dano aplicado, se foi crítico ou se o golpe errou. */
    void registrarDano(String alvo, int dano, boolean critico, boolean errou);
    /** Registra alterações de status no Pokesal. */
    void registrarEfeitoStatus(String pokesal, String status);
    /** Registra efeitos climáticos ou de arena aplicados ao fim do turno. */
    void registrarEfeitoTerreno(String terreno, String efeito);
    /** Registra o consumo de itens durante o turno. */
    void registrarUsoItem(String treinador, String item);
}
