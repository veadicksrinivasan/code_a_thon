import java.util.Scanner;

class EmergencyMode {
    public static void main(String[] args) {
        String[] roads = {"NORTH", "SOUTH", "EAST", "WEST"};

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("\n🚨 EMERGENCY VEHICLE");
            System.out.println("1. No Emergency");
            System.out.println("2. North");
            System.out.println("3. South");
            System.out.println("4. East");
            System.out.println("5. West");

            System.out.print("Select emergency road: ");
            int emergency = sc.nextInt();

            if (emergency >= 2 && emergency <= 5) {
                String emergencyRoad = roads[emergency - 2];

                System.out.println("\n🚨 EMERGENCY DETECTED!");
                System.out.println("Priority Road: " + emergencyRoad);
                System.out.println("\nSIGNAL STATUS:");

                for (String road : roads) {
                    String signal = road.equals(emergencyRoad) ? "🟢 GREEN" : "🔴 RED";
                    System.out.println(road + " → " + signal);
                }

                System.out.println("\nEmergency vehicle is passing...");
                pauseForFiveSeconds();
                System.out.println("\n🚨 Emergency cleared.");
                System.out.println("Returning to normal traffic control...\n");
            }
        }
    }

    private static void pauseForFiveSeconds() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
