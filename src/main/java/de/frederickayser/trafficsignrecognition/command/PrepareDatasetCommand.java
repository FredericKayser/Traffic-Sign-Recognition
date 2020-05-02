package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.image.DataPreparer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 05.04.20(22:05)
 */
public class PrepareDatasetCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareDatasetCommand.class);

    @Override
    public void execute(String[] strings) {
        if(strings.length == 2) {
            if(strings[1].equalsIgnoreCase("training")) {
                MessageBuilder.send(LOGGER, "Starting to prepare training dataset.");
                DataPreparer.getInstance().prepareTrainingDataset();
                MessageBuilder.send(LOGGER, "Finished preparing training dataset.");
            } else if(strings[1].equalsIgnoreCase("test")) {
                MessageBuilder.send(LOGGER, "Starting to prepare test dataset.");
                DataPreparer.getInstance().prepareTestDataset();
                MessageBuilder.send(LOGGER, "Finished preparing test dataset.");
            } else
                MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax! preparedataset <training|test>");
        } else
            MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax! preparedataset <training|test>");
    }
}
