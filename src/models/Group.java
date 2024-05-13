package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionBD;

public class Group {
    private int idGroup;
    private String name;
    private String description;
    private List<User> users;
    private List<Activity> activities;

    public Group(int idGroup, String name, String description, List<User> users, List<Activity> activities) {
        this.idGroup = idGroup;
        this.name = name;
        this.description = description;
        this.users = users;
        this.activities = activities;
    }

    public Group(String name, String description, List<User> users) {
        this.name = name;
        this.description = description;
        this.users = users;
    }

    public Group(int idGroup){
        this.idGroup = idGroup;
    }

    public Group(int idGroup, String name) {
        this.idGroup = idGroup;
        this.name = name;
    }

    public int getIdGroup() {
        // retornar el id del grupo en la base de datos
        return idGroup;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public static void createGroup(Group group, List<User> user) throws SQLException {
        String consulta = "INSERT INTO groups (name, description) VALUES (?, ?)";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, group.getName());
            pstmt.setString(2, group.getDescription());
            pstmt.executeUpdate();
        }
        // Añadir al usuario que ha creado el grupo
        Group groupCreated = getGroupByName(group.getName());
        addUserToGroup(groupCreated, user);
    }

    

    // unirse a un grupo
    public static void joinGroup(Group group, User user) throws SQLException {
        String consulta = "INSERT INTO users_groups (group_id, user_id) VALUES (?, ?)";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, group.idGroup);
            pstmt.setInt(2, user.getIdUser());
            pstmt.executeUpdate();
        }
    }

    public static Group getGroupByName(String name) throws SQLException {
        Group group = null;
        String consulta = "SELECT * FROM groups WHERE name = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    group = new Group(
                        rs.getInt("idGroup"),
                        rs.getString("name"),
                        rs.getString("description"),
                        null, // Aquí puedes cargar la lista de usuarios si lo deseas
                        null  // Aquí puedes cargar la lista de actividades si lo deseas
                    );
                }
            }
        }
        return group;
    }

    public static void addUserToGroup(Group group, List<User> users) throws SQLException {
        String consulta = "INSERT INTO users_groups (group_id, user_id) VALUES (?, ?)";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            for (User user : users) {
                pstmt.setInt(1, group.idGroup);
                pstmt.setInt(2, user.getIdUser());
                pstmt.executeUpdate();
            }
        }
    }

    // getGroupFromUser
    public static List<Group> getGroupsFromUser(User user) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String consulta = "SELECT g.idGroup, g.name, g.description FROM groups g INNER JOIN users_groups ug ON g.idGroup = ug.group_id WHERE ug.user_id = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, user.getIdUser());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Group group = new Group(
                        rs.getInt("idGroup"),
                        rs.getString("name"),
                        rs.getString("description"),
                        null, // Aquí puedes cargar la lista de usuarios si lo deseas
                        null  // Aquí puedes cargar la lista de actividades si lo deseas
                    );
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    public static Group getGroupById(int idGroup) throws SQLException {
        Group group = null;
        String consulta = "SELECT * FROM groups WHERE idGroup = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, idGroup);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    group = new Group(
                        rs.getInt("idGroup"),
                        rs.getString("name"),
                        rs.getString("description"),
                        null, // Aquí puedes cargar la lista de usuarios si lo deseas
                        null  // Aquí puedes cargar la lista de actividades si lo deseas
                    );
                }
            }
        }
        return group;
    }

    //getGroupsByUser
    public static List<Group> getGroupsByUser(User user) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String consulta = "SELECT g.idGroup, g.name, g.description FROM groups g INNER JOIN users_groups ug ON g.idGroup = ug.group_id WHERE ug.user_id = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, user.getIdUser());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Group group = new Group(
                        rs.getInt("idGroup"),
                        rs.getString("name"),
                        rs.getString("description"),
                        null, // Aquí puedes cargar la lista de usuarios si lo deseas
                        null  // Aquí puedes cargar la lista de actividades si lo deseas
                    );
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    // getUsersInGroup
    public static List<User> getUsersInGroup(Group group) throws SQLException {
        List<User> users = new ArrayList<>();
        String consulta = "SELECT u.idUser, u.name, u.surname, u.email, u.username, u.password FROM users u INNER JOIN users_groups ug ON u.idUser = ug.user_id WHERE ug.group_id = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, group.getIdGroup());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("idUser"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                    users.add(user);
                }
            }
        }
        return users;
    }

    public static void joinGroup(Group group, Activity activity) throws SQLException {
        String query = "INSERT INTO groups_activities (group_id, activity_id) VALUES (?, ?)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, group.getIdGroup());
            statement.setInt(2, activity.getIdActivity());
            statement.executeUpdate();
        }
    }
    
    public static List<Group> getGroupsNotJoinedToActivity(Activity activity) throws SQLException {
        List<Group> groups = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionBD.obtenerConexion();
            String query = "SELECT * FROM groups WHERE id_group NOT IN (SELECT id_group FROM group_activity WHERE id_activity = ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, activity.getIdActivity()); // Suponiendo que hay un método getId() en la clase Activity para obtener el ID de la actividad
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_group");
                String name = rs.getString("name");
                // Otros campos de grupo...

                Group group = new Group(id, name); // Suponiendo que hay un constructor en la clase Group para crear un objeto Group
                groups.add(group);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return groups;
    }
}
