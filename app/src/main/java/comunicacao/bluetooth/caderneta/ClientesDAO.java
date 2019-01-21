package comunicacao.bluetooth.caderneta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Enoque on 04/02/2018.
 */

public class ClientesDAO {

    private BaseDeDados banco;
    private SQLiteDatabase bd;

    public ClientesDAO(Context contexto){

        banco = new BaseDeDados(contexto);

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public long insereCliente(Cliente cliente){

        ContentValues valores = new ContentValues();
        long resultado;
        bd = banco.getWritableDatabase();

        valores.put(BaseDeDados.ID, cliente.getId());
        valores.put(BaseDeDados.TIPO_DE_CLIENTE, cliente.getTipo());
        valores.put(BaseDeDados.DATA_DE_CRIACAO, cliente.getDataDeCriacaoFormatada());
        valores.put(BaseDeDados.NOME_DO_CLIENTE, cliente.getNome());
        valores.put(BaseDeDados.RG, cliente.getRg());
        valores.put(BaseDeDados.CPF, cliente.getCpf());
        valores.put(BaseDeDados.ENDERECO, cliente.getEndereco());
        valores.put(BaseDeDados.TELEFONE, cliente.getTelefone());
        valores.put(BaseDeDados.E_MAIL, cliente.getEmail());
        resultado = bd.insert(BaseDeDados.TABElA_CLIENTES, null, valores);
        bd.close();

        return resultado;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long removeCliente(Cliente cliente){

        long resultado;
        bd = banco.getWritableDatabase();
        String onde = BaseDeDados.ID + "=" + cliente.getId();

        resultado = bd.delete(BaseDeDados.TABElA_CLIENTES, onde, null);
        bd.close();

        return resultado;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Cliente consultaCliente(long id){
        Cliente c = new Cliente();
        Cursor cursor;
        bd = banco.getReadableDatabase();
        String campos[] = { BaseDeDados.ID, BaseDeDados.TIPO_DE_CLIENTE, BaseDeDados.DATA_DE_CRIACAO,
                            BaseDeDados.NOME_DO_CLIENTE, BaseDeDados.RG, BaseDeDados.CPF,
                            BaseDeDados.ENDERECO, BaseDeDados.TELEFONE, BaseDeDados.E_MAIL};

        String onde = BaseDeDados.ID + "=" + id;
        cursor = bd.query(BaseDeDados.TABElA_CLIENTES, campos, onde,null, null, null, null);

        if(cursor != null){

            cursor.moveToFirst();
            c.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(BaseDeDados.ID))));
            c.setTipo(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(BaseDeDados.TIPO_DE_CLIENTE))));
            c.setDataEmTexto(cursor.getString(cursor.getColumnIndex(BaseDeDados.DATA_DE_CRIACAO)));
            c.setNome(cursor.getString(cursor.getColumnIndex(BaseDeDados.NOME_DO_CLIENTE)));
            c.setRG(Long.parseLong(cursor.getString(cursor.getColumnIndex(BaseDeDados.RG))));
            c.setCpf(Long.parseLong(cursor.getString(cursor.getColumnIndex(BaseDeDados.CPF))));
            c.setEndereco(cursor.getString(cursor.getColumnIndex(BaseDeDados.ENDERECO)));
            c.setTelefone(Long.parseLong(cursor.getString(cursor.getColumnIndex(BaseDeDados.TELEFONE))));
            c.setEmail(cursor.getString(cursor.getColumnIndex(BaseDeDados.E_MAIL)));
            bd.close();

            return c;

        }else {

            bd.close();
            return null;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long alteraCliente(Cliente cliente){

        long resultado;
        bd = banco.getWritableDatabase();
        String onde = BaseDeDados.ID + "=" + cliente.getId();
        ContentValues valores = new ContentValues();
        bd = banco.getWritableDatabase();
        valores.put(BaseDeDados.ID, cliente.getId());
        valores.put(BaseDeDados.TIPO_DE_CLIENTE, cliente.getTipo());
        valores.put(BaseDeDados.DATA_DE_CRIACAO, cliente.getDataDeCriacaoFormatada());
        valores.put(BaseDeDados.NOME_DO_CLIENTE, cliente.getNome());
        valores.put(BaseDeDados.RG, String.valueOf(cliente.getRg()));
        valores.put(BaseDeDados.CPF, String.valueOf(cliente.getCpf()));
        valores.put(BaseDeDados.ENDERECO, cliente.getEndereco());
        valores.put(BaseDeDados.TELEFONE, String.valueOf(cliente.getTelefone()));
        valores.put(BaseDeDados.E_MAIL, cliente.getEmail());

        resultado = bd.update(BaseDeDados.TABElA_CLIENTES, valores, onde, null);

        bd.close();

        return resultado;
    }
}
