package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 12.05.20(17:47)
 */
public class ReloadDatasetCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadDatasetCommand.class);

    @Override
    public void execute(String[] strings) {
        TrafficSignRecognition.getInstance().getNeuralNetwork().loadFilesInCache(200);
        MessageBuilder.send(LOGGER, "Dataset reloaded.");
    }
}
