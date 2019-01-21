package comunicacao.bluetooth.caderneta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.List;

/**
 * Created by Enoque on 04/02/2018.
 */

//classe responsável por gravar todas as vendas realizadas ao longo do dia no BD.

public class VendasDAO {

    private BaseDeDados banco;
    private SQLiteDatabase bd;

    public VendasDAO(Context contexto){

        banco = new BaseDeDados(contexto);

    }

    public long insereVenda(Venda venda){

        long resultado = 0;
        ContentValues valores = new ContentValues();
        bd = banco.getWritableDatabase();
        for(Compra c : venda.getListaDeCompras()) {

            valores.put(BaseDeDados.ID_VENDA, venda.getIdVenda());
            valores.put(BaseDeDados.ID_COMPRA, c.getId());
            valores.put(BaseDeDados.TIPO_DE_CLIENTE, venda.getTipoDeCliente());
            valores.put(BaseDeDados.ID_CLIENTE, venda.getIdDoCliente());
            valores.put(BaseDeDados.DATA_DA_COMPRA, String.valueOf(c.getDataDaCompra()));
            valores.put(BaseDeDados.NOME_PRODUTO, c.getProduto().getNome());
            valores.put(BaseDeDados.PRECO_UNITARIO, c.getProduto().getPrecoDeVenda());
            valores.put(BaseDeDados.QTD, c.getQuantidade());
            valores.put(BaseDeDados.VALOR_TOTAL, c.getCustoTotal());
        }

        resultado = bd.insert(BaseDeDados.TABELA_VENDAS, null, valores);
        bd.close();

        return resultado;

    }

    public Cursor consultaVendasPorCliente(int idDoCliente){

        Cursor cursor;
        bd = banco.getReadableDatabase();
        String campos[] = {BaseDeDados.DATA_DA_COMPRA, BaseDeDados.NOME_PRODUTO, BaseDeDados.PRECO_UNITARIO,
                           BaseDeDados.QTD, BaseDeDados.VALOR_TOTAL};
        String onde = BaseDeDados.ID_CLIENTE + "=" + idDoCliente;
        cursor = bd.query(BaseDeDados.TABELA_VENDAS, campos, onde,null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        bd.close();

        return cursor;
    }

    public Cursor consultaVendasPorPeríodo(Date data){

        return null;
    }

    public Cursor consultaVendasPorTipoDeCliente(boolean tipoDeCliente){

        Cursor cursor;
        bd = banco.getReadableDatabase();
        String campos[] = { BaseDeDados.TIPO_DE_CLIENTE, BaseDeDados.DATA_DA_COMPRA, BaseDeDados.NOME_PRODUTO,
                BaseDeDados.PRECO_UNITARIO, BaseDeDados.QTD, BaseDeDados.VALOR_TOTAL};
        String onde = BaseDeDados.ID_CLIENTE + "=" + tipoDeCliente;
        cursor = bd.query(BaseDeDados.TABELA_VENDAS, campos, onde,null, null, null, null);

        if(cursor != null){

            cursor.moveToFirst();

        }

        bd.close();

        return cursor;
    }

    public Cursor relatorioDeVendas(){

        Cursor cursor;
        bd = banco.getReadableDatabase();
        String campos[] = { BaseDeDados.ID_VENDA, BaseDeDados.ID_COMPRA, BaseDeDados.TIPO_DE_CLIENTE, BaseDeDados.ID_CLIENTE,
                            BaseDeDados.DATA_DA_COMPRA, BaseDeDados.NOME_PRODUTO, BaseDeDados.PRECO_UNITARIO,
                            BaseDeDados.QTD, BaseDeDados.VALOR_TOTAL};
        cursor = bd.query(BaseDeDados.TABELA_VENDAS, campos, null,null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        bd.close();
        return cursor;
    }
}
