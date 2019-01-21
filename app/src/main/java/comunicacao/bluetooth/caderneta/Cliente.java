package comunicacao.bluetooth.caderneta;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

/**
 * Created by Enoque on 19/10/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Cliente {

    private static int id;//8param id é um código identificador único composto pela data de criação do cliente na ordem
                    // aaaa/mm/dd/cc e o número de instâncias criadas.
    private boolean tipo;
    private String nome;
    private Calendar dataDeCriacao;
    private long rg;
    private long cpf;
    private String endereco;
    private long telefone;
    private String email;
    private Date data;
    private SimpleDateFormat dataFormatada;
    private String dataEmTexto;

    /*
    * @param nome é o nome do cliente que será criado.
    * @param tipo é o parâmetro que identifica se o cliente está na caderneta ou não. se for 0, ou false, o cliente não está
    * na caderneta, do contrário está.
     */

    public Cliente(){}

    public Cliente(boolean tipo, String nome, long rg, long cpf, String endereco, long telefone, String email){

        this.id = id + 1;
        this.tipo = tipo;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        setDataDeCriacao();

    }

    public void setId(int id){
        this.id = id;
    }


    public void setTipo(boolean tipo){
        this.tipo = tipo;
    }

    public boolean getTipo(){
        return tipo;
    }

    public void setNome(String nome){

        this.nome = nome;
    }

    public String getNome(){

        return nome;

    }

    private void setDataDeCriacao(){

        dataDeCriacao = Calendar.getInstance();
        data = dataDeCriacao.getTime();

    }

    public String getDataDeCriacaoFormatada(){

        dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
        dataEmTexto = dataFormatada.format(data);

        return dataEmTexto;
    }

    public void setDataEmTexto(String data){
        dataEmTexto = data;
    }

    public Date getDataDeCriacao(){

        return data;
    }

    public void setEndereco(String end){
        endereco = end;
    }


    public long getId(){

        return id;

    }

    public String getEndereco(){
        return endereco;
    }

    public long getTelefone(){
        return telefone;
    }

    public void setTelefone(long telefone){
        this.telefone = telefone;
    }

    public void  setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setRG(long rg){
        this.rg = rg;
    }

    public long getRg(){
        return rg;
    }

    public void setCpf(long cpf){
        this.cpf = cpf;
    }

    public long getCpf(){
        return cpf;
    }

    public String toString(){

        return "/nIdentificador: " + id + "\n" +
                "Nome: " + nome + "\n" +
                "RG: " + rg + "\n" +
                "CPF: " + cpf + "\n" +
                "Endereço: " + endereco + "\n" +
                "Telefone: " + telefone + "\n" +
                "E-mail: " + email + "\n" +
                "Data de Criação: " + getDataDeCriacaoFormatada() ;

    }

}
