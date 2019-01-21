package comunicacao.bluetooth.caderneta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Enoque on 01/02/2018.
 */

public class BaseDeDados extends SQLiteOpenHelper {

    private static final String NOME_DO_BANCO = "merceariasouza.db";

    static final String TABELA_PRODUTOS = "produtos";
    static final String CB = "cb";
    static final String NOME_DO_PRODUTO ="nome";
    static final String MARCA = "marca";
    static final String DATA_DE_VALIDADE ="data_de_validade";
    static final String PRECO_DE_COMPRA = "preco_de_compra";
    static final String PRECO_DE_VENDA = "preco_de_venda";
    static final String LUCRO = "lucro";
    static final String QUANTIDADE_EM_ESTOQUE = "quantidade_em_estoque";

    static final String TABElA_CLIENTES = "clientes";
    static final String ID = "id";
    static final String TIPO_DE_CLIENTE = "tipo_de_cliente"; // 0 para um cliente comum 1 para um cliente que esta na caderneta
    static final String DATA_DE_CRIACAO = "data_de_criacao";
    static final String NOME_DO_CLIENTE = "nome";
    static final String RG = "rg";
    static final String CPF = "cpf";
    static final String ENDERECO = "endereco";
    static final String TELEFONE = "telefone";
    static final String E_MAIL = "email";


    static final String TABELA_VENDAS = "vendas";
    static final String ID_VENDA = "id_venda";
    static final String ID_COMPRA = "id_compra";
    private static final String TIPO_CLIENTE = "tipo_cliente";
    static final String ID_CLIENTE = "id_cliente";
    static final String DATA_DA_COMPRA = "data_da_compra";
    static final String NOME_PRODUTO = "produto";
    static final String PRECO_UNITARIO = "preco_unitario";
    static final String QTD = "quantidade";
    static final String VALOR_TOTAL = "valor_total";

    private static int versao;

    public BaseDeDados(Context context) {

        super(context, NOME_DO_BANCO, null, versao);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql1 = "CREATE TABLE " + TABELA_PRODUTOS + "("
                        + CB + " bigint primary key, "
                        + NOME_DO_PRODUTO + " varchar, "
                        + MARCA + " varchar, "
                        + DATA_DE_VALIDADE + " date, "
                        + PRECO_DE_COMPRA + " float, "
                        + PRECO_DE_VENDA + " float, "
                        + LUCRO + " float,"
                        + QUANTIDADE_EM_ESTOQUE + " int" +")";

        String sql2 = "CREATE TABLE " + TABElA_CLIENTES + "("
                        + ID + " int primary key,"
                        + TIPO_DE_CLIENTE + "bit, "
                        + DATA_DE_CRIACAO +"date, "
                        + NOME_DO_CLIENTE + " varchar, "
                        + RG + " bigint, "
                        + CPF + " bigint, "
                        + ENDERECO +"varchar, "
                        + TELEFONE + "bigint, "
                        + E_MAIL + "varchar )";

        String sql3 = "CREATE TABLE " + TABELA_VENDAS +"("
                        + ID_VENDA +"int primary key, "
                        + ID_COMPRA + "int, "
                        + TIPO_CLIENTE + "bit, "
                        + ID_CLIENTE + "int, "
                        + DATA_DA_COMPRA + "date, "
                        + NOME_PRODUTO + "varchar, "
                        + PRECO_UNITARIO + "float, "
                        + QTD + "int, "
                        + VALOR_TOTAL + "float";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABElA_CLIENTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_VENDAS);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_PRODUTOS);

            onCreate(db);

    }
}
