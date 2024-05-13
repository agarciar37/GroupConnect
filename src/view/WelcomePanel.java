package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import models.Activity;
import models.Group;
import models.User;

public class WelcomePanel extends JPanel {
    private User user;
    private JPanel groupButtonPanel;

    public WelcomePanel(int userId) throws SQLException {
        user = User.getUserById(userId);

        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("¡Bienvenido, " + user.getUsername() + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        groupButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        add(groupButtonPanel, BorderLayout.CENTER);

        updateGroupButtons();

        JPanel optionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        add(optionPanel, BorderLayout.SOUTH);

        JButton createGroupButton = createStyledButton("Crear un Grupo");
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPanel(new CreateGroupPanel(user));
            }
        });

        JButton joinGroupButton = createStyledButton("Unirse a un Grupo");
        joinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPanel(new JoinGroupPanel(user));
            }
        });

        JButton createActivityButton = createStyledButton("Crear una Actividad");
        createActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPanel(new CreateActivityPanel(user));
            }
        });

        JButton joinActivityButton = createStyledButton("Unirse a una Actividad");
        joinActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implementar la lógica para unirse a una actividad aquí
                cambiarPanel(new JoinActivityPanel(user));
            }
        });

        JButton acceptActivityRequestButton = createStyledButton("Aceptar Solicitudes de Actividad");
        acceptActivityRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPanel(new ManageActivityRequestsPanel(user));
            }
        });

        JButton manageGroupsActivitiesButton = createStyledButton("Manejar Grupos y Actividades");
        manageGroupsActivitiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPanel(new ManageGroupsAndActivitiesPanel(user));
            }
        });
        
        optionPanel.add(createGroupButton);
        optionPanel.add(joinGroupButton);
        optionPanel.add(createActivityButton);
        optionPanel.add(joinActivityButton);
        optionPanel.add(acceptActivityRequestButton);
        optionPanel.add(manageGroupsActivitiesButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 30));
        button.setBackground(new Color(50, 150, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void updateGroupButtons() {
        groupButtonPanel.removeAll(); // Limpiar los botones existentes

        try {
            List<Group> groups = Group.getGroupsByUser(user);
            for (Group group : groups) {
                JButton groupButton = new JButton(group.getName());
                groupButton.setBackground(new Color(250, 128, 114));
                groupButton.setForeground(Color.BLACK);
                groupButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showGroupInfo(group);
                    }
                });
                groupButtonPanel.add(groupButton);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los grupos", "Error", JOptionPane.ERROR_MESSAGE);
        }

        revalidate(); // Actualizar el diseño
        repaint(); // Volver a pintar los componentes
    }

    private void showGroupInfo(Group group) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel titleLabel = new JLabel("Información del Grupo: " + group.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
    
        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
    
        StringBuilder message = new StringBuilder();
        message.append("Nombre del Grupo: ").append(group.getName()).append("\n");
        message.append("Descripción: ").append(group.getDescription()).append("\n");
    
        try {
            List<User> groupUsers = Group.getUsersInGroup(group);
            message.append("Usuarios en el Grupo: ").append("\n");
            for (User u : groupUsers) {
                message.append("- ").append(u.getUsername()).append("\n");
            }
    
            List<Activity> groupActivities = Activity.getActivitiesByGroup(group);
            message.append("Actividades del Grupo: ").append("\n");
            for (Activity activity : groupActivities) {
                message.append("- ").append(activity.getName()).append("\n");
                message.append("  Descripción: ").append(activity.getDescription()).append("\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar la información del grupo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        infoTextArea.setText(message.toString());
    
        JOptionPane.showMessageDialog(null, panel, "Información del Grupo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.validate();
    }
}
