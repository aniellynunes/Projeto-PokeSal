package enums;

/**
 * Catálogo simplificado de golpes elementais (2 ataques regulares + 1 Sal-Move por tipo).
 */
public enum Moves {
    // Fogo
    EMBER(40, 100, Type.FOGO),
    FLAMETHROWER(90, 100, Type.FOGO),
    FIRE_BLAST(110, 85, Type.FOGO),

    // Água
    WATER_GUN(40, 100, Type.AGUA),
    SURF(90, 100, Type.AGUA),
    HYDRO_PUMP(110, 80, Type.AGUA),

    // Grama
    VINE_WHIP(40, 100, Type.GRAMA),
    ENERGY_BALL(90, 100, Type.GRAMA),
    SOLAR_BEAM(120, 100, Type.GRAMA);

    private final int poder;
    private final int precisao;
    private final Type tipo;

    Moves(int poder, int precisao, Type tipo) {
        this.poder = poder;
        this.precisao = precisao;
        this.tipo = tipo;
    }

    public int getPoder() { return poder; }
    public int getPrecisao() { return precisao; }
    public Type getTipo() { return tipo; }
}
