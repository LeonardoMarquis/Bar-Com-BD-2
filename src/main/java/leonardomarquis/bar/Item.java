package leonardomarquis.bar;


import java.util.Objects;

public class Item {
    private String numero;
    private String descricao;
    private double valor;
    private int tipo; // 00 gorjeta, 01 ingresso, 02 bebida, 03 comida

    public Item(double valor, String descricao, int tipo, String numero) throws DadosInvalidos {
        if (descricao == null || descricao.isEmpty() || valor <= 0)
            throw new DadosInvalidos();
        this.valor = valor;
        this.descricao = descricao;
        this.tipo = tipo;
        this.numero = numero;
    }

    public int getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getNumero() {
        return numero;
    }


}

