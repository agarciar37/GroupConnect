package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import models.*;

public class ManageGroupsAndActivitiesPanel extends JPanel {
    private User user;
    private JPanel groupsPanel;
    private JPanel activitiesPanel;

    public ManageGroupsAndActivitiesPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestionar Grupos y Actividades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        groupsPanel = new JPanel();
        groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.Y_AXIS));
        JScrollPane groupsScrollPane = new JScrollPane(groupsPanel);
        contentPanel.add(groupsScrollPane);

        activitiesPanel = new JPanel();
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));
        JScrollPane activitiesScrollPane = new JScrollPane(activitiesPanel);
        contentPanel.add(activitiesScrollPane);

        loadGroupsAndActivities();

        add(contentPanel, BorderLayout.CENTER);

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

    private void loadGroupsAndActivities() {
        try {
            // Cargar grupos del usuario
            List<Group> groups = Group.getGroupsFromUser(user);
            for (Group group : groups) {
                JPanel groupPanel = new JPanel(new BorderLayout());
                JLabel groupNameLabel = new JLabel("Grupo: " + group.getName());
                groupPanel.add(groupNameLabel, BorderLayout.WEST);
                JButton leaveGroupButton = new JButton("Abandonar");
                leaveGroupButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            user.leaveGroup(group);
                            JOptionPane.showMessageDialog(null, "Has abandonado el grupo correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            groupsPanel.remove(groupPanel);
                            revalidate();
                            repaint();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al abandonar el grupo", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                });
                groupPanel.add(leaveGroupButton, BorderLayout.EAST);
                groupsPanel.add(groupPanel);
            }

            // Cargar actividades del usuario
            List<Activity> activities = Activity.getActivitiesFromUser(user);
            for (Activity activity : activities) {
                JPanel activityPanel = new JPanel(new BorderLayout());
                JLabel activityNameLabel = new JLabel("Actividad: " + activity.getName());
                activityPanel.add(activityNameLabel, BorderLayout.WEST);
                JButton leaveActivityButton = new JButton("Abandonar");
                leaveActivityButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            user.leaveActivity(activity);
                            JOptionPane.showMessageDialog(null, "Has abandonado la actividad correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            activitiesPanel.remove(activityPanel);
                            revalidate();
                            repaint();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al abandonar la actividad", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                });
                activityPanel.add(leaveActivityButton, BorderLayout.EAST);
                activitiesPanel.add(activityPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los grupos y actividades", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.revalidate();
    }
}
