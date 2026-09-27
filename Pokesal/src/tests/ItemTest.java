package tests;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Item;
import entities.Pokesal;

class ItemTest {

    private Pokesal pokesalReal;

    @BeforeEach
    void setUp() {
    	//instanciar um pokesal pra facilitar os testes
        pokesalReal = new Pokesal("CyndaSal", "fogo", 100.0, 100.0, 100.0, 100.0);
    }

    @Test
    @DisplayName("cura da potion")
    void potionHeal() {
        Item potion = new Item("potion");
        pokesalReal.setHp(50.0);

        potion.efeitoItem(pokesalReal);

        assertEquals(70.0, pokesalReal.getHp(), "O HP deveria ser 50 + 20 = 70");
    }

    @Test
    @DisplayName("limitar cura a hpMax do pokesal")
    void healLimit() {
        Item potion = new Item("potion");
        pokesalReal.setHp(95.0);

        potion.efeitoItem(pokesalReal);

        assertEquals(100.0, pokesalReal.getHp(), "hp não pode passar do hpMax");
    }

    @Test
    @DisplayName("x-atk buff")
    void buffXAtk() {
        Item xAtk = new Item("x-atk");

        xAtk.efeitoItem(pokesalReal);

        assertEquals(115.0, pokesalReal.getAtk(), "atk raises in 15");
    }

    @Test
    @DisplayName("x-def buff")
    void buffDef() {
        Item xDef = new Item("x-def");

        xDef.efeitoItem(pokesalReal);

        assertEquals(115.0, pokesalReal.getDef(), "def raises in 15");
    }
    
    @Test
    @DisplayName("x-spd buff")
    void buffSpd() {
        Item xSpd = new Item("x-spd");

        xSpd.efeitoItem(pokesalReal);

        assertEquals(115.0, pokesalReal.getDef(), "spd raises in 15");
    }
}
