package leonardomarquis.bar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BarServiceBD {

    public void limparDados(){

        String[] tabelas = {"pedido", "menu", "contas_fechadas"};

        try (Connection con = Conexao.getConnection();
             Statement stmt = con.createStatement()) {

            // uma trnasacao
            con.setAutoCommit(false);

            for (String tabela : tabelas){
                stmt.addBatch("DELETE FROM " + tabela);
            }
            stmt.executeBatch();
            con.commit();

            System.out.println("Dados apagados com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro em apagar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
