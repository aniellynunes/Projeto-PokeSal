package entities;

import java.util.ArrayList;
import java.util.Scanner;

public class Trainer {
	Scanner sc=new Scanner(System.in);
	
	private Pokesal pokesal;
	private ArrayList<Item> itens;
	
	public Trainer(Pokesal pokesal, ArrayList<Item> itens) {
		super();
		this.pokesal = pokesal;
		this.itens = new ArrayList<>();
	}
	
	public Pokesal getPokesal() {
		return pokesal;
	}
	
	public void setPokesal(Pokesal pokesal) {
		this.pokesal=pokesal;
	}
	
	public ArrayList<Item> getItens(){
		return itens;
	}
	
	public void pokeMart() {
		if (itens.size()<2) {
			System.out.println("Choose 2 itens before the battle");
		
			while(itens.size()<2) {
				System.out.println("Welcome to Pokemart");
				System.out.println("--------------MENU--------------");
				System.out.println("1- potion--> heal 20hp");
				System.out.println("2- X-spd--> increase spd in 10%");
				System.out.println("3- X-atk--> increase atk in 10%");
				System.out.println("4- X-def--> increase def in 10%");
			
				int choose=sc.nextInt();
				Item choosedItem=null;
			
				switch (choose) {
				case 1:
					choosedItem=new Item("potion");
				break;
				case 2:
					choosedItem=new Item("X-spd");
				break;
				case 3:
					choosedItem=new Item("X-atk");
					break;
				case 4:
					choosedItem=new Item("X-def");
				break;
				default:
					System.out.println("invalid option");
				continue;
				}
				itens.add(choosedItem);
		
			}
		}else {
			System.out.println("your bag is full, comeback another day");
		}
		
	}
	
	public void choosePokesal(Pokesal pokesal){
		if(pokesal==null) {
			System.out.println("now, choose your first pokesal");
			this.pokesal=pokesal;
			System.out.println(pokesal.getNome()+" foi escolhido");
		}
	}
	
	public void useItens() {
		for (int i=0; i<itens.size(); i++) {
			System.out.println(i+1+"- "+itens.get(i).getNome());
		}
		int choose=sc.nextInt();
		switch (choose) {
		case 1:
			//itens.get(1).usar-->(precisamos criar o usar())
			itens.remove(0);
		break;
		case 2:
			//itens.get(1).use(pokesal);
			itens.remove(1);
		break;
		default:
			System.out.println("invalid option!");
		}
		
	}
	
	
	
	//instancia uma sequencia de metodos
	public void journey() {
		choosePokesal(pokesal);
		pokeMart();
		//zone.battle()-->criar metodo que cria um trainer randomico e fazer uma batalha em turnos e printa o ganhador
		pokesal.pokeCenter();
		pokeMart();
		//zone.battle();
		//provavelmente teremos que printar um log de batalha mostrando o ganhador 
	}
	
	
	
	
	
	
		
}
