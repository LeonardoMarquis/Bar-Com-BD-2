package leonardomarquis.bar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Para usar NOW() no SQL ou data atual do Java


import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class ContaDAO {


    public void salvarContaFechada(Conta conta, String nomeCliente) {

        String sql = "INSERT INTO contas_fechadas (numero_conta, nome_cliente, data_fechamento, valor_total, valor_pago, valor_pendente) " +
                "VALUES (?, ?, NOW(), ?, ?, ?)";

        // Usando try-with-resources para garantir que a conexão seja fechada automaticamente
        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            double valorTotal = conta.calcularTotal();
            double valorPago = conta.getTotalPago();
            double valorPendente = conta.getRestante();

            stmt.setString(1, conta.getNumero());
            stmt.setString(2, nomeCliente);
            stmt.setDouble(3, valorTotal);
            stmt.setDouble(4, valorPago);
            stmt.setDouble(5, valorPendente);

            stmt.executeUpdate();
            System.out.println("Conta " + conta.getNumero() + " registrada no banco de dados.");

        } catch (SQLException e) {
            // Caso tente salvar uma conta já existente (UNIQUE em numero_conta)
            if (e.getErrorCode() == 1062) { // Código de erro para chave duplicada no MySQL
                System.err.println("Atenção: Conta " + conta.getNumero() + " já registrada. Ignorando.");
            } else {
                System.err.println("Erro ao salvar conta fechada no BD: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    // Atualiza o valor pendente de uma conta no banco de dados após um pagamento.
    // chamo apos o pagamento ser registrado na Conta.java.

    public void atualizarValorPendente(String numeroConta, double novoValorPendente, double novoValorPago) {

        String sql = "UPDATE contas_fechadas SET valor_pendente = ?, valor_pago = ? WHERE numero_conta = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Garante que o pendente não seja negativo
            double pendenteAtualizado = Math.max(0.0, novoValorPendente);

            stmt.setDouble(1, pendenteAtualizado);
            stmt.setDouble(2, novoValorPago);
            stmt.setString(3, numeroConta);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Conta " + numeroConta + " atualizada no BD. Pendente: R$" + String.format("%.2f", pendenteAtualizado));
            } else {
                System.err.println("Atenção: Nenhuma conta foi atualizada. A conta " + numeroConta + " pode não ter sido fechada e registrada.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar valor pendente no BD: " + e.getMessage());
            e.printStackTrace();
        }
    }




    /**
     * Registra a conta no banco de dados quando ela é fechada.
     */
    public double consultarValorPendente(String numeroConta) {

        // Buscamos o valor_pendente de QUALQUER conta fechada, seja ela paga ou não.
        // Se a conta estiver paga, valor_pendente será 0.00.
        String sql = "SELECT valor_pendente FROM contas_fechadas WHERE numero_conta = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double pendente = rs.getDouble("valor_pendente");
                    // Retorna o valor exato. Se for 0, é porque está paga.
                    return Math.max(0.0, pendente);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao consultar valor pendente: " + e.getMessage());
            e.printStackTrace();
        }

        // Retorna 0.0 se a conta não for encontrada (não foi fechada ainda ou não existe).
        return 0.0;
    }





    /**
     * Retorna a lista de todas as contas fechadas do histórico (BD).
     */
    public List<ContaJaFechada> consultarTodasContasFechadas() {
        List<ContaJaFechada> historico = new ArrayList<>();
        String sql = "SELECT numero_conta, nome_cliente, valor_total, valor_pago, valor_pendente, data_fechamento FROM contas_fechadas ORDER BY data_fechamento DESC";

        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { // Não precisa de parâmetros para o SELECT *

            while (rs.next()) {
                // Converte o java.sql.Timestamp (retornado do MySQL DATETIME) para LocalDateTime
                LocalDateTime dataFech = rs.getTimestamp("data_fechamento").toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

                ContaJaFechada conta = new ContaJaFechada(
                        rs.getString("numero_conta"),
                        rs.getString("nome_cliente"),
                        rs.getDouble("valor_total"),
                        rs.getDouble("valor_pago"),
                        rs.getDouble("valor_pendente"),
                        dataFech
                );
                historico.add(conta);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao consultar todas as contas fechadas: " + e.getMessage());
            e.printStackTrace();
        }

        return historico;
    }




    public boolean contaMesmoNumeroJaCadastrada(int numConta) {

        String sql = "SELECT 1 FROM contas_fechadas WHERE numero_conta = ? LIMIT 1";

        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Converte int → String automaticamente
            stmt.setString(1, String.valueOf(numConta));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();  // Se encontrou, já existe
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar conta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
