package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import models.Group;
import models.User;

public class JoinGroupPanel extends JPanel {
    private JTextField groupIdField;
    private JButton joinGroupButton;
    private JButton cancelButton;

    public JoinGroupPanel(User user) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel groupIdLabel = new JLabel("ID del Grupo:");
        groupIdField = new JTextField(15);
        joinGroupButton = new JButton("Unirse al Grupo");
        joinGroupButton.setBackground(new Color(52, 152, 219)); // Cambiar color de fondo del botón
        joinGroupButton.setForeground(Color.WHITE); // Cambiar color del texto del botón

        add(groupIdLabel, gbc);
        gbc.gridy++;
        add(groupIdField, gbc);
        gbc.gridy++;
        add(joinGroupButton, gbc);

        cancelButton = new JButton("Volver");
        cancelButton.setBackground(new Color(200, 50, 50)); // Cambiar color de fondo del botón
        cancelButton.setForeground(Color.WHITE); // Cambiar color del texto del botón
        gbc.gridx++;
        add(cancelButton, gbc);

        // Agregar ActionListener para el botón "Unirse al Grupo"
        joinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el ID del grupo ingresado por el usuario
                int groupId = Integer.parseInt(groupIdField.getText());

                try {
                    // Verificar si el grupo existe
                    Group group = Group.getGroupById(groupId);

                    if (group != null) {
                        // Si el grupo existe, unir al usuario al grupo
                        Group.joinGroup(group, user);
                        JOptionPane.showMessageDialog(null, "Te has unido al grupo con ID '" + groupId + "'", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Cambiar al panel de bienvenida
                        cambiarPanel(new WelcomePanel(user.getIdUser()));
                    } else {
                        // Si el grupo no existe, mostrar un mensaje de error
                        JOptionPane.showMessageDialog(null, "El grupo con ID '" + groupId + "' no existe", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    // Mostrar un mensaje de error si ocurre un error al buscar el grupo
                    JOptionPane.showMessageDialog(null, "Error al unirse al grupo", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {
                    // Manejar la excepción si el usuario ingresa un ID no válido
                    JOptionPane.showMessageDialog(null, "Por favor, ingresa un ID válido para el grupo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Agregar ActionListener para el botón "Cancel"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cambiar al panel de bienvenida
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
        frame.validate();
    }
}
