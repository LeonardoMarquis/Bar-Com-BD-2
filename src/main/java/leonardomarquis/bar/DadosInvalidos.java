package leonardomarquis.bar;

public class DadosInvalidos extends Exception{
    public DadosInvalidos(){
        super("Dados Inválidos!");
    }
    public DadosInvalidos(String msg){
        super(msg);
    }
}