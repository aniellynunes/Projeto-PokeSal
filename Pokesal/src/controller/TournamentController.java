package controller;

import entities.Enemy;
import entities.Trainer;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o torneio de 4 ou 8 treinadores onde o Player enfrenta todos os oponentes,
 * incluindo sistema de repescagem (Losers Bracket) para ativar a mecânica de Vingança.
 */
public class TournamentController {

    private final BattleController battleController;

    public TournamentController(BattleController battleController) {
        this.battleController = battleController;
    }

    /**
     * Executa o torneio eliminatório simples de 4 ou 8 participantes (1 Player + 3 ou 7 Enemies).
     */
    public Trainer startTournament(Trainer player, List<Enemy> inimigos) {
        int totalParticipantes = inimigos.size() + 1;
        if (totalParticipantes != 4 && totalParticipantes != 8) {
            throw new IllegalArgumentException("O torneio exige exatamente 4 ou 8 treinadores no total.");
        }

        for (Enemy oponente : inimigos) {
            Trainer vencedor = battleController.iniciarBatalha(player, oponente);
            if (vencedor != player) {
                return vencedor; // Player eliminado na chave simples
            }
        }
        return player;
    }

    /**
	 * Executa o torneio com Losers Bracket (repescagem): Se o Player perder uma
	 * luta, cai para a Losers Bracket e enfrenta o mesmo oponente novamente na
	 * revanche, ativando a mecânica de Vingança.
	 */
    public Trainer startTWLosers(Trainer player, List<Enemy> inimigos) {
        int totalParticipantes = inimigos.size() + 1;
        if (totalParticipantes != 4 && totalParticipantes != 8) {
            throw new IllegalArgumentException("O torneio exige exatamente 4 ou 8 treinadores no total.");
        }

        boolean caiuParaLosers = false;
        List<Enemy> filaOponentes = new ArrayList<>(inimigos);

        for (int i = 0; i < filaOponentes.size(); i++) {
            Enemy oponenteAtual = filaOponentes.get(i);
            Trainer vencedor = battleController.iniciarBatalha(player, oponenteAtual);

            if (vencedor != player) {
                if (!caiuParaLosers) {
                    // Primeira derrota: Player cai para a Losers Bracket e ganha direito à revanche
                    caiuParaLosers = true;
                    System.out.println(player.getNome() + " caiu para a LOSERS BRACKET! Revanche ativada contra " + oponenteAtual.getNome() + "!");
                    i--; // Repete o confronto contra o mesmo algoz (Mecânica de Vingança)
                } else {
                    // Segunda derrota: Player é eliminado definitivamente do torneio
                    return oponenteAtual;
                }
            }
        }
        return player;
    }
}
