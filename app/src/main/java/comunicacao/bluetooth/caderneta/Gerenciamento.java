package comunicacao.bluetooth.caderneta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Enoque on 23/02/2018.
 */

public class Gerenciamento extends AppCompatActivity {

    private ImageButton gerenciaProdutos, gerenciaClientes, gerenciaVendas;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento);

        gerenciaProdutos = (ImageButton) findViewById(R.id.gerencia_produtos);
        gerenciaClientes = (ImageButton) findViewById(R.id.gerencia_clientes);
        gerenciaVendas = (ImageButton) findViewById(R.id.gerencia_vendas);

        gerenciaProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerenciarProdutos();
            }
        });

        gerenciaClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerenciarClientes();
            }
        });

        gerenciaVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerenciarVendas();
            }
        });

    }

    public void gerenciarProdutos(){

        Intent intent = new Intent(this, GerenciaProdutos.class);
        startActivity(intent);
    }

    public void gerenciarClientes(){

        Intent intent = new Intent(this, GerenciaClientes.class);
        startActivity(intent);
    }

    public void gerenciarVendas(){

        Intent intent = new Intent(this, GerenciaVendas.class);
        startActivity(intent);
    }
}
