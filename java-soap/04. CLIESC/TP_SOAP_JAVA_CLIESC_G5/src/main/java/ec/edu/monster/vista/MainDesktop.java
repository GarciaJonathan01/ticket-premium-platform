package ec.edu.monster.vista;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;

/**
 * Clase principal para lanzar la aplicación de escritorio.
 */
public class MainDesktop {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
