package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import models.User;
import models.Group;
import models.Activity;

public class CreateActivityPanel extends JPanel {
    private JTextField activityNameField;
    private JTextField activityDescriptionField;
    private JTextField placeField; // Nuevo campo para el lugar de la actividad
    private JComboBox<String> groupComboBox;
    private JButton createActivityButton;

    public CreateActivityPanel(User user) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Añadir una fila para el lugar de la actividad

        JLabel titleLabel = new JLabel("Crear Nueva Actividad");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JLabel activityNameLabel = new JLabel("Nombre de la actividad:");
        activityNameField = new JTextField();
        formPanel.add(activityNameLabel);
        formPanel.add(activityNameField);

        JLabel activityDescriptionLabel = new JLabel("Descripción de la actividad:");
        activityDescriptionField = new JTextField();
        formPanel.add(activityDescriptionLabel);
        formPanel.add(activityDescriptionField);

        JLabel placeLabel = new JLabel("Lugar de la actividad:");
        placeField = new JTextField(); // Nuevo campo para el lugar de la actividad
        formPanel.add(placeLabel);
        formPanel.add(placeField);

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

        createActivityButton = new JButton("Crear Actividad");
        createActivityButton.setBackground(new Color(52, 152, 219));
        createActivityButton.setForeground(Color.WHITE);
        createActivityButton.setPreferredSize(new Dimension(150, 30));
        createActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewActivity(user);
            }
        });
        formPanel.add(createActivityButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Volver");
        backButton.setBackground(new Color(200, 50, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(150, 30));
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
        buttonPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createNewActivity(User user) {
        String activityName = activityNameField.getText();
        String activityDescription = activityDescriptionField.getText();
        String place = placeField.getText(); // Obtener el valor del campo de lugar
        String selectedGroupName = (String) groupComboBox.getSelectedItem();
    
        try {
            Group selectedGroup = Group.getGroupByName(selectedGroupName);
            if (selectedGroup != null) {
                // Obtener el ID del grupo seleccionado
    
                // Crear la actividad con el ID del grupo y el lugar
                Activity activity = new Activity(activityName, activityDescription, place);
                Activity.createActivity(activity, selectedGroup, place);
    
                JOptionPane.showMessageDialog(null, "Actividad creada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cambiarPanel(new WelcomePanel(user.getIdUser()));
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un grupo válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al crear la actividad", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.revalidate();
    }
}
