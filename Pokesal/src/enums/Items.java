package enums;

public enum Items {
	POTION(20),
	X_ATK(5),
	X_DEF(5),
	X_SPD(5);
    
	private final int efeito;
    Items(int efeito) {
        this.efeito = efeito;        
    }

    public int getEfeito() { return efeito; }
    
}
