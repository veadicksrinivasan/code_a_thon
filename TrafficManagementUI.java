import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/** Main dashboard, controller and visual simulation for one four-way intersection. */
public class TrafficManagementUI extends JFrame {
    private static final Color BACKGROUND = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color MUTED = new Color(148, 163, 184);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED = new Color(248, 113, 113);

    private final TrafficData data = new TrafficData();
    private final DecisionEngine engine = new DecisionEngine();
    private final EmergencyManager emergency = new EmergencyManager();
    private final Random random = new Random();
    private final RoadCard[] roadCards = new RoadCard[4];
    private final IntersectionPanel intersection = new IntersectionPanel();
    private final JLabel priorityValue = valueLabel("Waiting for decision");
    private final JLabel trafficValue = valueLabel("LOW");
    private final JLabel countdownValue = valueLabel("--");
    private final JLabel emergencyValue = valueLabel("None");
    private final JLabel statusLabel = new JLabel("System ready — adaptive mode is active");
    private final JToggleButton autoMode = new JToggleButton("AUTO MODE", true);
    private final JComboBox<String> manualRoad = new JComboBox<>(TrafficData.ROADS);
    private Timer timer;
    private int activeRoad = -1;
    private int remainingSeconds;

    public TrafficManagementUI() {
        setTitle("FlowSense | Intelligent Traffic Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        buildUI();
        loadDemoTraffic();
        startController();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));
        root.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);
        center.add(createRoadGrid(), BorderLayout.WEST);
        center.add(createIntersectionSection(), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        root.add(createControlPanel(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("FLOWSENSE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        JLabel subtitle = new JLabel("Adaptive intersection control  •  Live simulation");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(subtitle);

        JLabel live = new JLabel("●  SYSTEM ONLINE");
        live.setForeground(GREEN);
        live.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.add(text, BorderLayout.WEST);
        header.add(live, BorderLayout.EAST);
        return header;
    }

    private JPanel createRoadGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(470, 430));
        for (int road = 0; road < 4; road++) {
            roadCards[road] = new RoadCard(road);
            grid.add(roadCards[road]);
        }
        return grid;
    }

    private JPanel createIntersectionSection() {
        JPanel section = panel(new BorderLayout(10, 10));
        section.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel heading = new JLabel("INTERSECTION LIVE VIEW");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));
        section.add(heading, BorderLayout.NORTH);
        intersection.setPreferredSize(new Dimension(520, 430));
        section.add(intersection, BorderLayout.CENTER);
        return section;
    }

    private JPanel createControlPanel() {
        JPanel outer = panel(new BorderLayout(18, 8));
        outer.setBorder(new EmptyBorder(14, 16, 12, 16));
        JPanel metrics = new JPanel(new GridLayout(1, 4, 10, 0));
        metrics.setOpaque(false);
        metrics.add(metric("CURRENT PRIORITY", priorityValue));
        metrics.add(metric("NETWORK LOAD", trafficValue));
        metrics.add(metric("GREEN REMAINING", countdownValue));
        metrics.add(metric("EMERGENCY ROUTE", emergencyValue));
        outer.add(metrics, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 2));
        controls.setOpaque(false);
        styleToggle(autoMode);
        autoMode.addActionListener(e -> {
            autoMode.setText(autoMode.isSelected() ? "AUTO MODE" : "MANUAL MODE");
            setStatus(autoMode.isSelected() ? "Automatic cycling enabled" : "Manual decisions enabled");
            if (autoMode.isSelected() && activeRoad < 0) startController();
        });
        controls.add(autoMode);
        controls.add(button("Simulate traffic", e -> simulateTraffic()));
        controls.add(button("Next decision", e -> startController()));
        controls.add(new JLabel("  Manual green:"));
        controls.add(manualRoad);
        controls.add(button("Apply", e -> activateManualRoad()));
        controls.add(button("Emergency", e -> chooseEmergency()));
        outer.add(controls, BorderLayout.CENTER);

        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        outer.add(statusLabel, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel metric(String title, JLabel value) {
        JPanel metric = new JPanel();
        metric.setOpaque(false);
        metric.setLayout(new BoxLayout(metric, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(title);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        metric.add(label);
        metric.add(Box.createVerticalStrut(3));
        metric.add(value);
        return metric;
    }

    private JLabel valueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }

    private JButton button(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 65, 85));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.addActionListener(listener);
        return button;
    }

    private void styleToggle(JToggleButton toggle) {
        toggle.setFocusPainted(false);
        toggle.setForeground(Color.WHITE);
        toggle.setBackground(new Color(14, 116, 144));
        toggle.setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    private JPanel panel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL);
        return panel;
    }

    private void loadDemoTraffic() {
        int[] initial = {72, 38, 81, 25};
        for (int road = 0; road < 4; road++) data.setVehicles(road, initial[road]);
        refresh();
    }

    private void simulateTraffic() {
        for (int road = 0; road < 4; road++) data.setVehicles(road, data.getVehicles(road) + random.nextInt(31) - 10);
        setStatus("Traffic sensors refreshed with simulated arrival data");
        refresh();
    }

    private void chooseEmergency() {
        String[] choices = {"Cancel", "NORTH", "SOUTH", "EAST", "WEST", "Clear active emergency"};
        int choice = JOptionPane.showOptionDialog(this, "Choose the incoming emergency route.", "Emergency priority",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, choices[0]);
        if (choice >= 1 && choice <= 4) {
            emergency.setEmergencyRoad(choice - 1);
            setStatus("Emergency pre-emption: " + TrafficData.ROADS[choice - 1] + " route cleared");
            startController();
        } else if (choice == 5) {
            emergency.clearEmergency();
            setStatus("Emergency override cleared; normal control resumed");
            startController();
        }
    }

    private void activateManualRoad() {
        emergency.clearEmergency();
        activateRoad(manualRoad.getSelectedIndex(), "Manual green assigned to " + manualRoad.getSelectedItem());
    }

    private void startController() {
        int road = engine.chooseRoad(data, emergency.getEmergencyRoad());
        String reason = emergency.isActive() ? "Emergency route granted priority" : "Adaptive engine selected highest-priority road";
        activateRoad(road, reason);
    }

    private void activateRoad(int road, String message) {
        if (timer != null) timer.stop();
        activeRoad = road;
        remainingSeconds = engine.calculateGreenTime(data, road);
        data.clearWaitingTime(road);
        timer = new Timer(1000, e -> tick());
        timer.start();
        setStatus(message + " • " + remainingSeconds + " second green window");
        refresh();
    }

    private void tick() {
        remainingSeconds--;
        for (int road = 0; road < 4; road++) if (road != activeRoad) data.addWaitingTime(road, 1);
        if (activeRoad >= 0) {
            int discharge = Math.max(1, data.getVehicles(activeRoad) / Math.max(1, remainingSeconds + 1));
            data.reduceVehicles(activeRoad, discharge);
        }
        if (remainingSeconds <= 0) {
            if (emergency.isActive()) {
                emergency.clearEmergency();
                setStatus("Emergency route complete; returning to adaptive control");
            }
            activeRoad = -1;
            timer.stop();
            if (autoMode.isSelected()) startController();
            else setStatus("Phase complete — choose a road or press Next decision");
        }
        refresh();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void refresh() {
        for (int road = 0; road < 4; road++) roadCards[road].update(data.getVehicles(road), data.getWaitingSeconds(road), road == activeRoad);
        priorityValue.setText(activeRoad < 0 ? "Awaiting manual choice" : TrafficData.ROADS[activeRoad]);
        trafficValue.setText(engine.getTrafficLevel(data) + "  •  " + data.getTotalVehicles() + " vehicles");
        countdownValue.setText(activeRoad < 0 ? "--" : remainingSeconds + " sec");
        emergencyValue.setText(emergency.isActive() ? TrafficData.ROADS[emergency.getEmergencyRoad()] : "None");
        intersection.update(data, activeRoad);
    }

    private static class RoadCard extends JPanel {
        private final int road;
        private final TrafficLight light = new TrafficLight();
        private final JLabel count = new JLabel();
        private final JLabel wait = new JLabel();
        private final JLabel state = new JLabel("RED");

        RoadCard(int road) {
            super(new BorderLayout(8, 8));
            this.road = road;
            setBackground(PANEL);
            setBorder(new EmptyBorder(14, 14, 14, 14));
            JLabel name = new JLabel(TrafficData.ROADS[road]);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("SansSerif", Font.BOLD, 16));
            add(name, BorderLayout.NORTH);
            light.setPreferredSize(new Dimension(48, 104));
            add(light, BorderLayout.WEST);
            JPanel information = new JPanel();
            information.setOpaque(false);
            information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
            state.setFont(new Font("SansSerif", Font.BOLD, 15));
            count.setForeground(Color.WHITE);
            wait.setForeground(MUTED);
            information.add(state);
            information.add(Box.createVerticalStrut(8));
            information.add(count);
            information.add(Box.createVerticalStrut(5));
            information.add(wait);
            add(information, BorderLayout.CENTER);
        }

        void update(int vehicles, int waiting, boolean isGreen) {
            light.setSignal(isGreen ? "GREEN" : "RED");
            state.setText(isGreen ? "GREEN • FLOWING" : "RED • WAITING");
            state.setForeground(isGreen ? GREEN : RED);
            count.setText(vehicles + " vehicles queued");
            wait.setText("Wait time: " + waiting + " sec");
            setBackground(isGreen ? new Color(20, 83, 45) : PANEL);
        }
    }

    private static class IntersectionPanel extends JPanel {
        private TrafficData data;
        private int activeRoad = -1;

        IntersectionPanel() { setBackground(new Color(17, 24, 39)); }

        void update(TrafficData data, int activeRoad) {
            this.data = data;
            this.activeRoad = activeRoad;
            repaint();
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2;
            g.setColor(new Color(55, 65, 81));
            g.fillRect(cx - 82, 0, 164, h);
            g.fillRect(0, cy - 82, w, 164);
            g.setColor(new Color(148, 163, 184));
            g.setStroke(new BasicStroke(2f));
            for (int i = 0; i < Math.max(w, h); i += 28) {
                g.drawLine(cx - 5, i, cx + 5, i);
                g.drawLine(i, cy - 5, i, cy + 5);
            }
            g.setColor(new Color(71, 85, 105));
            g.fillRect(cx - 82, cy - 82, 164, 164);
            if (data != null) for (int road = 0; road < 4; road++) drawQueue(g, road, data.getVehicles(road), road == activeRoad, cx, cy, w, h);
            g.dispose();
        }

        private void drawQueue(Graphics2D g, int road, int vehicles, boolean green, int cx, int cy, int w, int h) {
            Color color = green ? GREEN : new Color(96, 165, 250);
            int shown = Math.min(7, Math.max(1, (vehicles + 11) / 12));
            for (int i = 0; i < shown; i++) {
                int x = cx, y = cy;
                if (road == 0) { x = cx - 52; y = cy - 110 - i * 32; }
                if (road == 1) { x = cx + 27; y = cy + 88 + i * 32; }
                if (road == 2) { x = cx + 88 + i * 32; y = cy - 52; }
                if (road == 3) { x = cx - 110 - i * 32; y = cy + 27; }
                g.setColor(color);
                g.fillRoundRect(x, y, road < 2 ? 25 : 30, road < 2 ? 30 : 25, 7, 7);
                g.setColor(Color.WHITE);
                g.fillRoundRect(x + 4, y + 4, road < 2 ? 17 : 22, road < 2 ? 7 : 6, 3, 3);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            String name = TrafficData.ROADS[road].substring(0, 1);
            int tx = road == 0 ? cx - 4 : road == 1 ? cx - 4 : road == 2 ? w - 20 : 12;
            int ty = road == 0 ? 16 : road == 1 ? h - 10 : cy + 4;
            g.drawString(name, tx, ty);
        }
    }
}
