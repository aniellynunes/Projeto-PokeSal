# Diagrama de Sequência UML — Turno de Batalha

```mermaid
sequenceDiagram
    actor Jogador
    participant BC as BattleController
    participant P as Pokesal
    participant BR as BattleRepository

    Jogador->>BC: Escolhe golpe
    BC->>P: Executa ataque
    P-->>BC: Retorna resultado
    BC->>BC: Calcula dano
    BC->>BR: Registra resultado
    BC-->>Jogador: Exibe resultado
