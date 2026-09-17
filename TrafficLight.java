import java.awt.*;
import javax.swing.*;

public class TrafficLight extends JPanel {

    private String signal = "RED";

    public TrafficLight() {
        setPreferredSize(new Dimension(90, 190));
        setOpaque(false);
    }

    public void setSignal(String signal) {
        this.signal = signal;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Traffic light body
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(20, 5, 50, 180, 20, 20);

        // Red light
        drawLight(g2, Color.RED, 45, 40, signal.equals("RED"));

        // Yellow light
        drawLight(g2, Color.YELLOW, 45, 95, signal.equals("YELLOW"));

        // Green light
        drawLight(g2, Color.GREEN, 45, 150, signal.equals("GREEN"));
    }

    private void drawLight(
            Graphics2D g2,
            Color color,
            int x,
            int y,
            boolean active) {

        if (active) {
            g2.setColor(color);
        } else {
            g2.setColor(Color.DARK_GRAY);
        }

        g2.fillOval(x - 18, y - 18, 36, 36);
    }
}