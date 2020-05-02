package de.frederickayser.trafficsignrecognition.neuralnetwork;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.ImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.schedule.MapSchedule;
import org.nd4j.linalg.schedule.ScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * by Frederic on 02.05.20(13:30)
 */
public class NeuralNetwork {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeuralNetwork.class);

    private MultiLayerNetwork multiLayerNetwork;

    private final int width, height, channels, outputAmount;
    private Random randNumGen;

    private DataSetIterator trainingSetIterator, testSetIterator;

    public NeuralNetwork(int width, int height, int channels, int outputAmount) throws IOException {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.outputAmount = outputAmount;

        int seed = 1234;
        this.randNumGen = new Random(seed);

        File file = new File("neuralnetwork.zip");
        if(file.exists()) {
            multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(file);
        } else {


            MessageBuilder.send(LOGGER,"Network configuration and training...");
            Map<Integer, Double> learningRateSchedule = new HashMap<>();
            learningRateSchedule.put(0, 0.06);
            learningRateSchedule.put(200, 0.05);
            learningRateSchedule.put(600, 0.028);
            learningRateSchedule.put(800, 0.0060);
            learningRateSchedule.put(1000, 0.001);

            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(seed)
                    .l2(0.0005) // ridge regression value
                    .updater(new Nesterovs(new MapSchedule(ScheduleType.ITERATION, learningRateSchedule)))
                    .weightInit(WeightInit.XAVIER)
                    .list()
                    .layer(new ConvolutionLayer.Builder(3, 3)
                            .nIn(channels)
                            .stride(1, 1)
                            .nOut(20)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new ConvolutionLayer.Builder(3, 3)
                            .stride(1, 1) // nIn need not specified in later layers
                            .nOut(50)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new ConvolutionLayer.Builder(3, 3)
                            .stride(1, 1) // nIn need not specified in later layers
                            .nOut(100)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new DenseLayer.Builder().activation(Activation.RELU)
                            .nOut(500)
                            .build())
                    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .nOut(outputAmount)
                            .activation(Activation.SOFTMAX)
                            .build())
                    .setInputType(InputType.convolutionalFlat(height, width, channels)) // InputType.convolutional for normal image
                    .build();

            multiLayerNetwork = new MultiLayerNetwork(conf);
            multiLayerNetwork.init();
        }
    }

    public void loadFilesInCache(int batchSize) {
        File trainData = new File(ConfigurationHandler.getInstance().getTrainingPath());
        FileSplit trainSplit = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator(); // use parent directory name as the image label
        ImageRecordReader trainRR = new ImageRecordReader(height, width, channels, labelMaker);
        try {
            trainRR.initialize(trainSplit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSetIterator trainIter = new RecordReaderDataSetIterator(trainRR, batchSize, 1, outputAmount);

        // pixel values from 0-255 to 0-1 (min-max scaling)
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(trainIter);
        trainIter.setPreProcessor(scaler);

        trainingSetIterator = trainIter;

        // vectorization of test data
        File testData = new File(ConfigurationHandler.getInstance().getTestPath());
        FileSplit testSplit = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        ImageRecordReader testRR = new ImageRecordReader(height, width, channels, labelMaker);
        try {
            testRR.initialize(testSplit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, batchSize, 1, outputAmount);

        scaler.fit(testIter);
        testIter.setPreProcessor(scaler);

        testSetIterator = testIter;
    }

    public void train(int epochs) {
        if(trainingSetIterator == null) {
            throw new RuntimeException("Please load first the files in cache.");
        }
        multiLayerNetwork.fit(trainingSetIterator, epochs);

        File file = new File("neuralnetwork.zip");
        try {
            ModelSerializer.writeModel(multiLayerNetwork, file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() {
        if(testSetIterator == null) {
            throw new RuntimeException("Please load first the files in cache.");
        }
        Evaluation evaluation = multiLayerNetwork.evaluate(testSetIterator);
        MessageBuilder.send(LOGGER, evaluation.stats());
    }

    public INDArray output(BufferedImage bufferedImage) {
        if(bufferedImage.getWidth() != width || bufferedImage.getHeight() != height)
            throw new RuntimeException("Imagesize must be equal to size of input");
        ImageLoader imageLoader = new ImageLoader(height, width, channels);
        return multiLayerNetwork.output(imageLoader.asMatrix(bufferedImage).reshape(1, 1, height, width), false);
    }



}
