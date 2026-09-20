package entities;

import java.util.Random;

public class Campo {
	private String cenario;
	
	public Campo() {
		String[] cenarios= {"asfalto quente", "poca de chuva", "piso molhado", "canteiro central"};
		Random r =new Random();
		this.cenario=cenarios[r.nextInt(cenarios.length)];
	}
	
	public double vantagemCampo(Pokesal p) {
		if (cenario.equals("asfalto quente") && p.getTipo().equals("fogo")) {
            return 1.15;
        }
		if ((cenario.equals("poca de chuva") || cenario.equals("piso molhado")) && p.getTipo().equals("agua")) {
            return 1.10;  
        }
		
		if (cenario.equals("canteiro Central") && p.getTipo().equals("planta")) {
            int cura = (int) (p.getHp() * 0.05);
            p.setHp(Math.min(p.getMaxHp(), p.getHp() + cura));
            System.out.println(p.getNome() + " recuperou " + cura + " de HP pelo Canteiro Central!");
        }
		return 1;
		
	}
	
	public String getCenario() {
		return cenario;
	}
}
