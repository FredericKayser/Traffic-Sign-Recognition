package de.frederickayser.trafficsignrecognition.signdetection;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * by Frederic on 03.05.20(00:21)
 */
public class RaspberrySignDetector {

    private final AtomicBoolean stop = new AtomicBoolean();
    private final String outputPath;

    public RaspberrySignDetector(String outputPath) {
        this.outputPath = outputPath;
        stop.set(false);
    }

    public void cancel() {
        stop.set(true);
    }

    public void detect() {

    }
}
