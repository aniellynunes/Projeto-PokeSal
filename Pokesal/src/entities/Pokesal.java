package entities;

import java.util.ArrayList;
import java.util.List;

public class Pokesal {
	private String nome;
	private String tipo;
	private double atk;
	private double def;
	private double spd;
	private double hp;
	private double maxHp;
	
	public Pokesal(String nome, String tipo, double hp, double atk, double def, double spd) {
		super();
		this.nome = nome;
		this.tipo = tipo;
		this.maxHp = hp;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.spd = spd;
		
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
	
	public static Pokesal escolha(Pokesal pokesal) {
        return new Pokesal(pokesal.nome, pokesal.tipo, pokesal.maxHp, pokesal.atk, pokesal.def, pokesal.spd);
    
	}

	public double multiplicadorTipo(String tipoAtaque, String tipoDefensor) {
	    if (tipoAtaque.equals("fogo") && tipoDefensor.equals("planta")) return 2.0;
	    if (tipoAtaque.equals("agua") && tipoDefensor.equals("fogo")) return 2.0;
	    if (tipoAtaque.equals("planta") && tipoDefensor.equals("agua")) return 2.0;
	    if (tipoAtaque.equals("planta") && tipoDefensor.equals("fogo")) return 0.5;
	    if (tipoAtaque.equals("fogo") && tipoDefensor.equals("agua")) return 0.5;
	    if (tipoAtaque.equals("agua") && tipoDefensor.equals("planta")) return 0.5;

	    return 1.0;
	
	}
	
	public void receberDano(double danoFinal) {
		if(this.hp<0) {
			this.hp =0;
		}else {
			this.hp-=danoFinal;
		}
    }
	
	public void heal() {
		this.hp=maxHp;
		
	}
		
}
