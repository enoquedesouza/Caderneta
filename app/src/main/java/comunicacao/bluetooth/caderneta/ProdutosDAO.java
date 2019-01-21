package comunicacao.bluetooth.caderneta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Enoque on 04/02/2018.
 */

public class ProdutosDAO {

    private BaseDeDados banco;
    private SQLiteDatabase bd;

    public ProdutosDAO(Context contexto){

        banco = new BaseDeDados(contexto);
    }

    public long insereProduto(Produto produto){

        long resultado = 0;
        ContentValues valores = new ContentValues();
        bd = banco.getWritableDatabase();
        valores.put(BaseDeDados.CB, produto.getCB());
        valores.put(BaseDeDados.NOME_DO_PRODUTO, produto.getNome());
        valores.put(BaseDeDados.MARCA, produto.getMarca());
        valores.put(BaseDeDados.DATA_DE_VALIDADE, String.valueOf(produto.getDataDeValidade()));
        valores.put(BaseDeDados.PRECO_DE_COMPRA, produto.getPrecoDeCompra());
        valores.put(BaseDeDados.PRECO_DE_VENDA, produto.getPrecoDeVenda());
        valores.put(BaseDeDados.LUCRO, produto.getLucro());
        valores.put(BaseDeDados.QUANTIDADE_EM_ESTOQUE, produto.getQuantidadeEmEstoque());

        resultado = bd.insert(BaseDeDados.TABELA_PRODUTOS, null, valores);
        bd.close();

        return resultado;

    }

    public long excluiProduto(long cb){

        long resultado;
        bd = banco.getWritableDatabase();
        String onde = BaseDeDados.CB + "=" + cb;
        resultado = bd.delete(BaseDeDados.TABELA_PRODUTOS, onde, null);
        bd.close();
        return resultado;
    }

    public long alteraProduto(Produto produto){

        long resultado;
        bd = banco.getWritableDatabase();
        String onde = BaseDeDados.CB + "=" + produto.getCB();
        ContentValues valores = new ContentValues();
        bd = banco.getWritableDatabase();
        valores.put(BaseDeDados.CB, produto.getCB());
        valores.put(BaseDeDados.NOME_DO_PRODUTO, produto.getNome());
        valores.put(BaseDeDados.MARCA, produto.getMarca());
        valores.put(BaseDeDados.DATA_DE_VALIDADE, String.valueOf(produto.getDataDeValidade()));
        valores.put(BaseDeDados.PRECO_DE_COMPRA, produto.getPrecoDeCompra());
        valores.put(BaseDeDados.PRECO_DE_VENDA, produto.getPrecoDeVenda());
        valores.put(BaseDeDados.LUCRO, produto.getLucro());
        valores.put(BaseDeDados.QUANTIDADE_EM_ESTOQUE, produto.getQuantidadeEmEstoque());

        resultado = bd.update(BaseDeDados.TABELA_PRODUTOS, valores, onde, null);

        bd.close();

        return resultado;
    }

    public Produto consultaProduto(long cb){

        Produto p = new Produto();
        Cursor cursor;
        bd = banco.getReadableDatabase();
        String campos[] = { BaseDeDados.CB, BaseDeDados.NOME_DO_PRODUTO, BaseDeDados.MARCA, BaseDeDados.DATA_DE_VALIDADE,
                BaseDeDados.PRECO_DE_COMPRA, BaseDeDados.PRECO_DE_VENDA, BaseDeDados.LUCRO, BaseDeDados.QUANTIDADE_EM_ESTOQUE};
        String onde = BaseDeDados.CB + "=" + cb;
        cursor = bd.query(BaseDeDados.TABELA_VENDAS, campos, onde,null, null, null, null);

        if(cursor != null){

            cursor.moveToFirst();
            p.setCB(Long.parseLong(cursor.getString(cursor.getColumnIndex(BaseDeDados.CB))));
            p.setNome(cursor.getString(cursor.getColumnIndex(BaseDeDados.NOME_DO_PRODUTO)));
            p.setMarca(cursor.getString(cursor.getColumnIndex(BaseDeDados.MARCA)));
            p.setDataDeValidade(cursor.getString(cursor.getColumnIndex(BaseDeDados.DATA_DE_VALIDADE)));
            p.setPrecoDeCompra(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BaseDeDados.PRECO_DE_COMPRA))));
            p.setPrecoDeVenda(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BaseDeDados.PRECO_DE_VENDA))));
            p.setLucro(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BaseDeDados.LUCRO))));
            p.setQuantidadeEmEstoque(Integer.parseInt(cursor.getString(cursor.getColumnIndex(BaseDeDados.QUANTIDADE_EM_ESTOQUE))));
            bd.close();
            return p;

        }else {
            bd.close();
            return null;

        }
    }
}
