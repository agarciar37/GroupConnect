package view;

import javax.swing.*;

import err.EmailExistsException;
import err.UsernameExistsException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import models.User;

public class RegisterPanel extends JPanel{
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton cancelButton;

    public RegisterPanel(){
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240)); // Establecer color de fondo
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etiquetas y campos de texto
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        addFormField(nameLabel, nameField, gbc);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameField = new JTextField(20);
        addFormField(surnameLabel, surnameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        addFormField(emailLabel, emailField, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        addFormField(usernameLabel, usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        addFormField(passwordLabel, passwordField, gbc);

        // Botón de registro
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(50, 150, 200)); // Color de fondo
        registerButton.setForeground(Color.WHITE); // Color de texto
        registerButton.setFocusPainted(false); // Eliminar efecto de enfoque
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(registerButton, gbc);

        // Botón de cancelar
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 50, 50)); // Color de fondo
        cancelButton.setForeground(Color.WHITE); // Color de texto
        cancelButton.setFocusPainted(false); // Eliminar efecto de enfoque
        gbc.gridy++;
        add(cancelButton, gbc);

        // Acción del botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener los datos ingresados por el usuario
                String nombre = nameField.getText();
                String apellidos = surnameField.getText();
                String email = emailField.getText();
                String nombreUsuario = usernameField.getText();
                String contrasena = new String(passwordField.getPassword());
        
                try {
                    // Crear un nuevo usuario con los datos ingresados
                    User newUser = new User(nombre, apellidos, email, nombreUsuario, contrasena);
        
                    // Intentar crear el usuario
                    User.createUser(newUser);
        
                    // Llamar al método para crear el usuario en la base de datos
                    JOptionPane.showMessageDialog(null, "Usuario registrado correctamente", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        
                    // Limpiar los campos de texto después del registro
                    clearFields();
        
                    // Cambiar al panel de inicio de sesión
                    cambiarPanel(new StartPanel());
                } catch (UsernameExistsException ex) {
                    // Manejar el caso donde el nombre de usuario ya existe
                    JOptionPane.showMessageDialog(null, "El nombre de usuario ya está en uso", "Error de registro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (EmailExistsException ex) {
                    // Manejar el caso donde el correo electrónico ya existe
                    JOptionPane.showMessageDialog(null, "El correo electrónico ya está en uso", "Error de registro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    // Mostrar un mensaje de error si ocurre un error al registrar al usuario
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Acción del botón de cancelar
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cambiar al panel de inicio de sesión
                cambiarPanel(new StartPanel());
            }
        });
    }

    private void addFormField(JLabel label, JTextField textField, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(textField, gbc);
    }

    private void clearFields() {
        nameField.setText("");
        surnameField.setText("");
        emailField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.validate();
    }
}
