# Intelligent Traffic Management System

A Java 21 desktop simulation that dynamically manages a four-way intersection using traffic density, waiting time and emergency priority. The Swing dashboard includes an intersection visualisation, live queue cards, manual override controls and an automatic controller.

## Features

- Real-time four-road traffic dashboard
- Dynamic green-time allocation (10–60 seconds)
- Traffic density levels: LOW, MEDIUM, HIGH
- Waiting-time fairness to reduce starvation
- Automatic signal cycling
- Emergency vehicle priority and automatic return to normal control
- Simulated live traffic updates
- Auto/manual controller controls and manual road override
- Visual intersection with simulated queues (aggregate counts, not individual vehicle physics)
- Clean Java/Swing architecture

## Run

```bash
javac *.java
java Main
```

## Architecture

- `Main.java` — application entry point
- `TrafficManagementUI.java` — Swing dashboard and simulation loop
- `TrafficData.java` — traffic and waiting-time model
- `DecisionEngine.java` — priority scoring and green-time calculation
- `EmergencyManager.java` — emergency override state
- `TrafficLight.java` — reusable traffic-light Swing component

The project is intentionally Java-only and uses standard Java/Swing APIs.
