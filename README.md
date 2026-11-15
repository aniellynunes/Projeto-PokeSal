```mermaid

Diagrama de Classes

    %% PACOTE ENUMS
    namespace enums {
        class Type {
            <<enumeration>>
            GRAMA
            FOGO
            AGUA
        }
        class Campos {
            <<enumeration>>
            ASFALTO_QUENTE
            POCA_DE_CHUVA
            PISO_MOLHADO
            CANTEIRO_CENTRAL
            NEUTRO
        }
        class Items {
            <<enumeration>>
            POTION(20)
            X_ATK(5)
            X_DEF(5)
            X_SPD(10)
            -int efeito
            +getEfeito() int
        }
        class Moves {
            <<enumeration>>
            EMBER
            FLAMETHROWER
            FIRE_BLAST
            WATER_GUN
            SURF
            HYDRO_PUMP
            VINE_WHIP
            ENERGY_BALL
            SOLAR_BEAM
            -int poder
            -int precisao
            -Type tipo
            +getPoder() int
            +getPrecisao() int
            +getTipo() Type
        }
    }

    %% PACOTE INTERFACES
    namespace interfaces {
        class Crud~T, ID~ {
            <<interface>>
            +salvar(T entidade) void
            +buscarPorId(ID id) Optional~T~
            +listarTodos() List~T~
        }
        class D100 {
            <<interface>>
            +rolarDado100() int
            +testarChance(double pct) boolean
        }
        class CampoService {
            <<interface>>
            +getField(LocalTime hora) Campos
        }
        class BattleEngine {
            <<interface>>
            +registrarAtaque(String atacante, String golpe) void
            +registrarDano(String alvo, int dmg, boolean crit, boolean errou) void
            +registrarEfeitoStatus(String pokesal, String status) void
            +registrarEfeitoTerreno(String terreno, String efeito) void
            +registrarUsoItem(String treinador, String item) void
        }
        class BattleDB {
            <<interface>>
            +findLastHit(String perdedor, String vencedor) Optional~Moves~
        }
        class SalDexDB {
            <<interface>>
            +discovered(String nomePokesal) boolean
        }
    }

    %% PACOTE ENTITIES
    namespace entities {
        class Pokesal {
            -String nome
            -Type type
            -double atk
            -double def
            -double spd
            -double hp
            -double maxHp
            -String efeitoStatus
            -List~Moves~ golpes
            -int barraEstresse
            -boolean estresseAtivo
            -int acertosSalMove
            -boolean salMoveDisponivel
            -boolean salMoveUsado
            +aplicarItem(Items item) void
            +obterVantagemTerreno(Campos campo) double
            +registrarErroAtaque() void
            +registrarAcertoAtaque() void
            +obterSalMove() Moves
            +consumirSalMove() void
            +receberDano(double dano) void
            +curar(double qtd) void
            +fullRestore() void
            +calcularMultiplicadortype(Type a, Type d)$ double
            +getIniciais()$ List~Pokesal~
            +clonar(Pokesal base)$ Pokesal
        }
        class Trainer {
            -String nome
            -Pokesal choosedPokesal
            -List~Items~ bag
            -Set~String~ pokedex
            -int usedItens
            +choosePokesal(Pokesal p) void
            +registrarSaldex(String nome) void
            +addItem(Items item) void
            +consumirPrimeiroItem() Items
        }
        
        class Enemy {
            +Enemy(String nome, Pokesal p)
            +decidirAcao() int
            +escolherMelhorGolpe(Pokesal alvo) Moves
        }
        class BattleLog {
            -String vencedor
            -String perdedor
            -Moves golpeFinal
            -int turnos
            -String pokesalsUtilizados
        }
    }

    %% PACOTE REPOSITORIES
    namespace repositories {
        class BattleRepository {
            -List~BattleLog~ lutas
            +salvar(BattleLog log) void
            +buscarPorId(String id) Optional~BattleLog~
            +listarTodos() List~BattleLog~
            +findLastHit(String p, String v) Optional~Moves~
        }
        class SalDexRepository {
            -Set~String~ catalogados
            +salvar(String nome) void
            +buscarPorId(String id) Optional~String~
            +listarTodos() List~String~
            +discovered(String nome) boolean
        }
    }

    %% PACOTE CONTROLLER
    namespace controller {
        class BattleController {
            -D100 d100
            -CampoService bField
            -Clock clock
            -BattleDB battleDB
            -SalDexDB salDex
            -BattleEngine registro
            -Scanner sc
            +battleStart(Player player, Enemy enemy) Trainer
            +executarAtaque(Trainer a, Trainer d, Moves g, Campos c) void
        }
        class TournamentController {
            -BattleController battleController
            +startTorneio(Trainer p, List~Enemy~ e) Trainer
            +startTWLosers(Trainer p, List~Enemy~ e) Trainer
        }
    }

    %% RELACIONAMENTOS
    Crud <|-- BattleDB
    Crud <|-- SalDexDB
    BattleDB <|.. BattleRepository
    SalDexDB <|.. SalDexRepository

    Trainer <|-- Enemy
    Trainer "1" *-- "1" Pokesal : possui
    Trainer "1" o-- "0..2" Items : mochila
    Pokesal "1" *-- "2..3" Moves : golpes
    Pokesal --> Type : tipo

    BattleRepository "1" o-- "*" BattleLog : armazena
    BattleController --> D100
    BattleController --> CampoService
    BattleController --> BattleDB
    BattleController --> SalDexDB
    BattleController --> BattleEngine
```
    BattleController ..> Campos : usa
    TournamentController --> BattleController
