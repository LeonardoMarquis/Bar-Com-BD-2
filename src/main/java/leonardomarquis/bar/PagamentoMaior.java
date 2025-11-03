package leonardomarquis.bar;

public class PagamentoMaior extends Exception{
    public PagamentoMaior(){
        super("Pagamento maior!");
    }
    public PagamentoMaior(String msg){
        super(msg);
    }
}
