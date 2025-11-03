package leonardomarquis.bar;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public class Bar implements InterfaceBar {

    private List<Conta> contas = new ArrayList<>();
    private List<Item> cardapio = new ArrayList<>();

    // Adiciona um mapa para associar o número da conta ao nome do cliente
    private Map<String, String> clientePorConta = new HashMap<>();

    // --- helpers ---
    private Conta findContaByNumero(int numConta) {
        String key = String.valueOf(numConta);
        for (Conta c : contas) {
            if (c.getNumero().equals(key)) return c;
        }
        return null;
    }

    private Item findItemByNumero(int numItem) {
        String key = String.valueOf(numItem);
        for (Item it : cardapio) {
            if (it.getNumero().equals(key)) return it;
        }
        return null;
    }

    private Item findItemByTipo(int tipo) {
        for (Item it : cardapio) {
            if (it.getTipo() == tipo) return it;
        }
        return null;
    }

    // --- InterfaceBar methods ---
    @Override
    public void abrirConta(int numConta, String cpf, String nomeCliente) throws DadosInvalidos {


        if (numConta <= 0 || cpf.isEmpty() || nomeCliente == null || nomeCliente.isEmpty())
            throw new DadosInvalidos();

        if (findContaByNumero(numConta) != null)
            throw new IllegalArgumentException("Conta já existe");

        Conta conta = new Conta(String.valueOf(numConta), 1);


        // Armazena o nome do cliente para uso futuro no BD ***
        clientePorConta.put(conta.getNumero(), nomeCliente);
        // ************************



        // adiciona o couvert ingresso do titular da conta, nesse caso, e so adiciona 1 porque so tem ele
        // sempre adicionar ingresso: procura o primeiro item do tipo 01
        Item ingresso = findItemByTipo(1); // tipo 01 = ingresso
        if (ingresso != null) {
            conta.adicionarIngresso(ingresso, 1);
        }
        contas.add(conta);
    }

    @Override
    public void addPedido(int numConta, int numItem, int quant)
            throws ContaFechada, ContaInexistente, ItemInexistente, DadosInvalidos {
        if (quant <= 0)
            throw new DadosInvalidos();

        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();
        if (conta.isFechada())
            throw new ContaFechada();

        Item item = findItemByNumero(numItem);
        if (item == null)
            throw new ItemInexistente();

        conta.adicionarItem(item, quant);
    }

    @Override
    public void addPessoasNaConta(int numConta, int quant_pessoas_a_mais) throws ContaInexistente, ContaFechada, DadosInvalidos{
        if (quant_pessoas_a_mais <= 0)
            throw new DadosInvalidos();
        Conta conta = findContaByNumero(numConta);
        if (conta == null){
            throw new ContaInexistente();
        }
        if (conta.isFechada()){
            throw new ContaFechada();
        }

        conta.addPessoas(quant_pessoas_a_mais);     // a conta la vai adicionar as pessoas, e aqui vai dizer quantos ingressos extras vai colocar, e aqui vai mandar adicionar so esse numero de ingressos a mais


        Item ingresso = findItemByTipo(1); // tipo 01 = ingresso
        if (ingresso != null) {
            conta.adicionarIngresso(ingresso, quant_pessoas_a_mais);
        }
    }




    @Override
    public double valorDaConta(int numConta) throws ContaInexistente {
        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();
        return conta.calcularTotal();
    }

    @Override
    public double fecharConta(int numConta) throws ContaInexistente {
        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();
        conta.fechar();



        // *** NOVO: REGISTRA A CONTA NO BANCO DE DADOS APÓS O FECHAMENTO ***
        String numContaStr = String.valueOf(numConta);
        String nomeCliente = clientePorConta.getOrDefault(numContaStr, "Cliente Não Informado");

        ContaDAO dao = new ContaDAO();
        dao.salvarContaFechada(conta, nomeCliente);




        return conta.calcularTotal();
    }

    @Override
    public void addCardapio(int num, String nome, double valItem, int tipo) throws ItemJaCadastrado, DadosInvalidos {
        if (num <= 0 || nome == null || nome.isEmpty() || valItem <= 0)
            throw new DadosInvalidos();

        if (findItemByNumero(num) != null)
            throw new ItemJaCadastrado();

        cardapio.add(new Item(valItem, nome, tipo, String.valueOf(num)));
    }

    @Override
    public void registrarPagamento(int numConta, double val)
            throws PagamentoMaior, ContaInexistente, DadosInvalidos, ContaNaoFechada {
        if (val <= 0)
            throw new DadosInvalidos();

        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();
        if (!conta.isFechada())
            throw new ContaNaoFechada();

        double restante = conta.getRestante();
        if (val > restante)
            throw new PagamentoMaior();

        conta.registrarPagamento(val);


        // ATUALIZA O VALOR PENDENTE NO BANCO DE DADOS
        ContaDAO dao = new ContaDAO();
        dao.atualizarValorPendente(conta.getNumero(), conta.getRestante(), conta.getTotalPago());
    }

    @Override
    public ArrayList<Consumo> extratoDeConta(int numConta) throws ContaInexistente {
        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();

        ArrayList<Consumo> extrato = new ArrayList<>();
        for (Item i : conta.getItens()) {
            extrato.add(new Consumo(i.getDescricao(), i.getValor()));
        }

        double total = conta.calcularTotal();
        extrato.add(new Consumo("TOTAL", total));

        return extrato;
    }







    //Permite consultar o valor restante a ser pago de UMA CONTA FECHADA no BD.
    //Se a conta não for encontrada no histórico, ou se o valor pendente for 0, retorna 0.0.
    //@param numConta O número da conta a ser consultada.
    //@return O valor pendente. Retorna 0.0 se for pago ou não encontrado.
    //
    public double consultarDividaContaFechada(int numConta) {
        String numContaStr = String.valueOf(numConta);

        ContaDAO dao = new ContaDAO();

        // O método no DAO faz o SELECT no BD e garante que o retorno seja >= 0
        double pendente = dao.consultarValorPendente(numContaStr);

        // Não precisamos verificar as contas ativas aqui se o foco é o histórico de dívidas.

        return pendente;
    }






    //Consulta e exibe no terminal todas as contas que foram fechadas e registradas no histórico (BD).

    public void consultarHistoricoContasFechadas() {
        ContaDAO dao = new ContaDAO();
        List<ContaJaFechada> historico = dao.consultarTodasContasFechadas();

        System.out.println("\n=========================================================================================");
        System.out.println("                              HISTÓRICO DE CONTAS FECHADAS (BD)");
        System.out.println("=========================================================================================");

        if (historico.isEmpty()) {
            System.out.println("Nenhum registro de conta fechada encontrado no banco de dados.");
        } else {
            for (ContaJaFechada conta : historico) {
                // O método toString() na classe ContaFechada formata a saída
                System.out.println(conta);
            }
        }
        System.out.println("=========================================================================================");
    }


}