public class TrafficData {
    public static final String[] ROADS = {"NORTH", "SOUTH", "EAST", "WEST"};

    private final int[] vehicles = new int[4];
    private final int[] waitingSeconds = new int[4];

    public void setVehicles(int road, int count) {
        vehicles[road] = Math.max(0, count);
    }

    public int getVehicles(int road) {
        return vehicles[road];
    }

    public int getWaitingSeconds(int road) {
        return waitingSeconds[road];
    }

    public void addWaitingTime(int road, int seconds) {
        if (vehicles[road] > 0) {
            waitingSeconds[road] += Math.max(0, seconds);
        }
    }

    public void clearWaitingTime(int road) {
        waitingSeconds[road] = 0;
    }

    public void reduceVehicles(int road, int amount) {
        vehicles[road] = Math.max(0, vehicles[road] - Math.max(0, amount));
    }

    public int getTotalVehicles() {
        int total = 0;
        for (int value : vehicles) total += value;
        return total;
    }

    public int getTrafficLevel() {
        int max = 0;
        for (int value : vehicles) max = Math.max(max, value);
        if (max <= 30) return 1;
        if (max <= 60) return 2;
        return 3;
    }
}
