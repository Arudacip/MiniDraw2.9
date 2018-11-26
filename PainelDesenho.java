import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private InterfaceGrafica aplicacao;
	private Figura figura;
	private ArrayList<Figura> pilha, historico;
	private ArrayList <Point> pontos;
	private Point ultimoDraw, ultimoRead;
	@SuppressWarnings("unused")
	private Point coordAtual;
	private boolean holdCtrl;
	@SuppressWarnings("unused")
	private boolean holdAlt;
	private int funcaoAtiva; 
	
	public PainelDesenho (InterfaceGrafica p) {
		aplicacao = p;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
		
		funcaoAtiva = 0;
		figura = new Figura();
		figura.setCorBorda(aplicacao.getCorBordaAtual());
		figura.setCorInterna(aplicacao.getCorFundoAtual());
		pontos = new ArrayList<Point>();
		pilha = new ArrayList<Figura>();
		historico = new ArrayList<Figura>();
		ultimoDraw = new Point(0,0);
		ultimoRead = new Point(0,0);
		holdCtrl = false;
		holdAlt = false;
	}
	
	public void paint(Graphics g) {	
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create(); // nunca trabalhe diretamente sobre g, apenas sobre uma copia de g  
        g2d.setColor(aplicacao.getCorBordaAtual());
		
		// PILHA
    	for (int j = 0; j < pilha.size(); j++) {
    		Figura tempFigura = pilha.get(j);
    		ArrayList<Point> tempPontos = new ArrayList<Point>();
    		g2d.setColor(tempFigura.getCorBorda());
    		tempPontos = freeman(tempFigura);
    		for (int i=1; i < tempPontos.size(); i++) {
    			Point anterior = tempPontos.get(i-1);
    			Point atual = tempPontos.get(i);
    			g2d.drawLine(anterior.x, anterior.y,atual.x , atual.y);
    		}
    		if (tempFigura.hasMarker()) {
    			for (int i=0; i < tempPontos.size(); i++) {
    				Point p = tempPontos.get(i);
    				int diametro = tempFigura.getDiametro();
    				int meio = diametro/2;
    				g2d.fillOval(p.x-meio, p.y-meio, diametro, diametro);
    			}
    		}
    	}
    	
    	// FIGURA
        g2d.setColor(figura.getCorBorda());
        pontos = freeman(figura);
		for (int i=1; i < pontos.size(); i++) {
			Point anterior = pontos.get(i-1);
			Point atual = pontos.get(i);
			g2d.drawLine(anterior.x, anterior.y,atual.x , atual.y);
		}
		
		// MARCADOR
		if (figura.hasMarker()) {
			for (int i=0; i < pontos.size(); i++) {
				Point p = pontos.get(i);
				int diametro = figura.getDiametro();
				int meio = diametro/2;
				g2d.fillOval(p.x-meio, p.y-meio, diametro, diametro);
			}
		}
		
		// LINHA
		//if(mkLine) {
		//	if(pontos.size()>0) {
    	//		float[] dash = {20f};
        //    	g2d.setStroke(new BasicStroke (4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0f));
        //    	g2d.setColor(figura.getCorBorda().brighter());
        //    	g2d.drawLine(ultimoPonto.x, ultimoPonto.y, coordAtual.x, coordAtual.y);
    	//	}
		//}
		
		g2d.dispose(); //toda vez que voce usa "create" e necessario usar "dispose"
	}
	
	/**
	 * FUNCOES DESENVOLVIDAS PELO GRUPO
	 * 
	 */
	
	// Monta sequencia de Freeman baseado no objeto FIGURA
	private ArrayList<Point> freeman(Figura figura) {
		Point inicio = figura.getInicio();
		Point p;
		int passo = figura.getPasso();
		double valor = (passo*passo)*2;
		int diagonal = (int) Math.sqrt(valor);
		ArrayList<Point> saida = new ArrayList<Point>();
		if (inicio.x != 0 && inicio.y != 0) {
			saida.add(inicio);
		}
		//System.out.println("Passo="+ passo + " / Diagonal="+ diagonal);
		
		for (int i=0; i < figura.getMovimentos().size(); i++) {
			int num = figura.getMovimentos().get(i);
			switch (num) {
			case 0:
				// Movimento 0
				p = new Point(saida.get(i).x + passo, saida.get(i).y);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 1:
				// Movimento 1
				p = new Point(saida.get(i).x + diagonal, saida.get(i).y - diagonal);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 2:
				// Movimento 2
				p = new Point(saida.get(i).x, saida.get(i).y - passo);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 3:
				// Movimento 3
				p = new Point(saida.get(i).x - diagonal, saida.get(i).y - diagonal);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 4:
				// Movimento 4
				p = new Point(saida.get(i).x - passo, saida.get(i).y);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 5:
				// Movimento 5
				p = new Point(saida.get(i).x - diagonal, saida.get(i).y + diagonal);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 6:
				// Movimento 6
				p = new Point(saida.get(i).x, saida.get(i).y + passo);
				saida.add(p);
				ultimoDraw = p;
				break;
			case 7:
				// Movimento 7
				p = new Point(saida.get(i).x + diagonal, saida.get(i).y + diagonal);
				saida.add(p);
				ultimoDraw = p;
				break;
			default:
				// NAO FAZ NADA, PQ NAO DEVE SER ATIVADO
				break;
				// Fim da sequencia
			}
		}
		// Retorna a sequencia preparada
		return saida;
	}
	
	// Monta o passo referente ao ponto
	private int reverse(Point anterior, Point p, int passo) {
		int passoX, passoY, mov = 0;
		passoX = (p.x - anterior.x)/passo;
		passoY = (p.y - anterior.y)/passo;
		System.out.println("Calculados: passoX=" + passoX + " / passoY=" + passoY);
		if (passoX >= 1 && passoY == 0) {
			mov = 0;
		} else if (passoX >= 1 && passoY <= -1) {
			mov = 1;
		} else if (passoX == 0 && passoY <= -1) {
			mov = 2;
		} else if (passoX <= -1 && passoY <= -1) {
			mov = 3;
		} else if (passoX <= -1 && passoY == 0) {
			mov = 4;
		} else if (passoX <= -1 && passoY >= 1) {
			mov = 5;
		} else if (passoX == 0 && passoY >= 1) {
			mov = 6;
		} else if (passoX >= 1 && passoY >= 1) {
			mov = 7;
		} else if (passoX == 0 && passoY == 0) {
			System.out.println("Erro - Ponto na origem: passoX=" + passoX + " / passoY=" + passoY);
		} else {
			System.out.println("Erro desconhecido: passoX=" + passoX + " / passoY=" + passoY);
		}
		return mov;
	}
	
	// Armazena a figura na pilha e libera o atributo principal para a proxima figura na pilha
	private void proxFigura() {
		if (!pontos.isEmpty()) {
			if (pontos.get(0).getX() == pontos.get(pontos.size() - 1).getX()
					&& pontos.get(0).getY() == pontos.get(pontos.size() - 1).getY() && pontos.size() > 1) {
				
				System.out.println("Inicio: X=" + figura.getInicio().getX() + " / Y=" + figura.getInicio().getY());
				System.out.println("Final: X=" + ultimoDraw.getX() + " / Y=" + ultimoDraw.getY());
				// MOSTRA A FIGURA
				System.out.println(figura.toString());
				pilha.add(figura);
				figura = new Figura(aplicacao.getCorBordaAtual(), aplicacao.getCorFundoAtual(),
						figura.hasMarker(), figura.getDiametro());
				pontos = new ArrayList<Point>();
				historico.clear();
			}
		}
/**		if (figura.getInicio().getX() == ultimoPonto.getX()
				&& figura.getInicio().getY() == ultimoPonto.getY() && figura.getMovimentos().size() > 1) {
			pilha.add(figura);
			figura = new Figura(aplicacao.getCorBordaAtual(), aplicacao.getCorFundoAtual(),
						figura.hasMarker(), figura.getDiametro());
			pontos = new ArrayList<Point>();
			historico.clear();
			//ultimoPonto = new Point(0,0);
			repaint();
		} */
	}
	
	// FUNCAO DE UNDO
	public void undo() {
		if (!pilha.isEmpty()) {
			historico.add(pilha.get(pilha.size() - 1));
			pilha.remove(pilha.size() - 1);
		}
		holdCtrl = false;
	}
	
	// FUNCAO DE REDO
	public void redo() {
		if (!historico.isEmpty()) {
			pilha.add(historico.get(historico.size() - 1));
			historico.remove(historico.size() - 1);
		}
		holdCtrl = false;
	}

	public void alteraMarcador() {
		// ALTERA O MARCADOR DOS PONTOS
		DialogMarker dialog;
		String titulo = "Definir Marcador";
		String message = "Configure se o marcador deve aparecer e seu diametro:";
		dialog = new DialogMarker(new JFrame(), titulo, message, this, figura.getDiametro(), figura.hasMarker());
		dialog.setSize(300, 110);
		System.out.println("Diametro > " + figura.getDiametro() + " / Habilitado? " + figura.hasMarker());
	}
	
	/**
	 * FIM DE FUNCOES DESENVOLVIDAS PELO GRUPO
	 * 
	 */
	
	//INTERFACE MouseListener
	
    public void mousePressed(MouseEvent e) {
    	requestFocusInWindow(); //Isso e necessario para que o KeyListener funcione adequadamente
    	Point inicio = figura.getInicio();
		int passo = figura.getPasso(); 
		
		// FUNCOES POR MOUSE
		switch (funcaoAtiva) {
		case 0:
			// FUNCAO DE DESENHAR UM POLIGONO LIVRE
			
			if(e.getButton() == MouseEvent.BUTTON1) {
	        	Point p = e.getPoint();
	    		System.out.println("P Original: x=" + p.x + " / y=" + p.y);
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
				} else {
		    		// Adicionar movimento
		    		System.out.println("P Anterior: x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					int mov = reverse(ultimoRead, p, passo);
					figura.addSequencia(mov);
					ultimoRead = p;
				}
	    	}
	    	else {
	    		//fechar a figura
	    		//pontos.add(pontos.get(0));
	    		//ultimoPonto = pontos.get(0);
	    	}
			break;
		case 1:
			// FUNCAO DE DESENHAR UMA SEQUENCIA DE FREEMAN
			if(e.getButton() == MouseEvent.BUTTON1) {
	        	Point p = e.getPoint();
	    		System.out.println("P Original: x=" + p.x + " / y=" + p.y);
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
				} else {
		    		// NADA
				}
	    	}
	    	else {
	    		// NADA
	    	}
			break;
		case 2:
			// FUNCAO DE DESENHAR UM RETANGULO
			
			if (e.getButton() == MouseEvent.BUTTON1) 
			{
				if(inicio.x == 0 && inicio.y == 0) {
					Point p1 = e.getPoint();
		    		System.out.println("P1 Original: x=" + p1.x + " / y=" + p1.y);
					p1.x = (p1.x/passo)*passo;
		    		p1.y = (p1.y/passo)*passo;
		    		System.out.println("P1 Corrigido: x=" + p1.x + " / y=" + p1.y);
					figura.setInicio(p1);
					ultimoRead = p1;
				}
				else
				{
					Point p2 = e.getPoint();
		    		System.out.println("P2 Original: x=" + p2.x + " / y=" + p2.y);
					p2.x = (p2.x/passo)*passo;
		    		p2.y = (p2.y/passo)*passo;
		    		System.out.println("P2 Corrigido: x=" + p2.x + " / y=" + p2.y);
					Point p3 = new Point();
					p3.setLocation(figura.getInicio().getX(), p2.getY());
					Point p4 = new Point();
					p4.setLocation(p2.getX(), figura.getInicio().getY());
					//pontos.add(p4);
					//pontos.add(p2);
					//pontos.add(p3);
					//pontos.add(pontos.get(0));
					
					// ADAPTA OS PONTOS EM MOVIMENTOS
					System.out.println("N de movimentos:" + figura.getMovimentos().size());
					int mov = reverse(figura.getInicio(), p4, passo);
					figura.addSequencia(mov);
					mov = reverse(p4, p2, passo);
					figura.addSequencia(mov);
					mov = reverse(p2, p3, passo);
					figura.addSequencia(mov);
					mov = reverse(p3, figura.getInicio(), passo);
					figura.addSequencia(mov);
					ultimoRead = figura.getInicio();
					System.out.println(figura.toString());
				}
			}
			break;
		case 3:
			// FUNCAO DE DESENHAR UM TRIANGULO
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				if(inicio.x == 0 && inicio.y == 0) {
					Point p1 = e.getPoint();
		    		System.out.println("P1 Original: x=" + p1.x + " / y=" + p1.y);
					p1.x = (p1.x/passo)*passo;
		    		p1.y = (p1.y/passo)*passo;
		    		System.out.println("P1 Corrigido: x=" + p1.x + " / y=" + p1.y);
					figura.setInicio(p1);
					ultimoRead = p1;
				}
				else {
					Point p2 = e.getPoint();
					Point p3 = new Point();
					p3.setLocation(figura.getInicio().getX(), p2.getY());
					//pontos.add(p2);
					//pontos.add(p3);
					//pontos.add(pontos.get(0));
					
					// ADAPTA OS PONTOS EM MOVIMENTOS
					System.out.println("N de movimentos:" + figura.getMovimentos().size());
					int mov = reverse(figura.getInicio(), p2, passo);
					figura.addSequencia(mov);
					mov = reverse(p2, p3, passo);
					figura.addSequencia(mov);
					mov = reverse(p3, figura.getInicio(), passo);
					figura.addSequencia(mov);
					ultimoRead = figura.getInicio();
					System.out.println(figura.toString());
				}
			}
			break;
		default:
			// FUNCAO DE DESENHAR UM POLIGONO LIVRE
			
			if(e.getButton() == MouseEvent.BUTTON1) {
	        	Point p = e.getPoint();
	    		System.out.println("P Original: x=" + p.x + " / y=" + p.y);
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
				} else {
		    		// Adicionar movimento
		    		System.out.println("P Anterior: x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					int mov = reverse(ultimoRead, p, passo);
					figura.addSequencia(mov);
					ultimoRead = p;
				}
	    	}
	    	else {
	    		//fechar a figura
	    		//pontos.add(pontos.get(0));
	    		//ultimoPonto = pontos.get(0);
	    	}
			break;
		}
    	
    	repaint();
		proxFigura();
    }
    
    // UNUSED
    public void mouseMoved(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) { }
    
	//INTERFACE KeyListener
	public void keyPressed(KeyEvent e) {
		int passo = figura.getPasso();
		double divisao = passo/2;
		int diagonal = (int) Math.sqrt(divisao);
		
		// TECLA ESC 
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			figura.clearMovimentos();
		}
		// TECLA CTRL
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			holdCtrl = true;
		}
		// TECLA ALT
		if (e.getKeyCode() == KeyEvent.VK_ALT) {
			holdAlt = true;
		}
		// TECLA CTRL + Z
		if (e.getKeyCode() == KeyEvent.VK_Z && holdCtrl) {
			// Tecla Ctrl+Z - Cancela o ultimo passo
			//undo();
			holdCtrl = false;
		}
		// TECLA CTRL + Y
		if (e.getKeyCode() == KeyEvent.VK_Y && holdCtrl) {
			// Tecla Ctrl+Y - Refaz o ultimo passo
			//redo();
			holdCtrl = false;
		}
		
		switch (funcaoAtiva) {
		case 0:
			// FUNCAO DE DESENHAR UM POLIGONO LIVRE
			// NAO FAZ NADA
			break;
		case 1:
			// FUNCAO DE DESENHAR UMA SEQUENCIA DE FREEMAN
			
			//TECLA 0
			if(e.getKeyCode() == KeyEvent.VK_0) {	
				Point p = new Point(ultimoRead.x+passo, ultimoRead.y);
				figura.addSequencia(0);
				ultimoRead = p;
			}
			
			//TECLA 1
			if(e.getKeyCode() == KeyEvent.VK_1) {	
				Point p = new Point(ultimoRead.x+diagonal, ultimoRead.y+diagonal);
				figura.addSequencia(1);
				ultimoRead = p;
			}
			
			//TECLA 2
			if(e.getKeyCode() == KeyEvent.VK_2) {	
				Point p = new Point(ultimoRead.x, ultimoRead.y-passo);
				figura.addSequencia(2);
				ultimoRead = p;
			}
			
			//TECLA 3
			if(e.getKeyCode() == KeyEvent.VK_3) {	
				Point p = new Point(ultimoRead.x-diagonal, ultimoRead.y-diagonal);
				figura.addSequencia(3);
				ultimoRead = p;
			}
			
			//TECLA 4
			if(e.getKeyCode() == KeyEvent.VK_4) {	
				Point p = new Point(ultimoRead.x-passo, ultimoRead.y);
				figura.addSequencia(4);
				ultimoRead = p;
			}
			
			//TECLA 5
			if(e.getKeyCode() == KeyEvent.VK_5) {	
				Point p = new Point(ultimoRead.x-diagonal, ultimoRead.y+diagonal);
				figura.addSequencia(5);
				ultimoRead = p;
			}
			
			//TECLA 6
			if(e.getKeyCode() == KeyEvent.VK_6) {	
				Point p = new Point(ultimoRead.x, ultimoRead.y+passo);
				figura.addSequencia(6);
				ultimoRead = p;
			}
			
			//TECLA 7
			if(e.getKeyCode() == KeyEvent.VK_7) {	
				Point p = new Point(ultimoRead.x+diagonal, ultimoRead.y+diagonal);
				figura.addSequencia(7);
				ultimoRead = p;
			}
			break;
		case 2:
			// FUNCAO DE DESENHAR UM RETANGULO
			// NAO FAZ NADA
			break;
		case 3:
			// FUNCAO DE DESENHAR UM TRIANGULO
			// NAO FAZ NADA
			break;
		default:
			// FUNCAO DE DESENHAR UM POLIGONO LIVRE
			// NAO FAZ NADA
			break;
		}
		
		repaint();
		proxFigura();
	}
	
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			holdCtrl = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_ALT) {
			holdAlt = true;
		}
	}
	
	// UNUSED
	public void keyReleased(KeyEvent e) { }
	
	// METODOS DE ACESSO
	
	public Figura getFigura() {
		return figura;
	}
	
	public void setFigura(Figura figura) {
		this.figura = figura;
	}
	
	public void setCores(int opt, Color cor) {
		if (opt == 1) {
			figura.setCorBorda(cor);
		} else if (opt == 2) {
			figura.setCorInterna(cor);
		}
	}
	
	public int getDiametro() {
		return figura.getDiametro();
	}
	
	public void setDiametro(int diametro) {
		figura.setDiametro(diametro);
	}

	public boolean hasMarker() {
		return figura.hasMarker();
	}

	public void setMarker(boolean habilitado) {
		figura.setMarker(habilitado);
	}
	
	public ArrayList<Figura> getPilha() {
		return pilha;
	}

	public void clearUltimo() {
		ultimoDraw = new Point(0,0);
	}
	
	public void setPilha(ArrayList<Figura> pilha) {
		this.pilha = pilha;
	}
	
	public void setFuncao(int funcao) {
		funcaoAtiva = funcao;
	}
	
}