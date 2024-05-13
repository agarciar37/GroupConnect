package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connection.ConnectionBD;
import err.EmailExistsException;
import err.UsernameExistsException;

public class User {
    private int idUser;
    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;


    public User(int idUser, String name, String surname, String email, String username, String password) {
        this.idUser = idUser;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String name, String surname, String email, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static void createUser(User user) throws SQLException, UsernameExistsException, EmailExistsException {
        // Verificar si el nombre de usuario ya existe
        if (usernameExists(user.getUsername())) {
            throw new UsernameExistsException("El nombre de usuario ya está en uso");
        }
    
        // Verificar si el correo electrónico ya existe
        if (emailExists(user.getEmail())) {
            throw new EmailExistsException("El correo electrónico ya está en uso");
        }
    
        // Si el nombre de usuario y el correo electrónico son únicos, proceder con la inserción
        String query = "INSERT INTO users (name, surname, email, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getUsername());
            statement.setString(5, user.getPassword());
            statement.executeUpdate();
        }
    }
    
    private static boolean usernameExists(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    
    private static boolean emailExists(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static User getUserById(int id) throws SQLException {
        String consulta = "SELECT * FROM users WHERE idUser = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("idUser"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public void leaveGroup(Group group) throws SQLException {
        String query = "DELETE FROM users_groups WHERE user_id = ? AND group_id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, this.idUser);
            statement.setInt(2, group.getIdGroup());
            statement.executeUpdate();
        }
    }

    public void leaveActivity(Activity activity) throws SQLException {
        String query = "DELETE FROM groups_activities WHERE activity_id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, activity.getIdActivity());
            statement.executeUpdate();
        }
    }
}
