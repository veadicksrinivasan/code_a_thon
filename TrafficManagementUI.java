import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TrafficManagementUI extends JFrame {
    private final TrafficData data = new TrafficData();
    private final DecisionEngine engine = new DecisionEngine();
    private final EmergencyManager emergency = new EmergencyManager();
    private final JLabel[] vehicleLabels = new JLabel[4];
    private final JLabel[] signalLabels = new JLabel[4];
    private final JLabel priorityLabel = new JLabel("Priority: -");
    private final JLabel levelLabel = new JLabel("Traffic: -");
    private final JLabel timerLabel = new JLabel("Green Time: -");
    private final JLabel emergencyLabel = new JLabel("Emergency: NONE");
    private final JLabel statusLabel = new JLabel("System ready");
    private final Random random = new Random();
    private int activeRoad = -1;
    private int remaining = 0;
    private Timer timer;

    public TrafficManagementUI() {
        setTitle("Intelligent Traffic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        buildUI();
        loadDemoTraffic();
        startController();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(new Color(18, 18, 22));

        JLabel title = new JLabel("INTELLIGENT TRAFFIC MANAGEMENT", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        root.add(title, BorderLayout.NORTH);

        JPanel roads = new JPanel(new GridLayout(2, 2, 12, 12));
        roads.setOpaque(false);
        for (int i = 0; i < 4; i++) roads.add(createRoadCard(i));
        root.add(roads, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 3, 8, 8));
        bottom.setOpaque(false);
        addInfo(bottom, priorityLabel);
        addInfo(bottom, levelLabel);
        addInfo(bottom, timerLabel);
        addInfo(bottom, emergencyLabel);

        JButton simulate = new JButton("Simulate New Traffic");
        simulate.addActionListener(e -> simulateTraffic());
        bottom.add(simulate);

        JButton emergencyButton = new JButton("Emergency Mode");
        emergencyButton.addActionListener(e -> chooseEmergency());
        bottom.add(emergencyButton);

        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel createRoadCard(int road) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setBackground(new Color(35, 35, 42));

        JLabel name = new JLabel(TrafficData.ROADS[road], SwingConstants.CENTER);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 18));
        card.add(name, BorderLayout.NORTH);

        signalLabels[road] = new JLabel("RED", SwingConstants.CENTER);
        signalLabels[road].setForeground(Color.RED);
        signalLabels[road].setFont(new Font("SansSerif", Font.BOLD, 32));
        card.add(signalLabels[road], BorderLayout.CENTER);

        vehicleLabels[road] = new JLabel("Vehicles: 0 | Wait: 0s", SwingConstants.CENTER);
        vehicleLabels[road].setForeground(Color.WHITE);
        card.add(vehicleLabels[road], BorderLayout.SOUTH);
        return card;
    }

    private void addInfo(JPanel panel, JLabel label) {
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
    }

    private void loadDemoTraffic() {
        int[] initial = {72, 38, 81, 25};
        for (int i = 0; i < 4; i++) data.setVehicles(i, initial[i]);
        refresh();
    }

    private void simulateTraffic() {
        for (int i = 0; i < 4; i++) {
            int change = random.nextInt(31) - 10;
            data.setVehicles(i, Math.max(0, data.getVehicles(i) + change));
        }
        statusLabel.setText("New traffic data received");
        refresh();
        if (activeRoad < 0) startController();
    }

    private void chooseEmergency() {
        String[] options = {"Cancel", "NORTH", "SOUTH", "EAST", "WEST"};
        int choice = JOptionPane.showOptionDialog(this, "Select emergency vehicle direction:",
                "Emergency Vehicle", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        if (choice > 0) {
            emergency.setEmergencyRoad(choice - 1);
            activeRoad = -1;
            remaining = 0;
            statusLabel.setText("EMERGENCY ROUTE CLEARED");
            refresh();
            startController();
        }
    }

    private void startController() {
        if (timer != null) timer.stop();
        activeRoad = engine.chooseRoad(data, emergency.getEmergencyRoad());
        remaining = engine.calculateGreenTime(data, activeRoad);
        data.clearWaitingTime(activeRoad);
        refresh();

        timer = new Timer(1000, e -> tick());
        timer.start();
    }

    private void tick() {
        remaining--;
        for (int i = 0; i < 4; i++) if (i != activeRoad) data.addWaitingTime(i, 1);
        if (activeRoad >= 0) data.reduceVehicles(activeRoad, Math.max(1, data.getVehicles(activeRoad) / Math.max(1, remaining + 1)));

        if (remaining <= 0) {
            if (emergency.isActive()) {
                emergency.clearEmergency();
                emergencyLabel.setText("Emergency: CLEARED");
            }
            activeRoad = -1;
            timer.stop();
            startController();
            return;
        }
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < 4; i++) {
            vehicleLabels[i].setText("Vehicles: " + data.getVehicles(i) + " | Wait: " + data.getWaitingSeconds(i) + "s");
            boolean green = i == activeRoad;
            signalLabels[i].setText(green ? "GREEN" : "RED");
            signalLabels[i].setForeground(green ? Color.GREEN : Color.RED);
        }
        if (activeRoad >= 0) {
            priorityLabel.setText("Priority: " + TrafficData.ROADS[activeRoad]);
            timerLabel.setText("Green Time: " + remaining + " sec");
        }
        levelLabel.setText("Traffic: " + engine.getTrafficLevel(data));
        emergencyLabel.setText(emergency.isActive()
                ? "Emergency: " + TrafficData.ROADS[emergency.getEmergencyRoad()]
                : "Emergency: NONE");
    }
}
