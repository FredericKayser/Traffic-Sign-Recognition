package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * by Frederic on 07.04.20(21:04)
 */
public class TrainCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainCommand.class);

    @Override
    public void execute(String[] strings) {
        if(strings.length == 3) {
            int epochs = Integer.valueOf(strings[1]);
            int evaluations = Integer.valueOf(strings[2]);
            MessageBuilder.send(LOGGER, "Started to train the neural network. This can take up to many hours.");
            for(int i = 0; i < evaluations; i++) {
                TrafficSignRecognition.getInstance().getNeuralNetwork().train(epochs);
                TrafficSignRecognition.getInstance().getNeuralNetwork().test();
            }
            MessageBuilder.send(LOGGER, "Finished training of neural network.");
        } else
            MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Wrong syntax. train <epochs to evaluation> <evalutions>");
    }
}
