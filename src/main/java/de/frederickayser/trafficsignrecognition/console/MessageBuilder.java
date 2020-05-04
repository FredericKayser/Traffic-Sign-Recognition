package de.frederickayser.trafficsignrecognition.console;

import de.frederickayser.trafficsignrecognition.Main;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBuilder {

    public static void send(Logger logger, MessageType messageType, String message) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = simpleDateFormat.format(date);

        if(messageType.equals(MessageType.INFO))
            logger.info(message);
        else if(messageType.equals(MessageType.WARNING))
            logger.warn(message);
        else if(messageType.equals(MessageType.ERROR))
            logger.error(message);
        else if(messageType.equals(MessageType.DEBUG))
            logger.debug(message);

        if(!messageType.equals(MessageType.DEBUG) || Main.isDEBUG())
            ConsoleLogger.out.println("[" + dateString + " | " + messageType.toString() + "] " + message);
    }

    public static void send(Logger logger, String message) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = simpleDateFormat.format(date);

        logger.info(message);
        ConsoleLogger.out.println("[" + dateString + " | " + MessageType.INFO + "] " + message);
    }

    public static void send(MessageType messageType, String message) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = simpleDateFormat.format(date);

        System.out.println("[" + dateString + " | " + messageType + "] " + message);
        if(!messageType.equals(MessageType.DEBUG) || Main.isDEBUG())
            ConsoleLogger.out.println("[" + dateString + " | " + messageType.toString() + "] " + message);
    }

    public static void send(String message) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = simpleDateFormat.format(date);

        System.out.println("[" + dateString + " | " + MessageType.INFO + "] " + message);
        ConsoleLogger.out.println("[" + dateString + " | " + MessageType.INFO + "] " + message);
    }

    public enum MessageType {
        INFO,
        WARNING,
        DEBUG,
        ERROR;
    }

}
