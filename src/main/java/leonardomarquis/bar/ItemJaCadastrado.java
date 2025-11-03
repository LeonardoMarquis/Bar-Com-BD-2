package leonardomarquis.bar;

public class ItemJaCadastrado extends Exception{
    public ItemJaCadastrado(){
        super("Item ja cadastrado!");
    }
    public ItemJaCadastrado(String msg){
        super(msg);
    }
}
