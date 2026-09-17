public class EmergencyManager {
    private int emergencyRoad = -1;

    public void setEmergencyRoad(int road) {
        emergencyRoad = road >= 0 && road < 4 ? road : -1;
    }

    public void clearEmergency() {
        emergencyRoad = -1;
    }

    public int getEmergencyRoad() {
        return emergencyRoad;
    }

    public boolean isActive() {
        return emergencyRoad >= 0;
    }
}
