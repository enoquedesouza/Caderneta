package comunicacao.bluetooth.caderneta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> data, produto, quantidade, preco_unit, preco_tot;
    private ArrayAdapter<TextView> lista, lista1;
    private ArrayAdapter<Produto> listaDeProdutos;
    private ListView informacoes, l_produto, l_quantidade, l_preco_unit, l_preco_tot;
    private TagPadrao datap, produtop, quantidadep, preco_unitp, preco_totap;
    private TableRow linha;
    private TableLayout tabela;
    private XmlPullParser parser;
    private AttributeSet atributos;
    private Calendar calendario;
    private DateFormat formatoData;
    private SimpleDateFormat formata;
    private RelativeLayout relative1, relative2, relative3;
    private Drawable back;
    private ImageButton vendas, caderneta,fluxo_de_caixa, gerenciamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vendas = (ImageButton) findViewById(R.id.vendas);
        caderneta = (ImageButton) findViewById(R.id.caderneta);
        fluxo_de_caixa = (ImageButton) findViewById(R.id.fluxo_de_caixa);
        gerenciamento = (ImageButton) findViewById(R.id.image_gerenciamento);


        relative1 = (RelativeLayout) findViewById(R.id.relative1);
        relative2 = (RelativeLayout) findViewById(R.id.relative2);
        relative3 = (RelativeLayout) findViewById(R.id.relative3);


        vendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vendas();
            }
        });;
        caderneta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caderneta();
            }
        });
        fluxo_de_caixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fluxoDeCaixa();
            }
        });
        gerenciamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerenciar();
            }
        });
    }

    public void vendas(){

        Intent vendas = new Intent(this, Vendas.class);
        startActivity(vendas);


    }
    public void caderneta(){

        Intent caderneta = new Intent(this, Caderneta.class);
        startActivity(caderneta);
    }

    public void fluxoDeCaixa(){

        Intent fluxoDeCaixa = new Intent(this, FluxoDeCaixa.class);
        startActivity(fluxoDeCaixa);
    }

    public void gerenciar(){

        Intent gerencia = new Intent(this, Gerenciamento.class);
        startActivity(gerencia);
    }


}
