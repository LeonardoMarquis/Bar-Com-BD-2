package leonardomarquis.bar;

public class ContaJaCadastrada extends Exception{
    public ContaJaCadastrada(){super("Conta já cadastrada!");}
    public ContaJaCadastrada(String msg){super(msg);}
}
