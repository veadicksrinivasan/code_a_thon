import java.awt.*;
import javax.swing.*;

/** Compact reusable traffic-light indicator used on the dashboard road cards. */
public class TrafficLight extends JPanel {
    private String signal = "RED";

    public TrafficLight() {
        setPreferredSize(new Dimension(52, 112));
        setOpaque(false);
    }

    public void setSignal(String signal) {
        this.signal = signal;
        repaint();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (getWidth() - 42) / 2;
        g.setColor(new Color(2, 6, 23));
        g.fillRoundRect(x, 2, 42, 108, 16, 16);
        drawLight(g, Color.RED, x + 21, 25, "RED".equals(signal));
        drawLight(g, new Color(250, 204, 21), x + 21, 55, "YELLOW".equals(signal));
        drawLight(g, new Color(34, 197, 94), x + 21, 85, "GREEN".equals(signal));
        g.dispose();
    }

    private void drawLight(Graphics2D g, Color color, int x, int y, boolean active) {
        g.setColor(active ? color : new Color(51, 65, 85));
        g.fillOval(x - 10, y - 10, 20, 20);
        if (active) {
            g.setColor(new Color(255, 255, 255, 80));
            g.fillOval(x - 5, y - 6, 6, 6);
        }
    }
}
