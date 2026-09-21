package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma criatura do tipo Pokesal, contendo seus atributos de combate,
 * estado de saúde, tipo elemental e métodos para manipulação de status e batalhas.
 *
 */
public class Pokesal {

  private String nome;
  private String tipo;
  private double atk;
  private double def;
  private double spd;
  private double hp;
  private double maxHp;
  private String efeitoStatus;

  /**
   * Instância o Pokesal com os atributos especificados.
   * O HP atual é inicializado com o valor do HP máximo.
   *  O efeito de status inicia como null.
   *
   * @param nome O nome do Pokesal.
   * @param tipo O tipo elemental do Pokesal.
   * @param hp O HP máximo e inicial do Pokesal.
   * @param atk O atributo de ataque.
   * @param def O atributo de defesa.
   * @param spd O atributo de velocidade.
   */
  public Pokesal(String nome, String tipo, double hp, double atk, double def, double spd) {
    super();
    this.nome = nome;
    this.tipo = tipo;
    this.maxHp = hp;
    this.hp = hp;
    this.atk = atk;
    this.def = def;
    this.spd = spd;
    this.efeitoStatus = null;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public double getAtk() {
    return atk;
  }

  public void setAtk(double atk) {
    this.atk = atk;
  }

  public double getDef() {
    return def;
  }

  public void setDef(double def) {
    this.def = def;
  }

  public double getSpd() {
    return spd;
  }

  public void setSpd(double spd) {
    this.spd = spd;
  }

  public double getHp() {
    return hp;
  }

  public void setHp(double hp) {
    this.hp = hp;
  }

  public double getMaxHp() {
    return maxHp;
  }

  public void setMaxHp(double maxHp) {
    this.maxHp = maxHp;
  }

  public String getEfeitoStatus() {
    return efeitoStatus;
  }

  public void setEfeitoStatus(String efeitoStatus) {
    this.efeitoStatus = efeitoStatus;
  }

  /**
   * Retorna uma lista contendo os Pokesals iniciais disponíveis no jogo.
   *
   */
  public static List<Pokesal> getIniciais() {
    List<Pokesal> iniciais = new ArrayList<>();

    iniciais.add(new Pokesal("BulbaSal", "planta", 45.0, 12.0, 10.0, 45.0));
    iniciais.add(new Pokesal("CharSal", "fogo", 39, 14, 8, 65));
    iniciais.add(new Pokesal("SquirtSal", "agua", 44, 11, 12, 43));
    iniciais.add(new Pokesal("ChikoSal", "planta", 45, 11, 12, 49));
    iniciais.add(new Pokesal("CyndaSal", "fogo", 39, 13, 9, 65));
    iniciais.add(new Pokesal("TotoSal", "agua", 50, 14, 10, 43));
    return iniciais;
  }

  /**
   * Cria uma nova cópia independente de um Pokesal selecionado.
   *
   * @param pokesal O Pokesal a ser clonado/escolhido.
   * @return Um novo objeto com os mesmos atributos base do modelo selecionado.
   */
  public static Pokesal escolha(Pokesal pokesal) {
    return new Pokesal(pokesal.nome, pokesal.tipo, pokesal.maxHp, pokesal.atk, pokesal.def, pokesal.spd);
  }

  /**
   * Calcula o multiplicador de dano com base nos tipos do ataque e do defensor.
   *
   * @param tipoAtaque O tipo do ataque executado ("fogo", "agua", "planta").
   * @param tipoDefensor O tipo do Pokesal que está recebendo o ataque.
   */
  public double multiplicadorTipo(String tipoAtaque, String tipoDefensor) {
    if (tipoAtaque.equals("fogo") && tipoDefensor.equals("planta")) {
      return 2.0;
    }
    if (tipoAtaque.equals("agua") && tipoDefensor.equals("fogo")) {
      return 2.0;
    }
    if (tipoAtaque.equals("planta") && tipoDefensor.equals("agua")) {
      return 2.0;
    }
    if (tipoAtaque.equals("planta") && tipoDefensor.equals("fogo")) {
      return 0.5;
    }
    if (tipoAtaque.equals("fogo") && tipoDefensor.equals("agua")) {
      return 0.5;
    }
    if (tipoAtaque.equals("agua") && tipoDefensor.equals("planta")) {
      return 0.5;
    }

    return 1.0;
  }

  /**
   * Aplica dano ao Pokesal, reduzindo seu HP atual com base no dano final recebido.
   *
   * @param danoFinal O valor total do dano a ser subtraído do HP.
   */
  public void receberDano(double danoFinal) {
    if (this.hp < 0) {
      this.hp = 0;
    } else {
      this.hp -= danoFinal;
    }
  }

  /**
   * Restaura o HP atual do Pokesal para o seu valor máximo.
   */
  public void heal() {
    this.hp = maxHp;

  }
}
