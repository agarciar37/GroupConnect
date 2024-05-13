package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionBD;

public class ActivityRequest {
    private int id;
    private Group requestingGroup;
    private Group requestedGroup;
    private Activity activity;
    private int status;

    public ActivityRequest(int id, Group requestingGroup, Group requestedGroup, Activity activity, int status) {
        this.id = id;
        this.requestingGroup = requestingGroup;
        this.requestedGroup = requestedGroup;
        this.activity = activity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Group getRequestingGroup() {
        return requestingGroup;
    }

    public Group getRequestedGroup() {
        return requestedGroup;
    }

    public Activity getActivity() {
        return activity;
    }

    public int getStatus() {
        return status;
    }

    public void acceptRequest() throws SQLException {
        // Cambiar el estado de la solicitud a aceptado (1)
        String query = "UPDATE activity_requests SET status = 1 WHERE id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

        // Unir al grupo solicitante a la actividad
        Group.joinGroup(requestingGroup, activity);
    }

    public void rejectRequest() throws SQLException {
        // Cambiar el estado de la solicitud a rechazado (2)
        String query = "UPDATE activity_requests SET status = 2 WHERE id = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public static List<ActivityRequest> getActivityRequestsForGroup(Group group) throws SQLException {
        List<ActivityRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM activity_requests WHERE requested_group_id = ? AND status = 0"; // Obtener solicitudes pendientes
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, group.getIdGroup());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Activity activity = Activity.getActivityById(resultSet.getInt("activity_id"));
                    Group requestingGroup = Group.getGroupById(resultSet.getInt("requesting_group_id"));
                    ActivityRequest request = new ActivityRequest(
                        resultSet.getInt("id"),
                        requestingGroup,
                        group,
                        activity,
                        resultSet.getInt("status")
                    );
                    requests.add(request);
                }
            }
        }
        return requests;
    }
}
