package de.frederickayser.trafficsignrecognition.signdetection;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.image.ImageTransformer;
import de.frederickayser.trafficsignrecognition.image.ImageUtil;
import de.frederickayser.trafficsignrecognition.trafficsign.Probability;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import de.frederickayser.trafficsignrecognition.util.Util;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * by Frederic on 02.05.20(14:35)
 */
public class SignDetector {

    private final File file;
    private final String outputPath;

    public SignDetector(String filePath, String outputPath) {
        this.file = new File(filePath);
        this.outputPath = outputPath;
    }

    public void detect() {
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        OpenCVFrameConverter openCVFrameConverter = new OpenCVFrameConverter.ToMat();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file.getAbsolutePath());
        try {
            frameGrabber.start();
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(outputPath,
                    frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            frameRecorder.setFormat("mp4");
            frameRecorder.setFrameRate(frameGrabber.getFrameRate());
            frameRecorder.setSampleFormat(frameGrabber.getSampleFormat());
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.start();
            for (int i = 0; i < frameGrabber.getFrameNumber(); i++) {
                frameGrabber.setFrameNumber(i);
                Frame frame = frameGrabber.grab();
                BufferedImage bufferedImage = frameConverter.getBufferedImage(frame);
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
                    if (x1 < 0) {
                        x1 = 0;
                    }
                    y1 = (int) (y1 - ((y2 - y1) * 0.2));
                    if (y1 < 0) {
                        y1 = 0;
                    }
                    x2 = (int) (x2 + ((x2 - x1) * 0.2));
                    if (x2 > bufferedImage.getWidth()) {
                        x2 = bufferedImage.getWidth();
                    }
                    y2 = (int) (y2 + ((y2 - y1) * 0.2));
                    if (y2 > bufferedImage.getHeight()) {
                        y2 = bufferedImage.getHeight();
                    }

                    if (!((x2 - x1) <= 0 || (y2 - y1) <= 0)) {
                        BufferedImage smallImage = bufferedImage.getSubimage(x1, y1, (x2 - x1), (y2 - y1));
                        ImageTransformer imageTransformer = new ImageTransformer(smallImage);
                        BufferedImage transformedImage = imageTransformer.transformWithoutSaving();

                        float[] output = TrafficSignRecognition.getInstance().getNeuralNetwork().output(transformedImage).toFloatVector();
                        Probability[] probabilities = new Probability[output.length];
                        for (int j = 0; j < output.length; j++) {
                            probabilities[j] = new Probability(j, output[j]);
                        }

                        Arrays.sort(probabilities, Collections.reverseOrder());

                        Point topLeft = new Point(x1, y1);
                        Point rightBot = new Point(x2, y2);

                        if (probabilities[0].getProbability() > 0.8) {

                            Imgproc.rectangle(mat, topLeft, rightBot, new Scalar(255, 255, 0), 3);
                            Imgproc.putText(mat, Type.getTypeByID(probabilities[0].getSignID()) + ": " +
                                    (Util.round(probabilities[0].getProbability(), 2)*100) + "%", topLeft, 0, 5, new Scalar(255, 255, 255));
                        }
                    }
                }

                Frame frame1 = openCVFrameConverter.convert(mat);
                frameRecorder.setFrameNumber(i);
                frameRecorder.record(frame1);

                mat.release();
                gray.release();
                circles.release();
            }
            frameRecorder.stop();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }


}
