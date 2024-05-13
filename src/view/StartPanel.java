package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import connection.ConnectionBD;

public class StartPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Image backgroundImage;

    public StartPanel() {
        // Cargar la imagen de fondo
        backgroundImage = new ImageIcon("images/Start_1.2.png").getImage();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        add(usernameLabel, gbc);
        gbc.gridy++;
        add(usernameField, gbc);
        gbc.gridy++;
        add(passwordLabel, gbc);
        gbc.gridy++;
        add(passwordField, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);
        gbc.gridy++;
        add(registerButton, gbc);

        // Agregar ActionListener para los botones
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    // Mostrar un mensaje de error
                    JOptionPane.showMessageDialog(null, "Por favor ingrese su nombre de usuario y contraseña", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    try {
                        int idUser = checkLogin(username, password);
                        if (idUser != -1) {
                            JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso", "Inicio de sesión", JOptionPane.INFORMATION_MESSAGE);

                            // Cambiar al panel de bienvenida
                            cambiarPanel(new WelcomePanel(idUser));

                        } else {
                            // Mostrar un mensaje de error
                            JOptionPane.showMessageDialog(null, "Nombre de usuario o contraseña incorrectos", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Mostrar un mensaje de error
                        JOptionPane.showMessageDialog(null, "Error al iniciar sesión", "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                    }
                }


            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para registrarse
                // Aquí debería ir el código para cambiar al panel de registro
                cambiarPanel(new RegisterPanel());

            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibujar la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void cambiarPanel(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(panel);
        frame.validate();
    }

    public static int checkLogin(String username, String password) throws SQLException {
        String query = "SELECT idUser FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ConnectionBD.obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idUser"); // Si hay una fila, devolvemos el userId
                } else {
                    return -1; // Si no hay ninguna fila, las credenciales no son válidas
                }
            }
        }
    }
}