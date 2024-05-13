package view;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{
    // Jframe donde se mostrará la interfaz gráfica	
    // se empezará con el panel de inicio (StartPanel)
    public GUI(){
        this.setTitle("GroupConnect");
        this.setSize(700, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(new CardLayout());
        this.add(new StartPanel());
        this.setVisible(true);
    }
}
