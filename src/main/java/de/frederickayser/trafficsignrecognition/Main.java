package de.frederickayser.trafficsignrecognition;

import de.frederickayser.trafficsignrecognition.console.ConsoleLogger;

/**
 * by Frederic on 01.04.20(14:51)
 */
public class Main {

    public static void main(String[] args) {
        try {
            ConsoleLogger.log();
            TrafficSignRecognition.getInstance().run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            e.printStackTrace(ConsoleLogger.out);
        }

    }

}
