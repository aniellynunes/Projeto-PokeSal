package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Campo;
import entities.Pokesal;

class CampoTest {

    @Test
    @DisplayName("aplicar vantagem elemental baseado no cenário")
    void testEfeitoTerrenoEstacionamentoUCSal() {
        // Arrange
        Campo randomCampo = new Campo();
        Pokesal pokesalFogo = new Pokesal("CharSal", "fogo", 100.0, 10.0, 10.0, 50.0);
        
        String cenario = randomCampo.getCenario();
        double multiplicador = randomCampo.vantagemCampo(pokesalFogo);

        if (cenario.equals("asfalto quente")) {
            assertEquals(1.15, multiplicador, 0.001, "No asfalto quente, tipo fogo ganha 1.15");
        } else {
            assertEquals(1.0, multiplicador, 0.001, "Fora do asfalto quente, tipo fogo deve receber 1.0");
        }
    }
}
