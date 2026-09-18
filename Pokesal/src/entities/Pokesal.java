package entities;

import java.util.Random;

public class Pokesal {
	private String nome;
	private String tipo;
	private int atk;
	private int baseAtk;
	private int baseDef;
	private int baseSpd;
	private int def;
	private int spd;
	private int hp;
	private int maxHp;
	
	public Pokesal(String nome, String tipo, int atk, int baseAtk, int baseDef, int baseSpd, int def, int spd, int hp,
			int maxHp) {
		super();
		this.nome = nome;
		this.tipo = tipo;
		this.atk = atk;
		this.baseAtk = baseAtk;
		this.baseDef = baseDef;
		this.baseSpd = baseSpd;
		this.def = def;
		this.spd = spd;
		this.hp = hp;
		this.maxHp = maxHp;
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


	public int getAtk() {
		return atk;
	}


	public void setAtk(int atk) {
		this.atk = atk;
	}


	public int getBaseAtk() {
		return baseAtk;
	}


	public void setBaseAtk(int baseAtk) {
		this.baseAtk = baseAtk;
	}


	public int getBaseDef() {
		return baseDef;
	}


	public void setBaseDef(int baseDef) {
		this.baseDef = baseDef;
	}


	public int getBaseSpd() {
		return baseSpd;
	}


	public void setBaseSpd(int baseSpd) {
		this.baseSpd = baseSpd;
	}


	public int getDef() {
		return def;
	}


	public void setDef(int def) {
		this.def = def;
	}


	public int getSpd() {
		return spd;
	}


	public void setSpd(int spd) {
		this.spd = spd;
	}


	public int getHp() {
		return hp;
	}


	public void setHp(int hp) {
		this.hp = hp;
	}


	public int getMaxHp() {
		return maxHp;
	}


	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	public void choosePokesal(Trainer trainer) {
		if(trainer.getPokesal()==null) {
			trainer.setPokesal(this);
			System.out.println("Congratulations! You've obtained a "+ this.getNome());
		}
	}
	
	public void attack(Pokesal alvo) {
		Random r=new Random();
		 int roll=r.nextInt(20)+1;
		 if(roll<alvo.getDef()) {
			 System.out.println("missed attack!!!");
			 return;
		 }
		 int dmg=atk;
		 if(roll>=18) {
			 dmg*=1.2;
			 System.out.println("Critical hit!");
		 }
		 alvo.setHp(alvo.getHp()-dmg);
		 System.out.println(alvo.getNome()+ "hp:"+alvo.getHp()+alvo.getMaxHp());
	}
	
	public void pokeCenter() {
		hp=maxHp;
		atk=baseAtk;
		def=baseDef;
		spd=baseSpd;
		
	}
	
	
}
