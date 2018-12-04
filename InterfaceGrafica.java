
/**
 *   A classe que cuida da interface grafica com o usuario, especialmente do gerenciamento
 *   dos dois menus (Arquivo e Cores).
 */

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InterfaceGrafica extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Color corBordaAtual, corFundoAtual;
	private PainelDesenho painel; // painel onde o desenho e feito
	private JMenuItem mNovo, mAbrir, mSalvar, mSair, mudaCorInterna, mudaCorDeBorda; // itens de menu
	private String ultimoDiretorioAcessado; // memoriza em qual diretorio houve a ultima operacao de gravacao ou leitura
	private JMenuItem bLivre, bFreeman, bRetangulo, bTriangulo; // botoes da interface para cada funcao
	private JMenuItem bUndo, bRedo, bMarcador;

	/**
	 * Constroi um objeto InterfaceGrafica
	 */
	public InterfaceGrafica() {
		super("MiniDraw2 versao 9.5 - by Grupo 2");
		setSize(750, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		corBordaAtual = Color.BLACK; // Preto
		corFundoAtual = Color.WHITE; // Branco

		JMenu menuArquivo = criaMenuArquivo();
		JMenu menuDeCores = criaMenuDeCores();
		JMenu menuFuncoes = criaMenuFuncoes();
		JMenuBar barraDeMenus = new JMenuBar();
		barraDeMenus.add(menuArquivo);
		barraDeMenus.add(menuDeCores);
		barraDeMenus.add(menuFuncoes);
		setJMenuBar(barraDeMenus);

		painel = new PainelDesenho(this);
		getContentPane().add(painel, BorderLayout.CENTER);

		ultimoDiretorioAcessado = "."; // O ponto faz alusao ao diretorio corrente
	}

	private JMenu criaMenuArquivo() {
		JMenu menuArquivo = new JMenu("Arquivo");
		mNovo = new JMenuItem("Novo Desenho"); // Cria um item de menu para comecar uma nova figura
		mNovo.addActionListener(this); // Configura o ouvinte de acao
		menuArquivo.add(mNovo); // Adiciona o item "Novo Desenho" ao Menu

		mAbrir = new JMenuItem("Abrir");
		mAbrir.addActionListener(this); // Configura o ouvinte de acao
		menuArquivo.add(mAbrir); // Adiciona o item "Abrir" ao Menu

		mSalvar = new JMenuItem("Salvar");
		mSalvar.addActionListener(this); // Configura o ouvinte de acao
		menuArquivo.add(mSalvar); // Adiciona o item "Salvar" ao Menu

		mSair = new JMenuItem("Sair");
		mSair.addActionListener(this); // Configura o ouvinte de acao
		menuArquivo.add(mSair); // Adiciona o item "Salvar" ao Menu

		return menuArquivo;
	}

	private JMenu criaMenuDeCores() {
		JMenu menuDeCores = new JMenu("Opcoes");

		mudaCorInterna = new JMenuItem("Configurar Cor Interna");
		mudaCorInterna.addActionListener(this);
		menuDeCores.add(mudaCorInterna);

		mudaCorDeBorda = new JMenuItem("Configurar Cor de Borda");
		mudaCorDeBorda.addActionListener(this);
		menuDeCores.add(mudaCorDeBorda);

		bMarcador = new JMenuItem("Configurar Marcador de Ponto");
		bMarcador.addActionListener(this);
		menuDeCores.add(bMarcador);

		return menuDeCores;
	}

	private JMenu criaMenuFuncoes() {
		JMenu menuFuncoes = new JMenu("Funcoes");

		// instanciar os botoes
		bLivre = new JMenuItem("Poligono");
		bFreeman = new JMenuItem("Freeman");
		bRetangulo = new JMenuItem("Retangulo");
		bTriangulo = new JMenuItem("Triangulo");
		bUndo = new JMenuItem("Undo");
		bRedo = new JMenuItem("Redo");

		// define os listeners dos botoes
		bLivre.addActionListener(this);
		bFreeman.addActionListener(this);
		bRetangulo.addActionListener(this);
		bTriangulo.addActionListener(this);
		bUndo.addActionListener(this);
		bRedo.addActionListener(this);

		// acrescenta no menu de funcoes
		menuFuncoes.add(bUndo);
		menuFuncoes.add(bRedo);
		menuFuncoes.add(bLivre);
		menuFuncoes.add(bFreeman);
		menuFuncoes.add(bRetangulo);
		menuFuncoes.add(bTriangulo);

		return menuFuncoes;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mNovo) {
			novoArquivo();
		} else if (e.getSource() == mAbrir) {
			abrirArquivo();
		} else if (e.getSource() == mSalvar) {
			salvarArquivo();
		} else if (e.getSource() == mSair) {
			sair();
		} else if (e.getSource() == mudaCorInterna) {
			mudarCorFundo();
		} else if (e.getSource() == mudaCorDeBorda) {
			mudarCorBorda();
		} else if (e.getSource() == bLivre) {
			painel.setFuncao(0);
		} else if (e.getSource() == bFreeman) {
			painel.setFuncao(1);
		} else if (e.getSource() == bRetangulo) {
			painel.setFuncao(2);
		} else if (e.getSource() == bTriangulo) {
			painel.setFuncao(3);
		} else if (e.getSource() == bUndo) {
			painel.undo();
		} else if (e.getSource() == bRedo) {
			painel.redo();
		} else if (e.getSource() == bMarcador) {
			painel.alteraMarcador();
		}
	}

	/**
	 * Metodo para reinicializar todo o desenho corrente
	 */
	private void novoArquivo() {
		String[] opcoes = { "Sim", "Nao" };
		int i = JOptionPane.showOptionDialog(this, "Tem certeza que deseja continuar? A figura atual seria perdida.",
				"Novo Desenho", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
		if (i == JOptionPane.YES_OPTION) {
			corBordaAtual = Color.BLACK;
			corFundoAtual = Color.WHITE;
			// Limpa a pilha de figuras e a tela. Obs.: Verificar array de pontos.
			painel.setPilha(new ArrayList<>());
			painel.setFigura(new Figura());
			painel.clearUltimo();
			painel.repaint();
		}
	}

	/**
	 * Metodo para ler um arquivo de desenho
	 */
	@SuppressWarnings("unchecked")
	private void abrirArquivo() {
		JFileChooser escolhedorDeArquivos = new JFileChooser(ultimoDiretorioAcessado);
		escolhedorDeArquivos.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".mdr") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "*.mdr - Arquivos do MiniDraw2";
			}
		});
		int situacao = escolhedorDeArquivos.showOpenDialog(this);
		if (situacao == JFileChooser.APPROVE_OPTION) {
			ultimoDiretorioAcessado = escolhedorDeArquivos.getCurrentDirectory().toString();
			String nomeDoArquivo = escolhedorDeArquivos.getSelectedFile().getAbsolutePath();
			try {
				FileInputStream fis = new FileInputStream(nomeDoArquivo);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object objPilha = ois.readObject();
				Object objFigura = ois.readObject();
				Object objCorBorda = ois.readObject();
				Object objCorInterna = ois.readObject();
				ois.close();
				fis.close();
				painel.setPilha((ArrayList<Figura>) objPilha);
				painel.setFigura((Figura) objFigura);
				corBordaAtual = (Color) objCorBorda;
				corFundoAtual = (Color) objCorInterna;
				painel.repaint();
			} catch (FileNotFoundException exc) {
				exc.printStackTrace();
			} catch (IOException exc) {
				exc.printStackTrace();
			} catch (ClassNotFoundException exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * Metodo para gravar um arquivo de desenho
	 */
	private void salvarArquivo() {
		String nomeDoArquivo;
		JFileChooser escolhedorDeArquivos = new JFileChooser(ultimoDiretorioAcessado);
		escolhedorDeArquivos.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".mdr") || f.isDirectory();
			}

			public String getDescription() {
				return "*.mdr - Arquivos do MiniDraw2";
			}
		});
		int situacao = escolhedorDeArquivos.showSaveDialog(this);
		if (situacao == JFileChooser.APPROVE_OPTION) {
			ultimoDiretorioAcessado = escolhedorDeArquivos.getCurrentDirectory().toString();
			nomeDoArquivo = escolhedorDeArquivos.getSelectedFile().getAbsolutePath();
			if (!nomeDoArquivo.toLowerCase().endsWith(".mdr")) {
				nomeDoArquivo += ".mdr";
			}
			try {
				FileOutputStream fos = new FileOutputStream(nomeDoArquivo);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(painel.getPilha());
				oos.writeObject(painel.getFigura());
				oos.writeObject(corBordaAtual);
				oos.writeObject(corFundoAtual);
				oos.close();
				fos.close();
			} catch (FileNotFoundException exc) {
				exc.printStackTrace();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		if (situacao == JFileChooser.CANCEL_OPTION) {
			JOptionPane.showMessageDialog(this, "Arquivo nao salvo", "Salvar", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Metodo para encerrar a aplicacao
	 */
	private void sair() {
		String[] opcoes = { "Sim", "N�o" };
		int i = JOptionPane.showOptionDialog(this, "Tem certeza que deseja sair?", "Sair", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
		if (i == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	/**
	 * Exibe a caixa de dialogo de selecao de cores e, em seguida, configura a cor
	 * interna de cada um dos prototipos de figuras. A exibicao e entao redesenhada.
	 */
	private void mudarCorFundo() {
		corFundoAtual = JColorChooser.showDialog(this, "Selecionar Cor Interna", corFundoAtual);
		if (corFundoAtual == null) {
			corFundoAtual = Color.WHITE;
		}
		painel.setCores(2, corFundoAtual);
		repaint();
	}

	/**
	 * Exibe a caixa de dialogo de selecao de cores e, em seguida, configura a cor
	 * de borda de cada um dos prototipos de figuras. A exibicao e entao
	 * redesenhada.
	 */
	private void mudarCorBorda() {
		corBordaAtual = JColorChooser.showDialog(this, "Selecionar Cor de Borda", corBordaAtual);
		if (corBordaAtual == null) {
			corBordaAtual = Color.BLACK;
		}
		painel.setCores(2, corBordaAtual);
		repaint();
	}

	public Color getCorBordaAtual() {
		return corBordaAtual;
	}
	
	public Color getCorFundoAtual() {
		return corFundoAtual;
	}

}
