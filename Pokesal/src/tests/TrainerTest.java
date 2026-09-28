package tests;



import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Item;
import entities.Pokesal;
import entities.Trainer;

class TrainerTest {

    private Trainer trainer;
    private Pokesal pokesalReal;

    @BeforeEach
    void setUp() {
        trainer = new Trainer("Ash");
        pokesalReal = new Pokesal("CharSal", "fogo", 100.0, 15.0, 10.0, 50.0);
    }

    @Test
    @DisplayName("inicializar a instancia trainer com a bolsa vazia")
    void deveInicializarTreinadorCorretamente() {
        assertAll("Estado Inicial",
            () -> assertEquals("Ash", trainer.getNome()),
            () -> assertTrue(trainer.getBag().isEmpty()),
            () -> assertNull(trainer.getChoosedPokesal())
        );
    }

    @Test
    @DisplayName("armazenar pokesal do trainer")
    void linkPokesalTrainer() {
        trainer.choosePokesal(pokesalReal);
        assertEquals("CharSal", trainer.getChoosedPokesal().getNome());
    }

    @Test
    @DisplayName("limite de espaco na bolsa")
    void testLimiteDeItensExcedido() {
    	Item i1 = new Item("potion"); 
        Item i2 = new Item("x-atk"); 
        Item i3 = new Item("x-def"); 

        trainer.buyItem(i1); 
        trainer.buyItem(i2); 
        trainer.buyItem(i3); 

        assertEquals(2, trainer.getBag().size()); 
        assertFalse(trainer.getBag().contains(i3)); 

        if (trainer.getBag().size() > 2) {
            fail("A bolsa está cheia!");
        }
    }
    
}
