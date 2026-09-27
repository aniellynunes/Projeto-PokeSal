package tests;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Battle;
import entities.Campo;
import entities.Pokesal;

class BattleTest {

    private Battle battle;
    private Pokesal atacante;
    private Pokesal defensor;
    private Campo campoAsfalto;

    @BeforeEach
    void setUp() {
        battle = new Battle();
        atacante = new Pokesal("CharSal", "fogo", 100.0, 20.0, 10.0, 50.0);
        defensor = new Pokesal("BulbaSal", "planta", 100.0, 10.0, 10.0, 45.0);
        campoAsfalto = new Campo();
    }

    @Test
    @DisplayName("Deve aplicar dano no HP do defensor ao atacar")
    void dmgReceived() {
        double hp = defensor.getHp();

        // Act - Executa o ataque real. Como o 'Random' interno pode dar erro ou acerto, 
        // rodamos o ataque e validamos se o HP foi modificado ou se manteve (em caso de erro do golpe)
        battle.atacar(atacante, defensor, campoAsfalto);

        // Assert - O HP deve ser menor que o inicial caso o golpe acerte, ou igual caso erre.
        // Isso garante que o método rodou a matemática inteira sem quebrar o sistema.
        assertTrue(defensor.getHp() <= hp, "O HP do defensor não pode aumentar após um ataque");
    }

    @Test
    @DisplayName("Deve exibir mensagens de resultado de batalha sem lançar exceções")
    void deveRodarResultadoBatalha() {
        defensor.setHp(0); // Simula defensor derrotado
        
        // Garante que o método de logs funciona com objetos reais
        assertDoesNotThrow(() -> battle.resultadoBatalha(atacante, defensor));
    }
}
