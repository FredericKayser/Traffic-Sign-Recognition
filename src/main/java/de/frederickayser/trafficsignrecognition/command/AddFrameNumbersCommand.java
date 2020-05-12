package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.image.DataPreparer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 12.05.20(17:39)
 */
public class AddFrameNumbersCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFrameNumbersCommand.class);

    @Override
    public void execute(String[] strings) {
        if(strings.length == 3) {
            String fileName = strings[1];
            String outputName = strings[2];
            MessageBuilder.send(LOGGER, "Started adding framenumbers to video " + fileName);
            DataPreparer.getInstance().addFrameNumbersToVideo(fileName, outputName);
            MessageBuilder.send(LOGGER, "Finished adding frame numbers to video " + fileName);
        }
    }
}
