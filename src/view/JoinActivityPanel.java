package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import models.*;

public class JoinActivityPanel extends JPanel {
    private User user;
    private JComboBox<String> groupComboBox;
    private JTextField placeField;
    private JButton searchButton;
    private JPanel activityButtonPanel;

    public JoinActivityPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Unirse a una Actividad");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel groupLabel = new JLabel("Selecciona un grupo:");
        groupComboBox = new JComboBox<>();
        try {
            List<Group> userGroups = Group.getGroupsFromUser(user);
            for (Group group : userGroups) {
                groupComboBox.addItem(group.getName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener los grupos del usuario", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        formPanel.add(groupLabel);
        formPanel.add(groupComboBox);

        JLabel placeLabel = new JLabel("Lugar de la actividad:");
        placeField = new JTextField();
        formPanel.add(placeLabel);
        formPanel.add(placeField);

        searchButton = new JButton("Buscar Actividades");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchActivities();
            }
        });
        formPanel.add(searchButton);

        add(formPanel, BorderLayout.NORTH);

        activityButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(activityButtonPanel);
        add(scrollPane, BorderLayout.CENTER);

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

    private void searchActivities() {
        activityButtonPanel.removeAll();
        activityButtonPanel.revalidate();
        activityButtonPanel.repaint();

        String selectedGroupName = (String) groupComboBox.getSelectedItem();
        String selectedPlace = placeField.getText();

        try {
            Group selectedGroup = Group.getGroupByName(selectedGroupName);
            List<Activity> activities = Activity.getActivitiesNotInGroupAtPlace(selectedGroup, selectedPlace);
            if (activities.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontraron actividades disponibles en el lugar especificado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Se encontraron actividades disponibles en el lugar especificado.", "Información", JOptionPane.INFORMATION_MESSAGE);
                for (Activity activity : activities) {
                    JButton activityButton = new JButton(activity.getName());
                    activityButton.setPreferredSize(new Dimension(150, 30));
                    activityButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int choice = JOptionPane.showConfirmDialog(null, "¿Deseas enviar una solicitud para unirte a esta actividad?", "Confirmar", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                sendJoinRequest(selectedGroup, activity);
                            }
                        }
                    });
                    activityButtonPanel.add(activityButton);
                }
                activityButtonPanel.setBackground(Color.WHITE);
                revalidate();
                repaint();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar actividades", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sendJoinRequest(Group group, Activity activity) {
        try {
            Activity.sendJoinRequest(user, group, activity);
            JOptionPane.showMessageDialog(null, "Solicitud enviada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cambiarPanel(new WelcomePanel(user.getIdUser()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al enviar la solicitud", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.revalidate();
    }
}
