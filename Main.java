import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficManagementUI ui = new TrafficManagementUI();
            ui.setVisible(true);
        });
    }
}
