package leonardomarquis.bar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // ⚠️ ATENÇÃO: Substitua pelos seus dados!
    private static final String URL = "jdbc:mysql://localhost:3306/bar_banco?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root"; // Ex: root
    private static final String SENHA = "mysql250925";

    public static Connection getConnection() {
        try {
            // Opcional para versões mais novas do JDBC, mas é bom manter
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            // O driver não foi encontrado
            throw new RuntimeException("Driver JDBC do MySQL não encontrado!", e);
        } catch (SQLException e) {
            // Erro ao conectar
            throw new RuntimeException("Erro ao conectar ao banco de dados!", e);
        }
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}