package leonardomarquis.bar;

public class ContaAberta extends Exception{
    public ContaAberta(){
        super("Conta aberta!");
    }
    public ContaAberta(String msg){
        super(msg);
    }
}
