package de.frederickayser.trafficsignrecognition.console;

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
        ConsoleLogger.out.println("[" + dateString + " | " + messageType.toString() + "] " + message);
    }

    public static void send(Logger logger, String message) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = simpleDateFormat.format(date);

        logger.info(message);
        ConsoleLogger.out.println("[" + dateString + " | " + MessageType.INFO + "] " + message);
    }

    public enum MessageType {
        INFO,
        WARNING,
        ERROR;
    }

}
