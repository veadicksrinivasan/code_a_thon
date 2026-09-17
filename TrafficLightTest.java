import javax.swing.*;

public class TrafficLightTest {

    public static void main(String[] args) {

        JFrame frame =
                new JFrame("Traffic Light Test");

        TrafficLight light =
                new TrafficLight();

        frame.add(light);

        frame.setSize(200, 300);
        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        // Start with RED
        light.setSignal("RED");
    }
}