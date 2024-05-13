package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import connection.ConnectionBD;


public class Activity {
    private int idActivity;
    private String name;
    private String description;
    private List<Group> groups;
    private int groupId;
    private Date endDate;
    private String place;

    public Activity(int idActivity, String name, String description, List<Group> groups) {
        this.idActivity = idActivity;
        this.name = name;
        this.description = description;
        this.groups = groups;
    }

    public Activity(String name, String description) {
        this.name = name;
        this.description = description;
    } 

    public Activity(int idActivity, String name, String description) {
        this.idActivity = idActivity;
        this.name = name;
        this.description = description;
    }

    public Activity(int idActivity, String name, String description, String place, List<Group> groups) {
        this.idActivity = idActivity;
        this.name = name;
        this.description = description;
        this.place = place;
        this.groups = groups;
    }

    public Activity(String name, String description, String place) {
        this.name = name;
        this.description = description;
        this.place = place;
    }

    public Activity(String name, String description, String place, Date endDate) {
        this.name = name;
        this.description = description;
        this.place = place;
        this.endDate = endDate;
    }

    public Activity(int idActivity, String name, String description, String place) {
        this.idActivity = idActivity;
        this.name = name;
        this.description = description;
        this.place = place;
    }

    public Activity() {
    }

    public int getIdActivity() {
        return idActivity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public static void createActivity(Activity activity, Group group, String place) throws SQLException {
        String query = "INSERT INTO activities (name, description, group_id, place) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, activity.getName());
            statement.setString(2, activity.getDescription());
            statement.setInt(3, group.getIdGroup());
            statement.setString(4, place); // Agregar el nombre del lugar
            statement.executeUpdate();
    
            // Obtener el ID generado para la actividad
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int activityId = generatedKeys.getInt(1);
                activity.setIdActivity(activityId);
            }
        }
    
        // Unir la actividad al grupo
        addGroupToActivity(activity, group);
    }

    public static Activity getActivityByName(String name) throws SQLException {
        Activity activity = null;
        String consulta = "SELECT * FROM activities WHERE name = ?";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    activity = new Activity(
                        rs.getInt("idActivity"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("place"),
                        null // Aquí puedes cargar la lista de grupos si lo deseas
                    );
                }
            }
        }
        return activity;
    }

    public static void addGroupToActivity(Activity activity, Group group) throws SQLException {
        String consulta = "INSERT INTO groups_activities (group_id, activity_id) VALUES (?, ?)";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, group.getIdGroup());
            pstmt.setInt(2, activity.idActivity);
            pstmt.executeUpdate();
        }
    }

    //getActivitiesByGroup
    public static List<Activity> getActivitiesByGroup(Group group) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String consulta = "SELECT * FROM activities WHERE idActivity IN (SELECT activity_id FROM groups_activities WHERE group_id = ?)";
        try (Connection conexion = ConnectionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, group.getIdGroup());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Activity activity = new Activity(
                        rs.getInt("idActivity"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("place"),
                        null // Aquí puedes cargar la lista de grupos si lo deseas
                    );
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    public static List<Activity> getActivitiesNotInGroup(Group group) throws SQLException {
        List<Activity> activities = new ArrayList<>();

        String query = "SELECT * FROM activities WHERE idActivity NOT IN (SELECT activity_id FROM groups_activities WHERE group_id = ?)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, group.getIdGroup());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Activity activity = new Activity();
                    activity.setIdActivity(resultSet.getInt("idActivity"));
                    activity.setName(resultSet.getString("name"));
                    activity.setDescription(resultSet.getString("description"));
                    activities.add(activity);
                }
            }
        }

        return activities;
    }

    public static void sendJoinRequest(User user, Group requestingGroup, Activity activity) throws SQLException {
        String query = "INSERT INTO activity_requests (requesting_group_id, requested_group_id, activity_id, status) VALUES (?, ?, ?, 0)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, requestingGroup.getIdGroup());
            statement.setInt(2, activity.getGroupId()); // Usar el group_id de la actividad
            statement.setInt(3, activity.getIdActivity());
            statement.executeUpdate();
        }
    }
    
    // método para obtener el id del grupo al que pertenece la actividad de la base de datos
    public int getGroupId() throws SQLException {
        int idGroup = 0;
        String query = "SELECT group_id FROM activities WHERE idActivity = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, this.idActivity);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idGroup = resultSet.getInt("group_id");
                }
            }
        }
        return idGroup;
    }

    public int getIdGroupByActivity() throws SQLException {
        int idGroup = 0;
        String query = "SELECT group_id FROM groups_activities WHERE activity_id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, this.idActivity);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idGroup = resultSet.getInt("group_id");
                }
            }
        }
        return idGroup;
    }

    public static Activity getActivityById(int id) throws SQLException {
        Activity activity = null;
        String query = "SELECT * FROM activities WHERE idActivity = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    activity = new Activity(
                        resultSet.getInt("idActivity"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                    );
                }
            }
        }
        return activity;
    }

    public static List<Activity> getActivitiesFromUser(User user) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT a.* FROM activities a INNER JOIN groups_activities ga ON a.idActivity = ga.activity_id " +
                       "INNER JOIN users_groups ug ON ga.group_id = ug.group_id " +
                       "WHERE ug.user_id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, user.getIdUser());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Activity activity = new Activity(
                        resultSet.getInt("idActivity"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                    );
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    public static List<Activity> getActivitiesByGroupAndPlace(Group group, String place) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionBD.obtenerConexion();
            String query = "SELECT * FROM activities WHERE group_id = ? AND place = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, group.getIdGroup()); // Suponiendo que getId() devuelve el ID del grupo
            statement.setString(2, place);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("idActivity");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String activityPlace = resultSet.getString("place");

                // Asumiendo que el constructor de Activity acepta los parámetros nombre, descripción y lugar
                Activity activity = new Activity(id, name, description, activityPlace);
                activities.add(activity);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return activities;
    }

    public static List<Activity> getActivitiesNotInGroupAtPlace(Group group, String place) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT * FROM activities WHERE place = ? AND idActivity NOT IN (SELECT activity_id FROM groups_activities WHERE group_id = ?)";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, place);
            statement.setInt(2, group.getIdGroup());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Activity activity = new Activity();
                    activity.setIdActivity(resultSet.getInt("idActivity"));
                    activity.setName(resultSet.getString("name"));
                    activity.setDescription(resultSet.getString("description"));
                    activities.add(activity);
                }
            }
        }
        return activities;
    }    
}
