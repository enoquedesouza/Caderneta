package comunicacao.bluetooth.caderneta;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TableRow;

import java.util.Date;

/**
 * Created by Enoque on 19/10/2017.
 */

public class Compra {

    private static int id;
    private Calendar dataDaCompra;
    private Produto produto;
    private int quantidade;
    private double precoUnitario;
    private double custoTotal;
    private String dataFormatada;
    private TableRow representacaoGrafica;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)

    //@param tipo deve ser 0 para clientes comuns e 1 para clientes que estão na caderneta
    public Compra(Produto produto, int quantidade){

        id+=1;
        setDataDaCompra();
        this.produto = produto;
        this.quantidade = quantidade;
        setPrecoUnitario(produto.getPrecoDeVenda());
        setCustoTotal(produto, quantidade);

    }

    public int getId(){
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDataDaCompra(){

        dataDaCompra = Calendar.getInstance();
        setDataDaCompraFormatada(dataDaCompra.getTime());

    }

    public Calendar getDataDaCompra(){

        return dataDaCompra;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDataDaCompraFormatada(Date data){

        SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
        this.dataFormatada = dataFormatada.format(data);
    }

    public String getDataFormatada(){

        return dataFormatada;
    }

    public void setProduto(Produto produto){

        this.produto = produto;

    }

    public Produto getProduto(){

        return produto;

    }

    public void setQuantidade(int quantidade){

        this.quantidade = quantidade;

    }

    public int getQuantidade(){

        return quantidade;

    }

    private void setPrecoUnitario(double precoUnitario){

        this.precoUnitario = precoUnitario;

    }

    private double getPrecoUnitario(){

        return precoUnitario;
    }

    private void setCustoTotal(Produto produto, int quantidade){

        custoTotal = produto.getPrecoDeVenda()*quantidade;

    }

    public double getCustoTotal(){

        return custoTotal;

    }

    public TableRow representacaoGrafica(Context context){

        TagPadrao c_data = new TagPadrao(context);
        TagPadrao c_produto = new TagPadrao(context);
        TagPadrao c_precoUnit = new TagPadrao(context);
        TagPadrao c_quantidade = new TagPadrao(context);
        TagPadrao c_custoTotal = new TagPadrao(context);

        c_data.setText(getDataFormatada());
        c_produto.setText(produto.getNome());
        c_precoUnit.setText(String.valueOf(getPrecoUnitario()));
        c_quantidade.setText(String.valueOf(getQuantidade()));
        c_custoTotal.setText(String.valueOf(getCustoTotal()));

        representacaoGrafica = new TableRow(context);
        representacaoGrafica.addView(c_data);
        representacaoGrafica.addView(c_produto);
        representacaoGrafica.addView(c_precoUnit);
        representacaoGrafica.addView(c_quantidade);
        representacaoGrafica.addView(c_custoTotal);

        return representacaoGrafica;

    }

}
