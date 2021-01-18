package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import de.frederickayser.trafficsignrecognition.trafficsign.Probability;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import de.frederickayser.trafficsignrecognition.util.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * by Frederic on 18.01.21(21:04)
 */
public class SpeedtestCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedtestCommand.class);

    @Override
    public void execute(String[] strings) {
        File file = new File(ConfigurationHandler.getInstance().getTestPath() + "/images/100kmh/137-final.jpg");

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            long startTime = System.currentTimeMillis();
            double[] output = TrafficSignRecognition.getInstance().getNeuralNetwork().output(bufferedImage).toDoubleVector();
            long endTime = System.currentTimeMillis();
            long difference = endTime - startTime;

            MessageBuilder.send(LOGGER, "Prediction took " + difference + " seconds.");

            Probability[] probabilities = new Probability[output.length];
            for (int j = 0; j < output.length; j++) {
                probabilities[j] = new Probability(j, output[j]);
            }

            Arrays.sort(probabilities, Collections.reverseOrder());

            Type type = Type.getTypeByID(probabilities[0].getSignID());
            String percentage = String.valueOf(Util.round(probabilities[0].getProbability(), 4)*100);
            MessageBuilder.send(LOGGER, "Predicted " + type + "; Probability: " + percentage);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
