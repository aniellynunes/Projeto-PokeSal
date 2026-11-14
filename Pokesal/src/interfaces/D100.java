package interfaces;

/**
 * Isola a geração de números aleatórios das regras de negócio (Inversão de Dependência).
 */
public interface D100 {
    /** Rola um dado de 1 a 100 para verificação de precisão. */
    int rolarDado100();
    /** Avalia se um evento probabilístico (ex: 6.25% de crítico) ocorreu. */
    boolean testarChance(double porcentagem);
}
