package de.frederickayser.trafficsignrecognition.image;

import de.frederickayser.trafficsignrecognition.command.TrainCommand;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import org.bytedeco.javacv.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * by Frederic on 01.04.20(18:08)
 */
public class DataPreparer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPreparer.class);

    private static DataPreparer dataPreparer;

    public static DataPreparer getInstance() {
        if (dataPreparer == null)
            dataPreparer = new DataPreparer();
        return dataPreparer;
    }

    public void addFrameNumbersToVideo(String input, String output) {
        OpenCVFrameConverter openCVFrameConverter = new OpenCVFrameConverter.ToMat();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File(input).getAbsolutePath());
        try {
            frameGrabber.start();
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(output,
                    frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            frameRecorder.setVideoCodec(frameGrabber.getVideoCodec());
            frameRecorder.setFormat(frameGrabber.getFormat());
            frameRecorder.setFrameRate(frameGrabber.getVideoFrameRate());
            frameRecorder.setSampleFormat(frameGrabber.getSampleFormat());
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.setVideoBitrate(frameGrabber.getVideoBitrate());
            frameRecorder.start();
            for (int i = 0; i < frameGrabber.getLengthInVideoFrames(); i++) {
                frameGrabber.setFrameNumber(i);
                Frame frame = frameGrabber.grab();
                frameRecorder.setFrameNumber(i);
                frameRecorder.setTimestamp(frameGrabber.getTimestamp());
                Mat mat = openCVFrameConverter.convertToOrgOpenCvCoreMat(frame);
                Point topLeft = new Point(mat.cols()-200, mat.rows()-50);
                Point botRight = new Point(mat.cols(), mat.rows());
                Imgproc.rectangle(mat, topLeft, botRight, new Scalar(255, 255, 255), -1);
                Imgproc.putText(mat, "", new Point(mat.cols()-180, mat.rows()-10), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 0));
                frameRecorder.record(openCVFrameConverter.convert(mat));
            }
            frameRecorder.stop();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareTrainingDataset() {
        File folder = new File(ConfigurationHandler.getInstance().getTrainingPath() + "images/");
        prepareDataset(folder);
    }

    public void prepareTestDataset() {
        File folder = new File(ConfigurationHandler.getInstance().getTestPath() + "images/");
        prepareDataset(folder);
    }

    private void prepareDataset(File folder) {
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isDirectory()) {
                if (!files[i].getName().endsWith("final.jpg")) {
                    MessageBuilder.send(LOGGER, MessageBuilder.MessageType.DEBUG, "Converting file " + files[i].getName() + ".");
                    ImageTransformer imageTransformer = new ImageTransformer(files[i].getAbsolutePath());
                    imageTransformer.transform();
                }
            } else
                prepareDataset(files[i]);
        }
    }

    public void saveTrainingImageTransformed(BufferedImage bufferedImage, Type type) {
        ImageTransformer imageTransformer = new ImageTransformer(bufferedImage, type);
        imageTransformer.transformTrainingImage();
    }

    public void saveTestImageTransformed(BufferedImage bufferedImage, Type type) {
        ImageTransformer imageTransformer = new ImageTransformer(bufferedImage, type);
        imageTransformer.transformTestImage();
    }

    public void convertTrainingVideos() {
        convertVideoToImages("training");
    }

    public void convertTrainingVideo(String name) {
        convertTrainingVideo(name, 0);
    }

    public void convertTrainingVideo(String name, int startAtFrame) {
        convertVideo("training", name, startAtFrame);
    }

    public void convertTestVideos() {
        convertVideoToImages("test");
    }

    public void convertTestVideo(String name) {
        convertTrainingVideo(name, 0);
    }

    public void convertTestVideo(String name, int startAtFrame) {
        convertVideo("test", name, startAtFrame);
    }

    private void convertVideoToImages(String settype) {
        MessageBuilder.send(LOGGER, MessageBuilder.MessageType.INFO, "Started searching frames with possible road signs.");
        File[] files = new File(settype.equals("training") ? ConfigurationHandler.getInstance().getTrainingPath() : ConfigurationHandler.getInstance().getTestPath() + "videos/").listFiles();
        for(int i = 0; i < files.length; i++) {
            convertVideo(settype, files[i].getName(), 0);
        }


    }

    public void convertTrainingVideoAutomatic(String name, int startFrame, int endFrame, Type type) {
        convertVideoAutomatic("training", name, startFrame, endFrame, type);
    }

    public void convertTestVideoAutomatic(String name, int startFrame, int endFrame, Type type) {
        convertVideoAutomatic("test", name, startFrame, endFrame, type);
    }

    private void convertVideoAutomatic(String settype, String fileName, int startFrame, int endFrame, Type type) {


        File file = new File((settype.equals("training") ? ConfigurationHandler.getInstance().getTrainingPath() : ConfigurationHandler.getInstance().getTestPath()) + "videos/" + fileName);

        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file.getAbsolutePath());
        try {
            frameGrabber.start();
            if(!(startFrame >= 0 || startFrame < endFrame || endFrame < frameGrabber.getLengthInFrames()))
                throw new RuntimeException("Frames out of range.");
            Frame frame;
            long millis = System.currentTimeMillis();
            MessageBuilder.send(LOGGER, "Started analyzing " + file.getName() + ".");
            for (int j = startFrame; j < endFrame; j++) {
                frameGrabber.setFrameNumber(j);
                frame = frameGrabber.grab();
                BufferedImage bufferedImage = frameConverter.convert(frame);
                Mat mat = ImageUtil.convertBufferedImageToMat(bufferedImage);
                Mat gray = new Mat();
                Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

                Imgproc.medianBlur(gray, gray, 5);

                Mat circles = new Mat();
                Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (gray.rows() / 5), 100, 33, 5, 50);

                for (int x = 0; x < circles.cols(); x++) {
                    double[] c = circles.get(0, x);
                    Point center = new Point(Math.round(c[0]), Math.round(c[1]));

                    int radius = (int) Math.round(c[2]);
                    int x1, y1, x2, y2;
                    x1 = (int) center.x - radius;
                    y1 = (int) center.y - radius;

                    x2 = (int) center.x + radius;
                    y2 = (int) center.y + radius;

                    x1 = (int) (x1 - ((x2 - x1) * 0.2));
                    if(x1 < 0) {
                        x1 = 0;
                    }
                    y1 = (int) (y1 - ((y2-y1) * 0.2));
                    if(y1 < 0) {
                        y1 = 0;
                    }
                    x2 = (int)(x2 + ((x2-x1) * 0.2));
                    if(x2 > bufferedImage.getWidth()) {
                        x2 = bufferedImage.getWidth();
                    }
                    y2 = (int) (y2 + ((y2-y1) * 0.2));
                    if(y2 > bufferedImage.getHeight()) {
                        y2 = bufferedImage.getHeight();
                    }

                    if(!((x2-x1) <= 0 || (y2-y1) <= 0)) {
                        BufferedImage smallImage = bufferedImage.getSubimage(x1, y1, (x2 - x1), (y2 - y1));
                        if (settype.equals("training")) {
                            DataPreparer.getInstance().saveTrainingImageTransformed(smallImage, type);
                        } else {
                            DataPreparer.getInstance().saveTestImageTransformed(smallImage, type);
                        }
                    }
                }
                mat.release();
                gray.release();
                circles.release();

            }
            long difference = System.currentTimeMillis() - millis;
            String time = String.format("%02d min, %02d sec",
                    TimeUnit.MILLISECONDS.toMinutes(difference),
                    TimeUnit.MILLISECONDS.toSeconds(difference) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
            MessageBuilder.send(LOGGER, "Finished analyzing " + file.getName() + ". Process took " + time + ".");
            file.delete();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    private void convertVideo(String settype, String fileName, int startAtFrame) {
        ImageFrame imageFrame = new ImageFrame(settype);
        File file = new File((settype.equals("training") ? ConfigurationHandler.getInstance().getTrainingPath() : ConfigurationHandler.getInstance().getTestPath()) + "videos/" + fileName);

        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file.getAbsolutePath());
        try {
            frameGrabber.start();
            if(!(startAtFrame >= 0 || startAtFrame < frameGrabber.getLengthInFrames()))
                throw new RuntimeException("Frames out of range.");
            imageFrame.initProgress(startAtFrame, frameGrabber.getLengthInFrames());
            Frame frame;
            long millis = System.currentTimeMillis();
            MessageBuilder.send(LOGGER, "Started analyzing " + file.getName() + ".");
            for (int j = startAtFrame; j < frameGrabber.getLengthInFrames(); j++) {
                frameGrabber.setFrameNumber(j);
                frame = frameGrabber.grab();
                BufferedImage bufferedImage = frameConverter.convert(frame);
                Mat mat = ImageUtil.convertBufferedImageToMat(bufferedImage);
                Mat gray = new Mat();
                Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

                Imgproc.medianBlur(gray, gray, 5);

                Mat circles = new Mat();
                Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (gray.rows() / 5), 100, 33, 5, 50);

                for (int x = 0; x < circles.cols(); x++) {
                    double[] c = circles.get(0, x);
                    Point center = new Point(Math.round(c[0]), Math.round(c[1]));

                    int radius = (int) Math.round(c[2]);
                    int x1, y1, x2, y2;
                    x1 = (int) center.x - radius;
                    y1 = (int) center.y - radius;

                    x2 = (int) center.x + radius;
                    y2 = (int) center.y + radius;

                    x1 = (int) (x1 - ((x2 - x1) * 0.2));
                    if(x1 < 0) {
                        x1 = 0;
                    }
                    y1 = (int) (y1 - ((y2-y1) * 0.2));
                    if(y1 < 0) {
                        y1 = 0;
                    }
                    x2 = (int)(x2 + ((x2-x1) * 0.2));
                    if(x2 > bufferedImage.getWidth()) {
                        x2 = bufferedImage.getWidth();
                    }
                    y2 = (int) (y2 + ((y2-y1) * 0.2));
                    if(y2 > bufferedImage.getHeight()) {
                        y2 = bufferedImage.getHeight();
                    }

                    if(!((x2-x1) <= 0 || (y2-y1) <= 0)) {
                        BufferedImage smallImage = bufferedImage.getSubimage(x1, y1, (x2 - x1), (y2 - y1));
                        imageFrame.updateImage(smallImage);
                        while (!imageFrame.getSubmitted().get());
                    }
                }
                mat.release();
                gray.release();
                circles.release();
                imageFrame.updateProgress(j);

            }
            long difference = System.currentTimeMillis() - millis;
            String time = String.format("%02d min, %02d sec",
                    TimeUnit.MILLISECONDS.toMinutes(difference),
                    TimeUnit.MILLISECONDS.toSeconds(difference) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
            MessageBuilder.send(LOGGER, "Finished analyzing " + file.getName() + ". Process took " + time + ".");
            file.delete();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }



}
