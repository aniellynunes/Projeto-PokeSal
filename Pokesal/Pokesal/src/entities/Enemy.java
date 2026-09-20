package entities;

public class Enemy extends Trainer {

	public Enemy(String nome, Pokesal pokesal) {
		super(nome);
		this.choosePokesal(pokesal);
	}
	
	public int action() {
		//usar item
		if(this.getChoosedPokesal().getHp()<10 && !this.getBag().isEmpty()) {
			return 2;
		}
		//ataca
		return 1;
	}
}
