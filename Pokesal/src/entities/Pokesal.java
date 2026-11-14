package entities;

import enums.Campos;
import enums.Items;
import enums.Moves;
import enums.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade principal de combate (GRASP Information Expert). Gerencia atributos,
 * 2 golpes elementais + Sal-Move, barra de estresse e aplicação de itens.
 */
public class Pokesal {
	private final String nome;
	private final Type type;
	private double atk;
	private double def;
	private double spd;
	private double hp;
	private final double maxHp;
	private String efeitoStatus;
	private final List<Moves> golpes;

	private int stressBar = 0;
	private boolean stressAtivo = false;
	private int acertosSalMove = 0;
	private boolean salMoveDisponivel = false;
	private boolean salMoveUsado = false;

	public Pokesal(String nome, Type type, double hp, double atk, double def, double spd) {
		this.nome = nome;
		this.type = type;
		this.maxHp = hp;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.spd = spd;
		this.golpes = new ArrayList<>();
		initMoves();
	}

	/** Preenche os 2 golpes elementais correspondentes ao type do Pokesal. */
	private void initMoves() {
		switch (this.type) {
		case FOGO -> {
			golpes.add(Moves.EMBER);
			golpes.add(Moves.FLAMETHROWER);
		}
		case AGUA -> {
			golpes.add(Moves.WATER_GUN);
			golpes.add(Moves.SURF);
		}
		case GRAMA -> {
			golpes.add(Moves.VINE_WHIP);
			golpes.add(Moves.ENERGY_BALL);
		}
		}
	}

	/** Retorna a lista padrão de Pokesals iniciais dos 3 types elementais. */
	public static List<Pokesal> getIniciais() {
		return List.of(new Pokesal("BulbaSal", Type.GRAMA, 45.0, 12.0, 10.0, 45.0),
				new Pokesal("CharSal", Type.FOGO, 39.0, 14.0, 8.0, 65.0),
				new Pokesal("SquirtSal", Type.AGUA, 44.0, 11.0, 12.0, 43.0),

				new Pokesal("ChikoSal", Type.GRAMA, 45.0, 12.0, 10.0, 45.0),
				new Pokesal("CyndaSal", Type.FOGO, 39.0, 14.0, 8.0, 65.0),
				new Pokesal("TotoSal", Type.AGUA, 44.0, 11.0, 12.0, 43.0));
	}

	/** Clona um Pokesal base para uso independente em batalha (GRASP Creator). */
	public static Pokesal clonar(Pokesal base) {
		return new Pokesal(base.nome, base.type, base.maxHp, base.atk, base.def, base.spd);
	}

	/**
	 * Aplica o valor numérico do enum typeItem diretamente no atributo
	 * correspondente.
	 */
	public void aplicarItem(Items item) {
		switch (item) {
		case POTION -> curar(item.getEfeito());
		case X_ATK -> this.atk += item.getEfeito();
		case X_DEF -> this.def += item.getEfeito();
		case X_SPD -> this.spd += item.getEfeito();
		}
	}

	/** Calcula o multiplicador elemental entre Fogo, Água e Grama. */
	public static double calcularMultiplicadortype(Type ataque, Type defensor) {
		if (ataque == Type.FOGO && defensor == Type.GRAMA)
			return 2.0;
		if (ataque == Type.AGUA && defensor == Type.FOGO)
			return 2.0;
		if (ataque == Type.GRAMA && defensor == Type.AGUA)
			return 2.0;

		if (ataque == Type.GRAMA && defensor == Type.FOGO)
			return 0.5;
		if (ataque == Type.FOGO && defensor == Type.AGUA)
			return 0.5;
		if (ataque == Type.AGUA && defensor == Type.GRAMA)
			return 0.5;

		return 1.0;
	}

	/** Calcula o multiplicador de vantagem concedido pelo cenário atual. */
	public double obterVantagemTerreno(Campos campo) {
		if (campo == Campos.ASFALTO_QUENTE && this.type == Type.FOGO)
			return 1.15;
		if ((campo == Campos.POCA_DE_CHUVA || campo == Campos.PISO_MOLHADO) && this.type == Type.AGUA)
			return 1.10;
		return 1.0;
	}

	/**
	 * Incrementa a barra de estresse ao errar um golpe; ativa punição ao atingir 3
	 * erros.
	 */
	public void registrarErroAtaque() {
		if (!stressAtivo && ++stressBar >= 3) {
			stressAtivo = true;
			stressBar = 0;
		}
	}

	/**
	 * Incrementa a barra do Sal-Move ao acertar um golpe; libera o golpe especial
	 * ao atingir 3 acertos.
	 */
	public void registrarAcertoAtaque() {
		if (!salMoveUsado && ++acertosSalMove >= 3) {
			salMoveDisponivel = true;
		}
	}

	/** Retorna o movimento especial Sal-Move correspondente ao type do Pokesal. */
	public Moves obterSalMove() {
		return switch (this.type) {
		case FOGO -> Moves.FIRE_BLAST;
		case AGUA -> Moves.HYDRO_PUMP;
		case GRAMA -> Moves.SOLAR_BEAM;
		};
	}

	/** Consome o uso único do Sal-Move na batalha atual. */
	public void useSalMove() {
		this.salMoveDisponivel = false;
		this.salMoveUsado = true;
		this.acertosSalMove = 0;
	}

	/** Subtrai dano do HP atual garantindo que não fique negativo. */
	public void receberDano(double dano) {
		this.hp = Math.max(0, this.hp - dano);
	}

	/** Restaura HP respeitando o limite máximo. */
	public void curar(double qtd) {
		this.hp = Math.min(this.maxHp, this.hp + qtd);
	}

	/** Restaura totalmente o HP e reseta barras temporárias de batalha. */
	public void fullRestore() {
		this.hp = maxHp;
		this.efeitoStatus = null;
		this.stressBar = 0;
		this.stressAtivo = false;
		this.acertosSalMove = 0;
		this.salMoveDisponivel = false;
		this.salMoveUsado = false;
	}

	public String getNome() {
		return nome;
	}

	public Type getType() {
		return type;
	}

	public double getAtk() {
		return atk;
	}

	public double getDef() {
		return def;
	}

	public double getSpd() {
		return spd;
	}

	public double getHp() {
		return hp;
	}

	public void setHp(double hp) {
		this.hp = Math.max(0, hp);
	}

	public double getMaxHp() {
		return maxHp;
	}

	public String getEfeitoStatus() {
		return efeitoStatus;
	}

	public void setEfeitoStatus(String status) {
		this.efeitoStatus = status;
	}

	public List<Moves> getGolpes() {
		return golpes;
	}

	public boolean getStressAtivo() {
		return stressAtivo;
	}

	public void setStressAtivo(boolean estresseAtivo) {
		this.stressAtivo = estresseAtivo;
	}

	public boolean isSalMoveDisponivel() {
		return salMoveDisponivel;
	}
}
