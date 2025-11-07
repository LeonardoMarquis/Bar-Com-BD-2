package leonardomarquis.bar;


import java.util.*;

public interface InterfaceBar {
    // --- InterfaceBar methods ---

    // tive que colocar a excecao conta aberta, aqui e no abrir conta em Bar.java para poder dar certo no TestBarResumido

    public void abrirConta(int numConta, int cpf, String nomeCliente) throws ContaAberta, ContaInexistente, DadosInvalidos, ContaAberta;
    public void addPedido(int numConta, int numItem, int quant) throws ContaFechada, ContaInexistente, ItemInexistente, DadosInvalidos;
    public double valorDaConta(int numConta) throws ContaInexistente;
    public double fecharConta(int numConta) throws ContaInexistente;
    public void addCardapio(int nu, String n, double x, int tp) throws ItemJaCadastrado, DadosInvalidos;
    public double registrarPagamento(int numConta, double val) throws PagamentoMaior, ContaInexistente, DadosInvalidos;
    public ArrayList<Consumo> extratoDeConta(int numConta) throws ContaInexistente;

    // mais pessoas chegaram! vamos adicionar elas na mesma "mesa" "conta"!!
    void addPessoasNaConta(int numConta, int quant_pessoas_a_mais) throws ContaInexistente, ContaFechada, DadosInvalidos;


}
