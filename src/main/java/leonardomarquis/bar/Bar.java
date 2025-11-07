package leonardomarquis.bar;

import java.util.*;

// com as novas atualizacoes do professor em 03/11/25, o ingresso nao vai para o cardapio, logo ele nao é pego do cardapio,
// é contabilizado direto. Cpf será coletado como int, e Bar terá: apagarTudo()
// deve ser para limpar no banco de dados, ja que no inicio de cada teste em "TesteBarResumido" é usado

// Esse Bar.java, ja tem metodos funcionando localmente, e tem certos metodos com atividades no banco de dados
// e tem metodos que mexem exclusivamente com alguma coisa no banco de dados

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
    public void abrirConta(int numConta, int cpf, String nomeCliente) throws DadosInvalidos, ContaAberta{


        if (numConta <= 0 || cpf == 0 || nomeCliente == null || nomeCliente.isEmpty())
            throw new DadosInvalidos();

        if (findContaByNumero(numConta) != null)
            throw new ContaAberta("Conta já existe");



        // procura se ja tem conta com esse numero no banco de dados
        // procura se ja esta cadastrado no BANCO DE DADOS
        ContaDAO dao = new ContaDAO();
        if (dao.contaMesmoNumeroJaCadastrada(numConta)){
            throw new ContaAberta("Ja tem uma conta com esse numero no Banco de Dados");
        }
        // tive que colocar a excecao conta aberta, aqui e no interface para poder dar certo no TestBarResumido


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

    //@Override
    //public void abrirConta(int numConta, int cpf, String nomeCliente) throws ContaAberta, ContaInexistente, DadosInvalidos {

    //}

    // so precisa de @Override se o metodo ja existir na classe mae dessa classe aqui
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

        // para ADD na tabela pedidos do BANCO DE DADOS
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.add_no_pedido(numConta, numItem, quant);


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

        // se a conta ja foi fechada, pega o valor dela que estiver guardado no Bnaco de Dados
        // se ainda nao estiver fechada, pega o valor na memoria
        if (conta.isFechada()){
            ContaDAO dao = new ContaDAO();

            String numContaString = String.valueOf(numConta);
            return dao.consultarValorPendente(numContaString);
        }
        else{
            return conta.calcularTotal();
        }


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
    public void addCardapio(int num, String nome, double valItem, int tipo) throws ItemJaCadastrado, DadosInvalidos{
        if (num <= 0 || nome == null || nome.isEmpty() || valItem <= 0)
            throw new DadosInvalidos();

        // procura se ja esta cadastrado na memoria
        if (findItemByNumero(num) != null)
            throw new ItemJaCadastrado();

        // procura se ja esta cadastrado no BANCO DE DADOS
        CardapioDAO menu_dao = new CardapioDAO();
        if (menu_dao.itemJaCadastrado_no_menu(num)){
            throw new ItemJaCadastrado();
        }

        cardapio.add(new Item(valItem, nome, tipo, String.valueOf(num)));

        // para add no MENU do BANCO DE DADOS
        menu_dao.add_no_menu(num, nome, valItem, tipo);

    }

    @Override
    public double registrarPagamento(int numConta, double val)
            throws PagamentoMaior, ContaInexistente, DadosInvalidos{
        if (val <= 0)
            throw new DadosInvalidos();

        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();
        //if (!conta.isFechada())
            //throw new ContaNaoFechada();
        // e teria que ter no throws aqui, e teria que ter na InterfaceBar tambem

        double restante = conta.getRestante();
        if (val > restante)
            throw new PagamentoMaior();

        conta.registrarPagamento(val);


        // ATUALIZA O VALOR PENDENTE NO BANCO DE DADOS
        ContaDAO dao = new ContaDAO();
        dao.atualizarValorPendente(conta.getNumero(), conta.getRestante(), conta.getTotalPago());

        String numContaString = "" + numConta;
        return dao.consultarValorPendente(numContaString);
    }

    @Override
    public ArrayList<Consumo> extratoDeConta(int numConta) throws ContaInexistente {
        Conta conta = findContaByNumero(numConta);
        if (conta == null)
            throw new ContaInexistente();

        // Mapa para contar itens
        Map<String, Integer> contador = new HashMap<>();
        Map<String, Double> valores = new HashMap<>();

        // Conta quantas vezes cada item aparece e guarda valor
        for (Item i : conta.getItens()) {
            contador.put(i.getDescricao(), contador.getOrDefault(i.getDescricao(), 0) + 1);
            valores.putIfAbsent(i.getDescricao(), i.getValor());
        }

        // Cria lista final sem repetidos mas com quantidade
        ArrayList<Consumo> extrato = new ArrayList<>();
        for (String descricao : contador.keySet()) {
            int qtd = contador.get(descricao);
            double valor = valores.get(descricao);

            // Nome formatado: arroz (x2)
            String descFormatada = descricao + " (x" + qtd + ")";
            extrato.add(new Consumo(descFormatada, valor));
        }

        double total = conta.calcularTotal();
        String total_formatado = String.format("%.2f", total);
        System.out.println("TOTAL: R$" + total_formatado);

        return extrato;
    }



    public ArrayList<Consumo> extratoDeContaExpandido(int numConta) throws ContaInexistente {
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





    // metodo para apagar os dados das tabelas no BANCO DE DADOS
    public void apagarTudo(){
        // é como chamar um DAO como os outros metodos, mas como apagar dados das tabelas é algo bem mais
        // geral, vai ficar nesse ...Service...java

        BarServiceBD serviceBd = new BarServiceBD();
        serviceBd.limparDados();

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