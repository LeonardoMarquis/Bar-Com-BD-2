package leonardomarquis.bar;


import java.util.ArrayList;
import java.util.List;

public class Conta {
    private String numero;
    private int pessoas;
    private boolean fechada = false;
    private List<Item> itens = new ArrayList<>();
    private double totalPago = 0;

    // no caso, quando abre a conta la e cria a conta dentro da abertura, ja vem so com 1 pessoa
    // deixsei assim para nao alterar o metodo original de abrir conta

    public Conta(String numero, int pessoas) throws DadosInvalidos{
        if (numero == null || numero.isEmpty() || pessoas <= 0)
            throw new DadosInvalidos("Dados inválidos");

        this.numero = numero;
        this.pessoas = pessoas;
    }

    public String getNumero() {
        return numero;
    }

    public int getPessoas() {
        return pessoas;
    }

    public boolean isFechada() {
        return fechada;
    }



    public void adicionarItem(Item item, int quantidade) throws DadosInvalidos, ContaFechada {
        if (fechada)
            throw new ContaFechada("Conta fechada");
        if (item == null || quantidade <= 0)
            throw new DadosInvalidos("Dados inválidos");

        for (int i = 0; i < quantidade; i++) {
            itens.add(item);

            // adicionar automaticamente a gorjeta do item
            if (item.getTipo() == 2) { // bebida
                itens.add(new Item(item.getValor() * 0.10, "Gorjeta (bebida)", 0, "00"));
            } else if (item.getTipo() == 3) { // comida
                itens.add(new Item(item.getValor() * 0.15, "Gorjeta (comida)", 0, "00"));
            }

        }
    }

    // quando abre a conta e so adiciona 1 pessoa, la em bar chama esse metodo, agora
    // quando adiciono mais pessoas, vai chamar esse, que ja adiciona de fato para o tanto de pessaos
    // tipo, como ja tinham pessoas antes, tem que adicionar ingressos so para as pessoas que nao estavam
    public void adicionarIngresso(Item ingresso, int pessoas_a_adicionar_ingresso) {
        for (int i = 0; i < pessoas_a_adicionar_ingresso; i++) {
            itens.add(ingresso);
        }
    }


    public void addPessoas(int quant) throws DadosInvalidos, ContaFechada {
        if (fechada){
            throw new ContaFechada("Conta fechada");
        }
        if (quant <= 0){
            throw new DadosInvalidos("Quantidade de pessoas inválida");
        }
        pessoas += quant;

        // agora incrementar valores
        // adicionar ingresso ja adiciona de acordo com o numero de pessoas
        // la em Bar, é que manda adicionar mais ingressos
    }



    public double calcularTotal() {
        double total = 0;
        for (Item i : itens) {
            total += i.getValor();
        }
        return total;
    }

    public void fechar() {
        fechada = true;
    }

    public void registrarPagamento(double valor) {
        if (!fechada)
            throw new IllegalStateException("Conta ainda não fechada");
        if (valor <= 0)
            throw new IllegalArgumentException("Valor inválido");
        double total = calcularTotal();
        if (totalPago + valor > total)
            throw new IllegalArgumentException("Pagamento maior que o valor da conta");
        totalPago += valor;
    }

    public double getRestante() {
        return calcularTotal() - totalPago;
    }

    public List<Item> getItens() {
        return itens;
    }




    public double getTotalPago() {
        return totalPago;
    }


}