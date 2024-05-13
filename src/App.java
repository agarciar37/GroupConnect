import javax.swing.SwingUtilities;

import view.GUI;

public class App {
    public static void main(String[] args) throws Exception {
        //lanzar el GUI en el hilo de eventos
        SwingUtilities.invokeLater(() -> new GUI());
    }
}
