package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Group;
import models.User;

public class CreateGroupPanel extends JPanel{
    private JTextField groupNameField;
    private JTextField groupDescriptionField;
    private JButton createGroupButton;
    private JButton cancelButton;

    public CreateGroupPanel(User user){
        List<User> users = new ArrayList<>();
        users.add(user);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel groupNameLabel = new JLabel("Nombre del grupo:");
        groupNameField = new JTextField(15);
        JLabel groupDescriptionLabel = new JLabel("Descripción del grupo:");
        groupDescriptionField = new JTextField(15);
        createGroupButton = new JButton("Crear Grupo");
        createGroupButton.setBackground(new Color(52, 152, 219)); // Cambiar color de fondo del botón
        createGroupButton.setForeground(Color.WHITE); // Cambiar color del texto del botón

        // Configurar fuente y tamaño de texto para los componentes
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        groupNameLabel.setFont(labelFont);
        groupDescriptionLabel.setFont(labelFont);
        createGroupButton.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(groupNameLabel, gbc);
        gbc.gridy++;
        add(groupNameField, gbc);
        gbc.gridy++;
        add(groupDescriptionLabel, gbc);
        gbc.gridy++;
        add(groupDescriptionField, gbc);
        gbc.gridy++;
        add(createGroupButton, gbc);

        // Botón de cancelar
        cancelButton = new JButton("Volver");
        cancelButton.setBackground(new Color(200, 50, 50)); // Cambiar color de fondo del botón
        cancelButton.setForeground(Color.WHITE); // Cambiar color del texto del botón
        gbc.gridx++;
        add(cancelButton, gbc);

        // Agregar ActionListener para el botón "Create Group"
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener los datos ingresados por el usuario
                String nombreGrupo = groupNameField.getText();
                String descripcionGrupo = groupDescriptionField.getText();
        
                try {
                    // Crear un nuevo grupo con los datos ingresados
                    Group newGroup = new Group(nombreGrupo, descripcionGrupo, users);
                    // Insertar el nuevo grupo en la base de datos
                    Group.createGroup(newGroup, users);
                    // Actualizar el objeto Group para obtener el ID real de la base de datos
                    newGroup = Group.getGroupByName(nombreGrupo);
                    // Obtener el ID del grupo recién creado
                    int groupID = newGroup.getIdGroup();
                    // Mostrar un mensaje de éxito con el ID del grupo
                    JOptionPane.showMessageDialog(null, "Group created successfully. Group ID: " + groupID, "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Volver al WelcomePanel
                    cambiarPanel(new WelcomePanel(user.getIdUser()));
                } catch (SQLException ex) {
                    // Mostrar un mensaje de error si ocurre un error al crear el grupo
                    JOptionPane.showMessageDialog(null, "Error creating group", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Agregar ActionListener para el botón "Cancel"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volver al WelcomePanel
                try {
                    cambiarPanel(new WelcomePanel(user.getIdUser()));
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.revalidate();
    }
}
