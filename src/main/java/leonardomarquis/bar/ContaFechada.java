package leonardomarquis.bar;

public class ContaFechada extends Exception{
    public ContaFechada(){
        super("Conta fechada!");
    }
    public ContaFechada(String msg){
        super(msg);
    }
}
