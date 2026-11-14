package entities;

import enums.Moves;

/**
 * Entidade que armazena o resultado consolidado de uma batalha,
 * incluindo vencedor, perdedor, golpe final, turnos e Pokésals utilizados.
 */
public class BattleLog {

    private final String winner;
    private final String loser;
    private final Moves lastHit;
    private final int turnos;
    private final String pokesalsUtilizados;

    public BattleLog(String winner, String loser, Moves lastHit,
                     int turnos, String pokesalsUtilizados) {
        this.winner = winner;
        this.loser = loser;
        this.lastHit = lastHit;
        this.turnos = turnos;
        this.pokesalsUtilizados = pokesalsUtilizados;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }

    public Moves getLastHit() {
        return lastHit;
    }

    public int getTurnos() {
        return turnos;
    }

    public String getPokesalsUtilizados() {
        return pokesalsUtilizados;
    }
}
