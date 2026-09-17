
    import java.util.Scanner;

class AutomaticSignalSwitching {

    public static void main(String[] args) throws InterruptedException {

        try (Scanner sc = new Scanner(System.in)) {

        System.out.println("=================================");
        System.out.println(" INTELLIGENT TRAFFIC MANAGEMENT");
        System.out.println("=================================");

        System.out.print("North vehicles: ");
        int north = sc.nextInt();

        System.out.print("South vehicles: ");
        int south = sc.nextInt();

        System.out.print("East vehicles: ");
        int east = sc.nextInt();

        System.out.print("West vehicles: ");
        int west = sc.nextInt();

        int[] vehicles = {north, south, east, west};
        String[] roads = {"NORTH", "SOUTH", "EAST", "WEST"};

        // Run 4 signal cycles
        for (int cycle = 0; cycle < 4; cycle++) {

            // Find road with highest remaining traffic
            int highestIndex = 0;

            for (int i = 1; i < 4; i++) {
                if (vehicles[i] > vehicles[highestIndex]) {
                    highestIndex = i;
                }
            }

            String currentRoad = roads[highestIndex];

            // Calculate green time
            int greenTime = 5 + (vehicles[highestIndex] / 20);

            if (greenTime > 10) {
                greenTime = 10;
            }

            System.out.println("\n=================================");
            System.out.println("🚦 SIGNAL CHANGE");
            System.out.println("=================================");

            System.out.println(currentRoad + " → 🟢 GREEN");

            System.out.println("NORTH : " +
                    (currentRoad.equals("NORTH") ? "🟢 GREEN" : "🔴 RED"));

            System.out.println("SOUTH : " +
                    (currentRoad.equals("SOUTH") ? "🟢 GREEN" : "🔴 RED"));

            System.out.println("EAST  : " +
                    (currentRoad.equals("EAST") ? "🟢 GREEN" : "🔴 RED"));

            System.out.println("WEST  : " +
                    (currentRoad.equals("WEST") ? "🟢 GREEN" : "🔴 RED"));

            // Countdown
            for (int time = greenTime; time > 0; time--) {

                System.out.println(
                        "⏱️ " + currentRoad +
                        " GREEN - " + time + " seconds remaining"
                );

                pauseForOneSecond();
            }

            System.out.println(currentRoad + " → 🔴 RED");

            // Remove the processed road temporarily
            vehicles[highestIndex] = 0;
        }

        System.out.println("\n=================================");
        System.out.println(" TRAFFIC CYCLE COMPLETED");
        System.out.println("=================================");

    }
    }

    private static void pauseForOneSecond() throws InterruptedException {
        Thread.sleep(1000);
    }
}
    

