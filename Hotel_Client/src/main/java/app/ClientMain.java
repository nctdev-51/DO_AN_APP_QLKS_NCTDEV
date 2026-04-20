package app;

import com.formdev.flatlaf.FlatLightLaf;
import gui.DangNhap_Gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ClientMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new DangNhap_Gui().setVisible(true));
    }
}
