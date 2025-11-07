package leonardomarquis.bar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// usando valitem como preco mesmo aqui
public class PedidoDAO {
    public void add_no_pedido(int numConta, int numItem, int quant){

        String sql = "INSERT INTO pedido (numConta, numItem, quant) VALUES (?, ?, ?)";

        // Usando try-with-resources para garantir que ocorra mesmo
        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, numConta);
            stmt.setInt(2, numItem);
            stmt.setInt(3, quant);


            stmt.executeUpdate();
            System.out.println("Pedido inserido no menu com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir na tabela pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
