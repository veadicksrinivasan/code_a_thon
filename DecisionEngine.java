public class DecisionEngine {
    public int chooseRoad(TrafficData data, int emergencyRoad) {
        if (emergencyRoad >= 0) return emergencyRoad;

        int bestRoad = 0;
        double bestScore = -1;
        for (int road = 0; road < 4; road++) {
            double score = data.getVehicles(road) * 1.0
                    + data.getWaitingSeconds(road) * 0.8;
            if (score > bestScore) {
                bestScore = score;
                bestRoad = road;
            }
        }
        return bestRoad;
    }

    public int calculateGreenTime(TrafficData data, int road) {
        int vehicles = data.getVehicles(road);
        int waiting = data.getWaitingSeconds(road);
        int time = 10 + (vehicles / 3) + (waiting / 5);
        return Math.max(10, Math.min(60, time));
    }

    public String getTrafficLevel(TrafficData data) {
        int level = data.getTrafficLevel();
        return level == 1 ? "LOW" : level == 2 ? "MEDIUM" : "HIGH";
    }
}
