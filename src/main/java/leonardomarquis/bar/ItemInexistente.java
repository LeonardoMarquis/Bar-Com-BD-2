package leonardomarquis.bar;

public class ItemInexistente extends Exception{
    public ItemInexistente(){
        super("Item inexistente!");
    }
    public ItemInexistente(String msg){
        super(msg);
    }
}


