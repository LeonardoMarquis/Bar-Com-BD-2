package leonardomarquis.bar;

public class ContaInexistente extends Exception{
    public ContaInexistente(){
        super("Conta inexistente!");
    }
    public ContaInexistente(String msg){
        super(msg);
    }
}
