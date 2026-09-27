package tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Pokesal;

class PokesalTest {

    private Pokesal fireType;
    private Pokesal waterType;
    private Pokesal grassType;

    @BeforeEach
    void setUp() {
        fireType = new Pokesal("CharSal", "fogo", 100.0, 15.0, 10.0, 50.0);
        waterType = new Pokesal("SquirtSal", "agua", 100.0, 12.0, 15.0, 40.0);
        grassType = new Pokesal("BulbaSal", "planta", 100.0, 10.0, 12.0, 45.0);
    }

    @Test
    @DisplayName("carrega o pokesal no estado inicial")
    void initAttributes() {
        assertAll("Atributos",
            () -> assertEquals("CharSal", fireType.getNome()),
            () -> assertEquals(100.0, fireType.getHp()),
            () -> assertNull(fireType.getEfeitoStatus())
        );
    }

    @Test
    @DisplayName("matriz de vantagem elemental")
    void MultiplierTipo() {
        assertAll("Vantagens",
            () -> assertEquals(2.0, fireType.multiplicadorTipo("fogo", "planta")),
            () -> assertEquals(2.0, waterType.multiplicadorTipo("agua", "fogo")),
            () -> assertEquals(2.0, grassType.multiplicadorTipo("planta", "agua"))
        );

        assertAll("Desvantagens",
            () -> assertEquals(0.5, grassType.multiplicadorTipo("planta", "fogo")),
            () -> assertEquals(0.5, fireType.multiplicadorTipo("fogo", "agua"))
        );
    }

    @Test
    @DisplayName("reduzir hp baseado em dano")
    void deveReduzirHpAoReceberDano() {
        fireType.receberDano(30.0);
        assertEquals(70.0, fireType.getHp());
    }

    @Test
    @DisplayName("clonar pokesal escolhido")
    void clonePokesal() {
        Pokesal clone = Pokesal.escolha(fireType);
        assertNotSame(fireType, clone);
        assertEquals(fireType.getNome(), clone.getNome());
    }

    @Test
    @DisplayName("retorna a lista de iniciais")
    void listaIniciais() {
        List<Pokesal> lista = Pokesal.getIniciais();
        assertEquals(6, lista.size());
    }
}
