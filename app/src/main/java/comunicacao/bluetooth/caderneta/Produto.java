package comunicacao.bluetooth.caderneta;

/**
 * Created by Enoque on 23/10/2017.
 */

public class Produto {

    private long cB; //Código de barras.
    private String nome;
    private String marca;
    private String dataDeValidade;
    private double precoDeCompra;
    private double precoDeVenda;
    private double lucro;
    private int quantidadeEmEstoque;


    /*
    * @param nome é o nome comum do produto.
    * @param marca é a marca do produto, geralmente o nome do fabricante.
    * @data de validade é data de validade do produto, ou seja, até quando ele estará em condições de comercialização.
    * @param precoDeCompra quanto foi pago pelo produto ao fornecedor.
    * @param precoDeVenda ´quento o cliente pagará para adquirir o produto.
     */

    public Produto(){}

    public Produto(long cB, String nome, String marca, String dataDeValidade, double precoDeCompra, double precoDeVenda, int quantidadeEmEstoque){

        this.cB = cB;
        this.nome = nome;
        this.marca = marca;
        this.dataDeValidade = dataDeValidade;
        this.precoDeCompra = precoDeCompra;
        this.precoDeVenda = precoDeVenda;
        lucro = precoDeVenda - precoDeCompra;
        this.quantidadeEmEstoque = quantidadeEmEstoque;

    }

    public void setCB(long cB){
        this.cB = cB;
    }

    public long getCB(){
        return cB;
    }
    public void setNome(String nome){

        this.nome = nome;

    }

    public void setLucro(double lucro){
        this.lucro = lucro;
    }

    public String getNome(){

        return nome;

    }

    public void setMarca(String marca){

        this.marca = marca;

    }

    public String getMarca(){

        return marca;

    }

    public void setDataDeValidade(String dataDeValidade){

        this.dataDeValidade = dataDeValidade;

    }

    public String getDataDeValidade(){

        return dataDeValidade;

    }

    public void setPrecoDeCompra(double precoDeCompra){

        this.precoDeCompra = precoDeCompra;

    }

    public double getPrecoDeCompra(){

        return precoDeCompra;

    }

    public void setPrecoDeVenda(double precoDeVenda){

        this.precoDeVenda = precoDeVenda;

    }

    public double getPrecoDeVenda(){

        return precoDeVenda;

    }

    public double getLucro(){

        return lucro;

    }

    public void setQuantidadeEmEstoque(int quantidadeEmEstoque){

        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public int getQuantidadeEmEstoque(){

        return quantidadeEmEstoque;

    }
    public String toString(){

        return "Nome do Produto: " + nome + "\nMarca: " + "\nPreço de Compra: " +
                precoDeCompra + "\nPreço de venda: " + precoDeVenda +
                "\nLucro: " + lucro;

    }

}
