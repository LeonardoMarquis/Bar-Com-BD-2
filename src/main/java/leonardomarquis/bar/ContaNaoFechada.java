package leonardomarquis.bar;

public class ContaNaoFechada extends Exception {
    public ContaNaoFechada(){
        super("Conta não fechada!");
    }
    public ContaNaoFechada(String msg){
        super(msg);
    }
}
