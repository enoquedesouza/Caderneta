package comunicacao.bluetooth.caderneta;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Enoque on 26/10/2017.
 */

public class Vendas extends AppCompatActivity {

    private final int CODIGO = 1;
    private Activity contexto = this;
    private final int CB_LIDO = 7;
    private long codigoDeBarras;
    private ArrayList<Produto> listaDeProdutos = new ArrayList<Produto>() ;
    private TableLayout tableDeCompras;
    private TextView precoTotal;
    private double custoTotal = 0.0;
    private ProdutosDAO produtos;


    private Handler bCHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case CB_LIDO:
                        Toast.makeText(contexto, msg.getData().getString("cb"), Toast.LENGTH_LONG).show();
                        codigoDeBarras = Long.parseLong(msg.getData().getString("cb"));
                        consultaCodigo(codigoDeBarras);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState){

            super.onCreate(savedInstanceState);
            setContentView(R.layout.vendas);
            criaProdutos();
            exibeTela();
            tableDeCompras = (TableLayout) findViewById(R.id.tabela1);
            precoTotal = (TextView) findViewById(R.id.totalCompra);



    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void exibeTela(){

        getFragmentManager().beginTransaction()
                .replace(R.id.superficie, FragmentoParaCamera.novaInstancia(bCHandler))
                .commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void consultaCodigo(long codigoDeBarras){

        for(Produto p : listaDeProdutos){
            if(p.getCB() == codigoDeBarras){
                registraCompra(criaCompra(p));
                custoTotal = custoTotal+p.getPrecoDeVenda();
                precoTotal.setText("TOTAL: "+custoTotal);
            }
        }

    }

    public void registraCompra(Compra compra){

            tableDeCompras.addView(compra.representacaoGrafica(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Compra criaCompra(Produto p){

        Compra compra = new Compra(p, 1);

        return compra;

    }

    public void criaProdutos(){
        Produto p1, p2, p3, p4, p5;

        p1 = new Produto(7622300807399l, "Wafer Bis","Lacta", "20/09/2018", 2.60, 5.50, 5);
        p2 = new Produto(7891962026763l, "Bisc. Cookie", "Bauduco", "06/11/2018", 2.42, 3.50, 30);
        p3 = new Produto(7898215151890l, "Leite Integral", "Piracanjuba", "14/06/2018", 1.92, 4.00, 24);
        p4 = new Produto(7896016601217l, "Cocô Ralado", "Ducoco", "05/06/2018", 2.40, 4.00, 8);
        p5 = new Produto(7898027658457l, "Chocolate Granulado", "Camp", "01/12/2018", 2.40, 4.00, 5);
        listaDeProdutos.add(p1);
        listaDeProdutos.add(p2);
        listaDeProdutos.add(p3);
        listaDeProdutos.add(p4);
        listaDeProdutos.add(p5);


    }

}
