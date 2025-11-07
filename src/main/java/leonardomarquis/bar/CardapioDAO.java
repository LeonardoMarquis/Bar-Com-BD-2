package leonardomarquis.bar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// usando valitem como preco mesmo aqui
public class CardapioDAO {
    public void add_no_menu(int num, String nome, double preco, int tipo){

        String sql = "INSERT INTO menu (num, nome, preco, tipo) VALUES (?, ?, ?, ?)";

        // Usando try-with-resources para garantir que ocorra mesmo
        try (Connection con = Conexao.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, num);
            stmt.setString(2, nome);
            stmt.setDouble(3, preco);
            stmt.setInt(4, tipo);


            stmt.executeUpdate();
            System.out.println("Item inserido no menu com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir no menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
