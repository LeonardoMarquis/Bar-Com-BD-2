
import leonardomarquis.bar.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;


public class BarTestes {

    @Test
    public void AtendimentoNormal() throws DadosInvalidos, ContaInexistente, ContaFechada, ItemInexistente, ContaNaoFechada, PagamentoMaior, ItemJaCadastrado {
        Bar bar1 = new Bar();
        Item ingresso = new Item(2.50, "Ingresso", 1, "01");
        Item terere_300ml =  new Item(8, "Tereré 300ml", 2, "05");
        Item vaca_preta_300ml =  new Item(8, "Vaca Preta 300ml", 2, "06");
        Item batata_300g =  new Item(12, "Batata 300g", 3, "12");

        bar1.addCardapio(1, "ingresso", 2.50, 1);
        bar1.addCardapio(5, "terere_300ml", 8, 2);
        bar1.addCardapio(6, "vaca_preta_300ml", 8, 2);
        bar1.addCardapio(12, "batata_300g", 12, 3);
        //-------------------------------



        bar1.abrirConta(1, "2222222222", "Luiz");
        bar1.addPessoasNaConta(1, 3);

        bar1.addPedido(1, 6, 2);
        bar1.addPedido(1, 5, 2);
        bar1.addPedido(1, 12, 2);

        double pagamento = bar1.valorDaConta(1);

        bar1.fecharConta(1);
        bar1.registrarPagamento(1, pagamento);

        System.out.println(bar1.extratoDeConta(1));
    }
    //por enquanto para pagar uma conta deixada para depois, tem que rodar o mesmo teste em que ela foi criada, mais adiante é bom ter como pegar direto do banco para pagar depois, porque se nao so daria para pagar enquanto ela esta na memoria da maquina

    @Test
    public  void ConsultarDivida_em_ContaFechada() throws DadosInvalidos, ItemJaCadastrado {
        Bar bar1 = new Bar();
        Item ingresso = new Item(2.50, "Ingresso", 1, "01");
        Item terere_300ml =  new Item(8, "Tereré 300ml", 2, "05");
        Item vaca_preta_300ml =  new Item(8, "Vaca Preta 300ml", 2, "06");
        Item batata_300g =  new Item(12, "Batata 300g", 3, "12");

        bar1.addCardapio(1, "ingresso", 2.50, 1);
        bar1.addCardapio(5, "terere_300ml", 8, 2);
        bar1.addCardapio(6, "vaca_preta_300ml", 8, 2);
        bar1.addCardapio(12, "batata_300g", 12, 3);
        //-------------------------------

        System.out.println(bar1.consultarDividaContaFechada(1));
    }








    @Test
    public  void ConsultarTodasAsContas() throws DadosInvalidos, ItemJaCadastrado {
        Bar bar1 = new Bar();
        Item ingresso = new Item(2.50, "Ingresso", 1, "01");
        Item terere_300ml =  new Item(8, "Tereré 300ml", 2, "05");
        Item vaca_preta_300ml =  new Item(8, "Vaca Preta 300ml", 2, "06");
        Item batata_300g =  new Item(12, "Batata 300g", 3, "12");

        bar1.addCardapio(1, "ingresso", 2.50, 1);
        bar1.addCardapio(5, "terere_300ml", 8, 2);
        bar1.addCardapio(6, "vaca_preta_300ml", 8, 2);
        bar1.addCardapio(12, "batata_300g", 12, 3);
        //-------------------------------

        // talvez fosse bom so retornar, mesmo, e fazer o system.out printl aqui
        bar1.consultarHistoricoContasFechadas();
    }
}
