package interfaces;

import entities.BattleLog;
import enums.Moves;
import java.util.Optional;

/**
 * Repositório que armazena resultados de batalhas, histórico de rivalidades e ranking.
 */
public interface BattleDB extends Crud<BattleLog, String> {
    /** Busca o golpe que derrotou o oponente. */
    Optional<Moves> findLastHit(String loser, String winner);
}
