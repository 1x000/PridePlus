package dev.stable.utils.hackerdetector;

import dev.stable.utils.hackerdetector.checks.FlightA;
import dev.stable.utils.hackerdetector.checks.FlightB;
import dev.stable.utils.hackerdetector.checks.ReachA;

import java.util.ArrayList;
import java.util.Arrays;

public class DetectionManager {

    private ArrayList<Detection> detections = new ArrayList<>();

    public DetectionManager() {
        addDetections(

                // Combat
                new ReachA(),

                // Movement
                new FlightA(),
                new FlightB()

                // Player

                // Misc

                // Exploit

        );
    }

    public void addDetections(Detection... detections) {
        this.detections.addAll(Arrays.asList(detections));
    }

    public ArrayList<Detection> getDetections() {
        return detections;
    }
}
