
/**
 * A classe que dispara a aplicacao pela instanciacao de um objeto InterfaceGrafica.
 *
 * @author  (Grupo 2) Bruno Teixeira Gama, Guilherme Sant’Clair Alves dos Santos, Luis Felipe R. Lima, Raiza Morata,
 *          Rafael Cassiolato e Júlio César Siqueira, com base no material fornecido pelo professor Carlos Menezes,
 *                      simplificando um projeto do livro "Objetos, Abstracao,
 * 			Estruturas de Dados e Projeto usando Java versao 5.0", de Elliot B. Koffman
 * 			e Paul A. T. Wolfgang, Editora LTC.
 * @see InterfaceGrafica
 */

public class MiniDraw2 {

    /**
     * Metodo main. Esse metodo instancia um objeto da classe InterfaceGrafica e
     * o exibe.
     *
     * @param args Um vetor de Strings (nao usado)
     */
    public static void main(String args[]) {
        InterfaceGrafica aplicacaoDesenho = new InterfaceGrafica();
        aplicacaoDesenho.setVisible(true);
    }
}
