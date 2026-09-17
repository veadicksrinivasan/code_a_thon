import java.util.Scanner;

class TrafficController {

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

        System.out.println("=================================");
        System.out.println(" INTELLIGENT TRAFFIC MANAGEMENT");
        System.out.println("=================================");

        System.out.print("Enter North vehicles: ");
        int north = sc.nextInt();

        System.out.print("Enter South vehicles: ");
        int south = sc.nextInt();

        System.out.print("Enter East vehicles: ");
        int east = sc.nextInt();

        System.out.print("Enter West vehicles: ");
        int west = sc.nextInt();

        // Find road with highest traffic
        int highest = Math.max(
                Math.max(north, south),
                Math.max(east, west)
        );

        String priorityRoad;

        if (highest == north) {
            priorityRoad = "NORTH";
        } else if (highest == south) {
            priorityRoad = "SOUTH";
        } else if (highest == east) {
            priorityRoad = "EAST";
        } else {
            priorityRoad = "WEST";
        }

        // Calculate traffic level
        String trafficLevel;

        if (highest <= 30) {
            trafficLevel = "LOW";
        } else if (highest <= 60) {
            trafficLevel = "MEDIUM";
        } else {
            trafficLevel = "HIGH";
        }

        // Calculate green time
        int greenTime = 20 + (highest / 2);

        if (greenTime > 60) {
            greenTime = 60;
        }

        System.out.println("\n========== TRAFFIC ANALYSIS ==========");

        System.out.println("North : " + north + " vehicles");
        System.out.println("South : " + south + " vehicles");
        System.out.println("East  : " + east + " vehicles");
        System.out.println("West  : " + west + " vehicles");

        System.out.println("\nTraffic Level : " + trafficLevel);
        System.out.println("Priority Road : " + priorityRoad);
        System.out.println("Green Time    : " + greenTime + " seconds");

        System.out.println("\n========== SIGNAL STATUS ==========");

        System.out.println("North : " +
                (priorityRoad.equals("NORTH") ? "GREEN" : "RED"));

        System.out.println("South : " +
                (priorityRoad.equals("SOUTH") ? "GREEN" : "RED"));

        System.out.println("East  : " +
                (priorityRoad.equals("EAST") ? "GREEN" : "RED"));

        System.out.println("West  : " +
                (priorityRoad.equals("WEST") ? "GREEN" : "RED"));

    }
    }
}