import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Esta classe serve para armazenar todos os parametros de uma determinada figura.
 * A mesma segue o metodo de Freeman para armazenar a figura completa.
 * Tambem possui as propriedades da figura em si.
 */

public class Figura implements Serializable {
	private static final long serialVersionUID = 1L;
	private Point inicio; // Ponto inicial da figura
	private int passo, diametro; // Tamanho do passo por movimento
	private ArrayList<Integer> movimentos; // Movimentos de Freeman {0,1,2,3,4,5,6,7}
	private Color corBorda, corInterna; // Cores da figura
	private boolean marker, fundo; // Definidores de figura (se tem marcador, se tem fundo)
	
	// CONSTRUTORES
	public Figura() {
		this.inicio = new Point(0,0);
		this.passo = 20;
		this.diametro = 10;
		this.movimentos = new ArrayList<>();
		this.corBorda = Color.BLACK;
		this.corInterna = Color.WHITE;
		this.marker = true;
		this.fundo = false;
	}
	
	public Figura(Color cor1, Color cor2, boolean marker, int mkDiam) {
		this.inicio = new Point(0,0);
		this.passo = 20;
		this.diametro = mkDiam;
		this.movimentos = new ArrayList<>();
		this.corBorda = cor1;
		this.corInterna = cor2;
		this.marker = marker;
		this.fundo = false;
	}
	
	public Figura(Point inicio, int passo, ArrayList<Integer> movimentos,
			Color cor1, Color cor2, boolean marker, int mkDiam, boolean fundo) {
		this.inicio = inicio;
		this.passo = passo;
		this.diametro = mkDiam;
		this.movimentos = movimentos;
		this.corBorda = cor1;
		this.corInterna = cor2;
		this.marker = marker;
		this.fundo = fundo;
	}
	
	// GETTERS AND SETTERS
	public Point getInicio() {
		return inicio;
	}
	
	public void setInicio(Point inicio) {
		this.inicio = inicio;
	}
	
	public int getPasso() {
		return passo;
	}
	
	public void setPasso(int passo) {
		this.passo = passo;
	}
	
	public int getDiametro() {
		return diametro;
	}
	
	public void setDiametro(int mkDiam) {
		this.diametro = mkDiam;
	}
	
	public ArrayList<Integer> getMovimentos() {
		return movimentos;
	}
	
	public void setMovimentos(ArrayList<Integer> movimentos) {
		this.movimentos = movimentos;
	}
	
	public void clearMovimentos() {
		this.movimentos = new ArrayList<Integer>();
	}
	
	public Color getCorInterna() {
		return corInterna;
	}
	
	public void setCorInterna(Color corInterna) {
		this.corInterna = corInterna;
	}
	
	public Color getCorBorda() {
		return corBorda;
	}
	
	public void setCorBorda(Color corBorda) {
		this.corBorda = corBorda;
	}
	
	public boolean hasFundo() {
		return fundo;
	}
	
	public void setFundo(boolean fundo) {
		this.fundo = fundo;
	}
	
	public boolean hasMarker() {
		return marker;
	}
	
	public void setMarker(boolean marker) {
		this.marker = marker;
	}
	
	// OPERATIONS
	
	public void addSequencia(int mov) {
		movimentos.add(mov);
	}
	
	@Override
    public String toString() {
		String texto = "Figura:\n Inicio=[" + inicio.x + "," + inicio.y + "]\n Movimentos=[" + printMov() +
				"]\n Passo=" + passo;
        return texto;
    }
	
	private String printMov() {
		String texto = "";
		for (int i=0; i < movimentos.size()-1; i++) {
			texto = texto + movimentos.get(i) + ", ";
		}
		texto = texto + movimentos.get(movimentos.size()-1);
		return texto;
	}
	
}
