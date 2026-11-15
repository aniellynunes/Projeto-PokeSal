@startuml
title PokeSal — Turno de Batalha
actor Player
participant Enemy
participant BattleController
participant "CampoService / Clock" as Campo
participant Pokesal
participant BattleEngine
database BattleDB
database SalDexDB

Player -> BattleController: iniciarBatalha(player, enemy)
BattleController -> Campo: getField(horaAtual)
Campo --> BattleController: campo
BattleController -> Pokesal: fullRestore() [ambos]
loop enquanto HP dos dois > 0
  BattleController -> Pokesal: selfDmg() [se stressAtivo]
  BattleController -> Player: playerAction()
  Player --> BattleController: ação
  BattleController -> Enemy: action()
  Enemy --> BattleController: ação
  opt ação de ataque
    BattleController -> Player: selectMove()
    Player --> BattleController: golpe
  end
  note over BattleController,Pokesal: A ordem dos ataques é definida pela velocidade (SPD).
  alt Player mais rápido ou empate
    BattleController -> BattleController: processarTurno(Player)
    opt Enemy ainda tem HP
      BattleController -> BattleController: processarTurno(Enemy)
    end
  else Enemy mais rápido
    BattleController -> BattleController: processarTurno(Enemy)
    opt Player ainda tem HP
      BattleController -> BattleController: processarTurno(Player)
    end
  end
  opt ataque executado
    BattleController -> BattleEngine: registrarAtaque() / registrarDano()
    BattleController -> BattleDB: findLastHit() [Vingança]
    BattleController -> Pokesal: aplicar dano
  end
  BattleController -> Pokesal: aplicar efeitos do terreno
  BattleController -> BattleEngine: registrar efeitos e itens
end
BattleController -> BattleController: definir vencedor e perdedor
BattleController -> SalDexDB: consultar/salvar descoberta
BattleController -> BattleDB: salvar(BattleLog)
BattleController --> Player: retorna Trainer vencedor
@enduml
