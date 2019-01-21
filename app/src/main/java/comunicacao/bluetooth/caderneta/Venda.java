package comunicacao.bluetooth.caderneta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Enoque on 04/02/2018.
 */

public class Venda {

    private static int idVenda;
    private boolean tipoDeCliente;
    private int idDoCliente;
    private List<Compra> compras = new ArrayList<Compra>();
    private double valorTotal;

    public Venda(){

        idVenda+= 1;

    }

    public int getIdVenda(){
        return  idVenda;
    }

    public void  setTipoDeCliente(boolean tipo){
        tipoDeCliente = tipo;
    }

    public boolean getTipoDeCliente(){
        return tipoDeCliente;
    }

    public void setIdDoCliente(int idDoCliente){
        this.idDoCliente = idDoCliente;
    }

    public int getIdDoCliente(){
        return idDoCliente;
    }

    public void adicionaCompra(Compra compra){
        compras.add(compra);
    }

    public void calculaValorTotal(Double precoCompra){

        valorTotal = valorTotal + precoCompra;

    }

    public List<Compra> getListaDeCompras(){

        return compras;

    }

    public void setValorTotal(double valorTotal) {

        this.valorTotal = valorTotal;

    }

    public double getValorTotal() {

        return valorTotal;

    }
}
