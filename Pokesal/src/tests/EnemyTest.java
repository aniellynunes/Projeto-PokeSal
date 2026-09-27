package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entities.Enemy;
import entities.Item;
import entities.Pokesal;

class EnemyTest {

    @Test
    @DisplayName("enemy action usar item")
    void actionUseItem() {
        Pokesal pokesalInimigo = new Pokesal("CharSal", "fogo", 100.0, 10.0, 10.0, 50.0);
        Enemy enemy = new Enemy("Rival", pokesalInimigo);
        pokesalInimigo.setHp(5.0);       
        enemy.buyItem(new Item("potion"));
        int action = enemy.action();
        assertEquals(2, action, "O enemy deveria usar o item (action 2)");
    }

    @Test
    @DisplayName("ataca se o hp estivar baixo mas a mochila estiver vazia")
    void LowHpAtk() {
        Pokesal pokesalInimigo = new Pokesal("CharSal", "fogo", 100.0, 10.0, 10.0, 50.0);
        Enemy enemy = new Enemy("Rival", pokesalInimigo);       
        pokesalInimigo.setHp(5.0);
        int action = enemy.action();
        assertEquals(1, action, "o enemy deveria atacar por não haver itens");
    }

    @Test
    @DisplayName("enemy action atacar")
    void atkAction() {
        // Arrange
        Pokesal pokesalInimigo = new Pokesal("CharSal", "fogo", 100.0, 10.0, 10.0, 50.0);
        Enemy enemy = new Enemy("Rival", pokesalInimigo);
        
        pokesalInimigo.setHp(50.0); 
        enemy.buyItem(new Item("potion"));
        int acao = enemy.action();
        assertEquals(1, acao, "enemy prioriza atacar até haver necessidade de cura");
    }
}
