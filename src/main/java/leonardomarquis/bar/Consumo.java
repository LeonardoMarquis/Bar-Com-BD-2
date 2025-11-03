package leonardomarquis.bar;

public class Consumo {
    private String descricao;
    private double valor;

    public Consumo(String descricao, double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return descricao + " - R$" + valor;
    }
}