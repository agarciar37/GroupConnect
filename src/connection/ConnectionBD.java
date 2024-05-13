package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/groupconnect";
    private static final String USUARIO = "gc";
    private static final String CONTRASENA = "gc";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
    
}
