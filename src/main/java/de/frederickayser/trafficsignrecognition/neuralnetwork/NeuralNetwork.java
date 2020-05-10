package de.frederickayser.trafficsignrecognition.neuralnetwork;

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
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nadam;
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

    private DataNormalization dataNormalization;

    public NeuralNetwork(int width, int height, int channels, int outputAmount, int batchSize) throws IOException {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.outputAmount = outputAmount;

        int seed = 1234;
        this.randNumGen = new Random(seed);

        File file = new File("neuralnetwork.zip");
        if(file.exists()) {
            multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(file);
            MessageBuilder.send(LOGGER,"Network load from file");
        } else {


            MessageBuilder.send(LOGGER,"Configuring network...");
            /*Map<Integer, Double> learningRateSchedule = new HashMap<>();
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
                            .nOut(32)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new ConvolutionLayer.Builder(3, 3)
                            .stride(1, 1) // nIn need not specified in later layers
                            .nOut(64)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new ConvolutionLayer.Builder(3, 3)
                            .stride(1, 1) // nIn need not specified in later layers
                            .nOut(128)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                            .kernelSize(2, 2)
                            .stride(2, 2)
                            .build())
                    .layer(new DenseLayer.Builder().activation(Activation.RELU)
                            .nOut(128)
                            .build())
                    .layer(new DenseLayer.Builder().activation(Activation.RELU)
                            .nOut(256)
                            .build())
                    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .nOut(outputAmount)
                            .activation(Activation.SOFTMAX)
                            .build())
                    .setInputType(InputType.convolutionalFlat(height, width, channels)) // InputType.convolutional for normal image
                    .build();*/


            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(seed) //include a random seed for reproducibility
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .updater(new Nadam())
                    .l2(0.0005) // regularize learning model
                    .list()
                    .layer(new DenseLayer.Builder() //create the first input layer.
                            .nIn(width * height)
                            .nOut(512)
                            .name("Hiddenschicht 1")
                            .build())
                    .layer(new DenseLayer.Builder() //create the second input layer
                            .nIn(512)
                            .nOut(256)
                            .name("Hiddenschicht 2")
                            .build())
                    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                            .activation(Activation.SOFTMAX)
                            .nOut(outputAmount)
                            .name("Outputschicht")
                            .build())
                    .setInputType(InputType.convolutional(height, width, 1))
                    .build();

            multiLayerNetwork = new MultiLayerNetwork(conf);
            multiLayerNetwork.init();
        }

        multiLayerNetwork.setListeners(new ScoreIterationListener(1));
        dataNormalization = new ImagePreProcessingScaler();
        loadFilesInCache(batchSize);

    }

    public void loadFilesInCache(int batchSize) {
        File trainData = new File(ConfigurationHandler.getInstance().getTrainingPath() + "/images/");
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
        dataNormalization.fit(trainIter);
        trainIter.setPreProcessor(dataNormalization);

        trainingSetIterator = trainIter;

        // vectorization of test data
        File testData = new File(ConfigurationHandler.getInstance().getTestPath() + "/images/");
        FileSplit testSplit = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        ImageRecordReader testRR = new ImageRecordReader(height, width, channels, labelMaker);
        try {
            testRR.initialize(testSplit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, batchSize, 1, outputAmount);
        testIter.setPreProcessor(dataNormalization);

        testSetIterator = testIter;
    }



    public void train(int epochs) {
        if(trainingSetIterator == null) {
            throw new RuntimeException("Please load first the files in cache.");
        }
        multiLayerNetwork.fit(trainingSetIterator, epochs);
        trainingSetIterator.reset();
    }

    public void test() {
        if(testSetIterator == null) {
            throw new RuntimeException("Please load first the files in cache.");
        }
        Evaluation evaluation = multiLayerNetwork.evaluate(testSetIterator);
        testSetIterator.reset();
        File modelLogFolder = new File("oldmodels/");
        int id = modelLogFolder.listFiles().length;
        String name = "model-" + id + ".zip";
        File saveFile = new File("oldmodels/" + name);
        MessageBuilder.send(LOGGER, "Saved model under oldmodels/" + saveFile.getName() + evaluation.stats());

        File file = new File("neuralnetwork.zip");
        if(file.exists()) {
            file.delete();
        }
        try {
            ModelSerializer.writeModel(multiLayerNetwork, file, true);
            ModelSerializer.writeModel(multiLayerNetwork, saveFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public INDArray output(BufferedImage bufferedImage) {
        if(bufferedImage.getWidth() != width || bufferedImage.getHeight() != height)
            throw new RuntimeException("Imagesize must be equal to size of input");
        ImageLoader imageLoader = new ImageLoader(height, width, channels);
        INDArray indArray = imageLoader.asMatrix(bufferedImage).reshape(1, 1, height, width);
        MessageBuilder.send(LOGGER, MessageBuilder.MessageType.DEBUG, "prev: " + indArray.toStringFull());
        dataNormalization.transform(indArray);
        MessageBuilder.send(LOGGER, MessageBuilder.MessageType.DEBUG, "after: " + indArray.toStringFull());
        return multiLayerNetwork.output(indArray);
    }



}
