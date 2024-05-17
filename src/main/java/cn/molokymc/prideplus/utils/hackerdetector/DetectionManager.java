package cn.molokymc.prideplus.utils.hackerdetector;

import cn.molokymc.prideplus.utils.hackerdetector.checks.FlightA;
import cn.molokymc.prideplus.utils.hackerdetector.checks.FlightB;
import cn.molokymc.prideplus.utils.hackerdetector.checks.ReachA;

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
