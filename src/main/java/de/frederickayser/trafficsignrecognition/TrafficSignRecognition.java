package de.frederickayser.trafficsignrecognition;

import de.frederickayser.trafficsignrecognition.command.*;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import de.frederickayser.trafficsignrecognition.image.ImageTransformer;
import de.frederickayser.trafficsignrecognition.neuralnetwork.NeuralNetwork;
import de.frederickayser.trafficsignrecognition.util.Setting;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * by Frederic on 01.04.20(15:38)
 */
public class TrafficSignRecognition {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrafficSignRecognition.class);

    private static TrafficSignRecognition trafficSignRecognition;

    @Getter
    private NeuralNetwork neuralNetwork = null;

    public static TrafficSignRecognition getInstance() {
        if(trafficSignRecognition == null)
            trafficSignRecognition = new TrafficSignRecognition();
        return trafficSignRecognition;
    }

    public TrafficSignRecognition() {

    }

    public void run() {
        MessageBuilder.send(LOGGER, "System is starting...");
        ConfigurationHandler.getInstance().init();
        System.load(ConfigurationHandler.getInstance().getOpenCVLibaryPath());
        ImageTransformer.loadHighestIds();
        CommandHandler.getInstance().registerCommand("convertvideo", new ConvertVideoCommand());
        CommandHandler.getInstance().registerCommand("preparedataset", new PrepareDatasetCommand());
        CommandHandler.getInstance().registerCommand("train", new TrainCommand());
        CommandHandler.getInstance().registerCommand("detectsigns", new DetectSignsCommand());
        CommandHandler.getInstance().start();

        try {
            neuralNetwork = new NeuralNetwork(Setting.IMAGE_SIZE, Setting.IMAGE_SIZE, 1, 11);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageBuilder.send(LOGGER, "System initalized. All actions are now available.");
    }



}
