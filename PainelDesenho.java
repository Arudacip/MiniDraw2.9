import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private InterfaceGrafica aplicacao;
	private Figura figura;
	private ArrayList<Figura> pilha, histPilha;
	private ArrayList<Point> pontos, histPontos;
	private ArrayList<Integer> histMovs;
	private Point ultimoDraw, ultimoRead, coordAtual;
	private boolean debug, mkLine, holdCtrl;
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
		histPilha = new ArrayList<Figura>();
		histPontos = new ArrayList<Point>();
		histMovs = new ArrayList<Integer>();
		ultimoDraw = new Point(0,0);
		ultimoRead = new Point(0,0);
		holdCtrl = false;
		holdAlt = false;
		mkLine = false;
		// Define se deve ocorrrer debug em console
		debug = false;
	}
	
	public void paint(Graphics g) {	
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create(); // nunca trabalhe diretamente sobre g, apenas sobre uma copia de g
		
		// PILHA
    	for (int j = 0; j < pilha.size(); j++) {
    		Figura tempFigura = pilha.get(j);
    		ArrayList<Point> tempPontos = new ArrayList<Point>();
    		g2d.setColor(tempFigura.getCorBorda());
    		tempPontos = freeman(tempFigura);
    		ultimoDraw = tempPontos.get(tempPontos.size()-1);
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
        g2d.setColor(aplicacao.getCorBordaAtual());
        pontos = freeman(figura);
        figura.setCorBorda(aplicacao.getCorBordaAtual());
		if (!pontos.isEmpty()) {
			ultimoDraw = pontos.get(pontos.size()-1);
			if(debug) {
				System.out.println("Ultimo Desenhado x=" + ultimoDraw.x + " / y=" + ultimoDraw.y);
			}
			if (funcaoAtiva == 1) {
				if(debug) {
					System.out.println("[F1] Ultimo Desenhado x=" + ultimoDraw.x + " / y=" + ultimoDraw.y);
				}
				proxFigura();
			}
		}
		for (int i=1; i < pontos.size(); i++) {
			Point anterior = pontos.get(i-1);
			Point atual = pontos.get(i);
			g2d.drawLine(anterior.x, anterior.y,atual.x , atual.y);
			if (funcaoAtiva == 0) {
				ultimoDraw = atual;
				if(debug) {
					System.out.println("[F0] Ultimo Desenhado x=" + ultimoDraw.x + " / y=" + ultimoDraw.y);
				}
				proxFigura();
			}
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
		if(mkLine) {
			if(pontos.size()>0) {
    			float[] dash = {5f};
            	g2d.setStroke(new BasicStroke (2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0f));
            	g2d.setColor(figura.getCorBorda());
            	g2d.drawLine(ultimoDraw.x, ultimoDraw.y, coordAtual.x, coordAtual.y);
    		}
		}
		
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
		ArrayList<Point> saida = new ArrayList<Point>();
		if (inicio.x != 0 && inicio.y != 0) {
			saida.add(inicio);
		}
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
				p = new Point(saida.get(i).x + passo, saida.get(i).y - passo);
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
				p = new Point(saida.get(i).x - passo, saida.get(i).y - passo);
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
				p = new Point(saida.get(i).x - passo, saida.get(i).y + passo);
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
				p = new Point(saida.get(i).x + passo, saida.get(i).y + passo);
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
	private int[] reverse(Point anterior, Point p, int passo) {
		int passoX, passoY, mov = 0;
		passoX = (p.x - anterior.x)/passo;
		passoY = (p.y - anterior.y)/passo;
		if(debug) {
			System.out.println("Calculados: passoX=" + passoX + " / passoY=" + passoY);
		}
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
			if(debug) {
				System.out.println("Erro - Ponto na origem: passoX=" + passoX + " / passoY=" + passoY);
			}
		} else {
			if(debug) {
				System.out.println("Erro desconhecido: passoX=" + passoX + " / passoY=" + passoY);
			}
		}
		int[] saida = {mov, passoX, passoY}; 
		return saida;
	}
	
	// Faz o movimento referente ao passo necessario em sequencia ordenada 
	public void movimento(Point anterior, Point atual, int passo, Figura figura) {
		int mov;
		int[] saida;
		saida = reverse(anterior, atual, passo);
		int counterX = saida[1]; // movimentos em X
		int counterY = saida[2]; // movimentos em Y
		//MOVIMENTO APENAS EM X
		if (Math.abs(counterX) > 0 && counterY == 0) {
			if(debug) {
				System.out.println("ContadorX:" + counterX);
			}
			while (Math.abs(counterX) > 0) {
				saida = reverse(anterior, atual, passo);
				mov = saida[0];
				figura.addSequencia(mov);
				if (counterX > 0) {
					counterX = counterX - 1;
				} else if (counterX < 0) {
					counterX = counterX + 1;
				}
			}
		}
		//MOVIMENTO APENAS EM Y
		if (Math.abs(counterY) > 0 && counterX == 0) {
			if(debug) {
				System.out.println("ContadorY:" + counterY);
			}
			while (Math.abs(counterY) > 0) {
				saida = reverse(anterior, atual, passo);
				mov = saida[0];
				figura.addSequencia(mov);
				if (counterY > 0) {
					counterY = counterY - 1;
				} else if (counterY < 0) {
					counterY = counterY + 1;
				}
			}
		}
		//MOVIMENTO EM DIAGONAL
		if (Math.abs(counterX) > 0 || Math.abs(counterY) > 0) {
			if(debug) {
				System.out.println("ContadorX:" + counterX + " / ContadorY:" + counterY);
			}
			while (Math.abs(counterX) > 0 && Math.abs(counterY) > 0) {
				saida = reverse(anterior, atual, passo);
				mov = saida[0];
				figura.addSequencia(mov);
				if (counterX > 0) {
					counterX = counterX - 1;
				} else if (counterX < 0) {
					counterX = counterX + 1;
				}
				if (counterY > 0) {
					counterY = counterY - 1;
				} else if (counterY < 0) {
					counterY = counterY + 1;
				}
			}
		}
	}
	
	// Verifica se a figura foi fechada
	private void proxFigura() {
		if(debug) {
			System.out.println("Checa se a figura esta fechada");
			System.out.println("Inicio: X=" + figura.getInicio().getX() + " / Y=" + figura.getInicio().getY());
			System.out.println("Final: X=" + ultimoDraw.getX() + " / Y=" + ultimoDraw.getY());
		}
		if (!figura.getMovimentos().isEmpty()) {
			if (figura.getInicio().getX() == ultimoDraw.getX() && figura.getInicio().getY() == ultimoDraw.getY()) {
				if(debug) {
					System.out.println("Inicio: X=" + figura.getInicio().getX() + " / Y=" + figura.getInicio().getY());
					System.out.println("Final: X=" + ultimoDraw.getX() + " / Y=" + ultimoDraw.getY());
				}
				// ARMAZENA A FIGURA E SEGUE PARA A PROXIMA
				pararFigura();
			}
		}
	}
	
	// Armazena a figura na pilha e libera o atributo principal para a proxima figura na pilha
	private void pararFigura() {
		// MOSTRA A FIGURA
		if(debug) {
			System.out.println(figura.toString());
		}
		pilha.add(figura);
		figura = new Figura(aplicacao.getCorBordaAtual(), aplicacao.getCorFundoAtual(), figura.hasMarker(), figura.getDiametro());
		pontos = new ArrayList<Point>();
		histPilha.clear();
		histPontos.clear();
		histMovs.clear();
		ultimoDraw = new Point(0,0);
		ultimoRead = new Point(0,0);
		holdCtrl = false;
		holdAlt = false;
		mkLine = false;
		repaint();
	}
	
	// FUNCAO DE UNDO
	public void undo() {
		if(!figura.getMovimentos().isEmpty()) {
			ArrayList<Integer> meusMovs = figura.getMovimentos();
			histMovs.add(meusMovs.get(meusMovs.size() - 1));
			meusMovs.remove(meusMovs.size() - 1);
			figura.setMovimentos(meusMovs);
			if(figura.getMovimentos().isEmpty()) {
				figura.setInicio(new Point(0,0));
				histPontos.add(pontos.get(0));
				pontos.clear();
			}
		} else if (!pilha.isEmpty()) {
			histPilha.add(pilha.get(pilha.size() - 1));
			pilha.remove(pilha.size() - 1);
		}
		holdCtrl = false;
	}
	
	// FUNCAO DE REDO
	public void redo() {
		if (!histMovs.isEmpty()) {
			if(histPontos.size() == 1) {
				figura.setInicio(histPontos.get(0));
				histPontos.remove(0);
			}
			ArrayList<Integer> meusMovs = figura.getMovimentos();
			meusMovs.add(histMovs.get(histMovs.size() - 1));
			histMovs.remove(histMovs.size() - 1);
			figura.setMovimentos(meusMovs);
			repaint();
		} else if (!histPilha.isEmpty()) {
			pilha.add(histPilha.get(histPilha.size() - 1));
			histPilha.remove(histPilha.size() - 1);
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
	        	if(debug) {
					System.out.println("P Original: x=" + p.x + " / y=" + p.y);
	        	}
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		if(debug) {
					System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	    		}
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
					mkLine = true;
				} else {
		    		// Adicionar movimento
					if(debug) {
						System.out.println("P Anterior: x=" + pontos.get(pontos.size()-1).x + " / y=" + pontos.get(pontos.size()-1).y);
					}
					int[] mov = reverse(pontos.get(pontos.size()-1), p, passo);
					figura.addSequencia(mov[0]);
					ultimoRead = p;
					ultimoDraw = p;
				}
	    	}
	    	else {
	    		// Parar no estado atual
				pararFigura();
	    	}
			break;
		case 1:
			// FUNCAO DE DESENHAR UMA SEQUENCIA DE FREEMAN
			if(e.getButton() == MouseEvent.BUTTON1) {
	        	Point p = e.getPoint();
	        	if(debug) {
					System.out.println("P Original: x=" + p.x + " / y=" + p.y);
	        	}
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		if(debug) {
					System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	    		}
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
				} else {
		    		// NADA
				}
	    	}
	    	else {
	    		// Parar no estado atual
				pararFigura();
	    	}
			break;
		case 2:
			// FUNCAO DE DESENHAR UM RETANGULO
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				if(inicio.x == 0 && inicio.y == 0) {
					Point p1 = e.getPoint();
					if(debug) {
						System.out.println("P1 Original: x=" + p1.x + " / y=" + p1.y);
					}
					p1.x = (p1.x/passo)*passo;
		    		p1.y = (p1.y/passo)*passo;
		    		if(debug) {
						System.out.println("P1 Corrigido: x=" + p1.x + " / y=" + p1.y);
		    		}
					figura.setInicio(p1);
					ultimoRead = p1;
					mkLine = true;
				}
				else {
					Point p2 = e.getPoint();
					if(debug) {
						System.out.println("P2 Original: x=" + p2.x + " / y=" + p2.y);
					}
					p2.x = (p2.x/passo)*passo;
		    		p2.y = (p2.y/passo)*passo;
		    		if(debug) {
						System.out.println("P2 Corrigido: x=" + p2.x + " / y=" + p2.y);
		    		}
					mkLine = false;
					Point p3 = new Point();
					p3.setLocation(figura.getInicio().getX(), p2.getY());
					Point p4 = new Point();
					p4.setLocation(p2.getX(), figura.getInicio().getY());
					
					// MOVIMENTO 1
					movimento(figura.getInicio(), p4, passo, figura);
					// MOVIMENTO 2
					movimento(p4, p2, passo, figura);
					// MOVIMENTO 3
					movimento(p2, p3, passo, figura);
					// MOVIMENTO 2
					movimento(p3, figura.getInicio(), passo, figura);
					// FEITO O RETANGULO
					ultimoRead = figura.getInicio();
					mkLine = false;
					if(debug) {
						System.out.println(figura.toString());
					}
				}
			} else {
	    		// Parar no estado atual
				pararFigura();
			}
			break;
		case 3:
			// FUNCAO DE DESENHAR UM TRIANGULO
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				if(inicio.x == 0 && inicio.y == 0) {
					Point p1 = e.getPoint();
					if(debug) {
						System.out.println("P1 Original: x=" + p1.x + " / y=" + p1.y);
					}
					p1.x = (p1.x/passo)*passo;
		    		p1.y = (p1.y/passo)*passo;
		    		if(debug) {
						System.out.println("P1 Corrigido: x=" + p1.x + " / y=" + p1.y);
		    		}
					figura.setInicio(p1);
					ultimoRead = p1;
					mkLine = true;
				}
				else {
					Point p2 = e.getPoint();
					if(debug) {
						System.out.println("P2 Original: x=" + p2.x + " / y=" + p2.y);
					}
					p2.x = (p2.x/passo)*passo;
		    		p2.y = (p2.y/passo)*passo;
		    		if(debug) {
						System.out.println("P2 Corrigido: x=" + p2.x + " / y=" + p2.y);
		    		}
					mkLine = false;
					Point p3 = new Point();
					p3.setLocation(figura.getInicio().getX(), p2.getY());
					
					// MOVIMENTO 1
					movimento(figura.getInicio(), p2, passo, figura);
					// MOVIMENTO 2
					movimento(p2, p3, passo, figura);
					// MOVIMENTO 3
					movimento(p3, figura.getInicio(), passo, figura);
					// Ajusta o triangulo
					figura.ajustaTriangulo();
					// FEITO O TRIANGULO
					ultimoRead = figura.getInicio();
					mkLine = false;
					if(debug) {
						System.out.println(figura.toString());
					}
				}
			} else {
	    		// Parar no estado atual
				pararFigura();
			}
			break;
		default:
			// FUNCAO DE DESENHAR UM POLIGONO LIVRE
			
			if(e.getButton() == MouseEvent.BUTTON1) {
	        	Point p = e.getPoint();
	        	if(debug) {
					System.out.println("P Original: x=" + p.x + " / y=" + p.y);
	        	}
				p.x = (p.x/passo)*passo;
	    		p.y = (p.y/passo)*passo;
	    		if(debug) {
					System.out.println("P Corrigido: x=" + p.x + " / y=" + p.y);
	    		}
	        	if (inicio.x == 0 && inicio.y == 0) {
					figura.setInicio(p);
					ultimoRead = p;
					mkLine = true;
				} else {
		    		// Adicionar movimento
					if(debug) {
						System.out.println("P Anterior: x=" + pontos.get(pontos.size()-1).x + " / y=" + pontos.get(pontos.size()-1).y);
					}
					int[] mov = reverse(pontos.get(pontos.size()-1), p, passo);
					figura.addSequencia(mov[0]);
					ultimoRead = p;
					ultimoDraw = p;
				}
	    	}
	    	else {
	    		// Parar no estado atual
				pararFigura();
	    	}
			break;
		}
    	
    	repaint();
		proxFigura();
    }
    
    public void mouseMoved(MouseEvent e) {
    	coordAtual = e.getPoint();
    	repaint();
    }
    
    // UNUSED
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) { }
    // FIM UNUSED
    
	//INTERFACE KeyListener
    
	public void keyPressed(KeyEvent e) {
		int passo = figura.getPasso();
		
		// TECLA ESC 
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			histPontos.clear();
			histMovs.clear();
			ultimoDraw = new Point(0,0);
			ultimoRead = new Point(0,0);
			holdCtrl = false;
			holdAlt = false;
			mkLine = false;
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
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(0);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 1
			if(e.getKeyCode() == KeyEvent.VK_1) {	
				Point p = new Point(ultimoRead.x+passo, ultimoRead.y+passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(1);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 2
			if(e.getKeyCode() == KeyEvent.VK_2) {	
				Point p = new Point(ultimoRead.x, ultimoRead.y-passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(2);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 3
			if(e.getKeyCode() == KeyEvent.VK_3) {	
				Point p = new Point(ultimoRead.x-passo, ultimoRead.y-passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(3);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 4
			if(e.getKeyCode() == KeyEvent.VK_4) {	
				Point p = new Point(ultimoRead.x-passo, ultimoRead.y);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(4);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 5
			if(e.getKeyCode() == KeyEvent.VK_5) {	
				Point p = new Point(ultimoRead.x-passo, ultimoRead.y+passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(5);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 6
			if(e.getKeyCode() == KeyEvent.VK_6) {	
				Point p = new Point(ultimoRead.x, ultimoRead.y+passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(6);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA 7
			if(e.getKeyCode() == KeyEvent.VK_7) {	
				Point p = new Point(ultimoRead.x+passo, ultimoRead.y+passo);
				if(debug) {
					System.out.println("Anterior x=" + ultimoRead.x + " / y=" + ultimoRead.y);
					System.out.println("Atual x=" + p.x + " / y=" + p.y);
				}
				figura.addSequencia(7);
				ultimoRead = p;
				ultimoDraw = p;
			}
			
			//TECLA ENTER
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	    		// Parar no estado atual
				pararFigura();
			}
			break;
		case 2:
			// FUNCAO DE DESENHAR UM TRIANGULO
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
	// FIM UNUSED
	
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