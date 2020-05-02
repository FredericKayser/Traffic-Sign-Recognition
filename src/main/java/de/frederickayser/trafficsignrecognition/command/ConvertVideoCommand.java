package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.image.DataPreparer;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 03.04.20(22:44)
 */
public class ConvertVideoCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertVideoCommand.class);

    @Override
    public void execute(String[] strings) {
        if (strings.length == 1 || strings.length == 0) {
            MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                    "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
        } else if (strings.length == 2) {
            if (strings[1].equalsIgnoreCase("training")) {
                DataPreparer.getInstance().convertTrainingVideos();
            } else if (strings[1].equalsIgnoreCase("test")) {
                DataPreparer.getInstance().convertTestVideos();
            } else
                MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                        "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
        } else if (strings.length == 3) {
            if (strings[1].equalsIgnoreCase("training")) {
                String name = strings[2];
                DataPreparer.getInstance().convertTrainingVideo(name);
            } else if (strings[1].equalsIgnoreCase("test")) {
                String name = strings[2];
                DataPreparer.getInstance().convertTestVideo(name);
            } else
                MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                        "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
        } else if (strings.length == 4) {
            if (strings[1].equalsIgnoreCase("training")) {
                String name = strings[2];
                int startAtFrame = Integer.valueOf(strings[3]);
                DataPreparer.getInstance().convertTrainingVideo(name, startAtFrame);
            } else if (strings[1].equalsIgnoreCase("test")) {
                String name = strings[2];
                int startAtFrame = Integer.valueOf(strings[3]);
                DataPreparer.getInstance().convertTestVideo(name, startAtFrame);
            } else
                MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                        "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
        } else if(strings.length == 7) {
            String name = strings[2];
            int startFrame = Integer.valueOf(strings[3]);
            int endFrame = Integer.valueOf(strings[5]);
            Type type = Type.getTypeByFolder(strings[6]);
            if(strings[4].equalsIgnoreCase("autovalue")) {
                if (strings[1].equalsIgnoreCase("training")) {
                    DataPreparer.getInstance().convertTrainingVideoAutomatic(name, startFrame, endFrame, type);
                } else if (strings[1].equalsIgnoreCase("test")) {
                    DataPreparer.getInstance().convertTestVideoAutomatic(name, startFrame, endFrame, type);
                } else
                    MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                            "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
            } else
                MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                        "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");
        } else
            MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. convertvideo <training|test> " +
                    "[filename] [startframe] [autovalue <endFrame> <type(name of folder)>] ");

    }
}
