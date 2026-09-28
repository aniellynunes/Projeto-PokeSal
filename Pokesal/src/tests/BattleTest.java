package tests;


import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Battle;
import entities.Campo;
import entities.Item;
import entities.Pokesal;
import entities.Trainer;

class BattleTest {

    private Battle battle;
    private Pokesal atacante;
    private Pokesal defensor;
    private Campo campoAsfalto;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        battle = new Battle();
        atacante = new Pokesal("CharSal", "fogo", 100.0, 20.0, 10.0, 50.0);
        defensor = new Pokesal("BulbaSal", "planta", 100.0, 10.0, 10.0, 45.0);
        campoAsfalto = new Campo();
        trainer=new Trainer("Ash");
    }
    
    
    @Test
    @DisplayName("o pokesal mais rápido ataca primeiro")
    void testOrdemAtaquePorSpd() {   
        String firstAttack=null;
        
        if(atacante.getSpd()>defensor.getSpd()) {
        	firstAttack=atacante.getNome();
        }else {
        	firstAttack=defensor.getNome();
        }     
        assertEquals(atacante, firstAttack);
    }
    
    @Test
    @DisplayName("Testa se a quantidade de itens usados é maior que o limite da bolsa")
    void testUsoLimiteDeItemExcedido() {
    	if (!trainer.getBag().isEmpty()) {
    		Item item = trainer.getBag().remove(0);
    		item.efeitoItem(atacante);
    		System.out.println(trainer.getNome() + " usou " + item.getNomeItem());
    	} else { 
    		throw new IllegalStateException("não tem item para ser usado!");
    	}

    }
    
    @Test
    @DisplayName("Testar atributos de Pokesal")
    void testCalculoDanoBoundAnyValues() {
    	atacante.getNome();
    	atacante.getAtk(); 
    	atacante.getDef(); 
    	atacante.getHp(); 
    	atacante.getMaxHp();
    }
    
    @Test
    @DisplayName("Deve aplicar dano no HP do defensor ao atacar")
    void dmgReceived() {
        double hp = defensor.getHp();
        battle.atacar(atacante, defensor, campoAsfalto);
        assertTrue(defensor.getHp() <= hp, "O HP do defensor não pode aumentar após um ataque");
    }
    
    @Test
    @DisplayName("Deve exibir mensagens de resultado de batalha sem lançar exceções")
    void efeitoStatus() {        
        assertDoesNotThrow(() -> atacante.setEfeitoStatus("PAR"), atacante.getEfeitoStatus());
        assertDoesNotThrow(() -> defensor.setEfeitoStatus("BRN"), defensor.getEfeitoStatus());
    }
    
   
    @Test
    @DisplayName("Deve exibir mensagens de resultado de batalha sem lançar exceções")
    void resultadoBatalha() {
        defensor.setHp(0); 
        assertDoesNotThrow(() -> battle.resultadoBatalha(atacante, defensor));
    }
}
