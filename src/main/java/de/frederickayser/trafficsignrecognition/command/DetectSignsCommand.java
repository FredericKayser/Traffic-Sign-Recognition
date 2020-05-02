package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.signdetection.SignDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 02.05.20(14:32)
 */
public class DetectSignsCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignDetector.class);

    @Override
    public void execute(String[] strings) {
        if(strings.length == 3) {
            String fileName = strings[1];
            String outputName = strings[2];
            MessageBuilder.send(LOGGER, "Starting to detect signs on video " + fileName + ".");
            SignDetector signDetector = new SignDetector(fileName, outputName);
            signDetector.detect();
            MessageBuilder.send(LOGGER, "Finished sign detection on video " + fileName + ". Outputvideo: " + outputName);
        }
    }
}
