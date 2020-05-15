package de.frederickayser.trafficsignrecognition;

import de.frederickayser.trafficsignrecognition.console.ConsoleLogger;
import lombok.Getter;

/**
 * by Frederic on 01.04.20(14:51)
 */
public class Main {

    @Getter
    private static boolean DEBUG = false;

    public static void main(String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("--debug") || args[0].equalsIgnoreCase("-d")) {
                DEBUG = true;
            }
        }
        try {
            ConsoleLogger.log();
            TrafficSignRecognition.getInstance().run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            e.printStackTrace(ConsoleLogger.out);
        }

    }

}
