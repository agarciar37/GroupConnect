package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import models.*;

public class ManageActivityRequestsPanel extends JPanel {
    private User user;
    private JPanel requestsPanel;

    public ManageActivityRequestsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Aceptar Solicitudes de Actividad");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadRequests();

        JButton backButton = new JButton("Volver");
        backButton.setBackground(new Color(200, 50, 50));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cambiarPanel(new WelcomePanel(user.getIdUser()));
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private void loadRequests() {
        try {
            List<Group> groups = Group.getGroupsFromUser(user);
            for (Group group : groups) {
                List<ActivityRequest> requests = ActivityRequest.getActivityRequestsForGroup(group);
                if (!requests.isEmpty()) {
                    JLabel groupLabel = new JLabel("Grupo: " + group.getName());
                    groupLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    groupLabel.setForeground(new Color(52, 152, 219)); // Cambiar color del texto del grupo
                    requestsPanel.add(groupLabel);

                    for (ActivityRequest request : requests) {
                        JPanel requestPanel = new JPanel(new BorderLayout());
                        requestPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Añadir borde al panel
                        JLabel activityLabel = new JLabel("Actividad: " + request.getActivity().getName());
                        activityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        requestPanel.add(activityLabel, BorderLayout.WEST);

                        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Panel para botones
                        JButton acceptButton = new JButton("Aceptar");
                        acceptButton.setBackground(new Color(52, 152, 219)); // Cambiar color de fondo del botón Aceptar
                        acceptButton.setForeground(Color.WHITE); // Cambiar color del texto del botón Aceptar
                        acceptButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    request.acceptRequest();
                                    JOptionPane.showMessageDialog(null, "Solicitud aceptada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                    requestsPanel.remove(requestPanel); // Eliminar la solicitud del panel
                                    revalidate(); // Actualizar la interfaz
                                    repaint();
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Error al aceptar la solicitud", "Error", JOptionPane.ERROR_MESSAGE);
                                    ex.printStackTrace();
                                }
                            }
                        });
                        buttonPanel.add(acceptButton);

                        JButton rejectButton = new JButton("Rechazar");
                        rejectButton.setBackground(new Color(200, 50, 50)); // Cambiar color de fondo del botón Rechazar
                        rejectButton.setForeground(Color.WHITE); // Cambiar color del texto del botón Rechazar
                        rejectButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    request.rejectRequest();
                                    JOptionPane.showMessageDialog(null, "Solicitud rechazada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                    requestsPanel.remove(requestPanel); // Eliminar la solicitud del panel
                                    revalidate(); // Actualizar la interfaz
                                    repaint();
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Error al rechazar la solicitud", "Error", JOptionPane.ERROR_MESSAGE);
                                    ex.printStackTrace();
                                }
                            }
                        });
                        buttonPanel.add(rejectButton);

                        requestPanel.add(buttonPanel, BorderLayout.EAST);

                        requestsPanel.add(requestPanel);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar las solicitudes", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.revalidate();
    }
}
