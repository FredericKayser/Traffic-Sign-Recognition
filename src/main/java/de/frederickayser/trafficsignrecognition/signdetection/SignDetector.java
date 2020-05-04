package de.frederickayser.trafficsignrecognition.signdetection;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.image.ImageTransformer;
import de.frederickayser.trafficsignrecognition.image.ImageUtil;
import de.frederickayser.trafficsignrecognition.trafficsign.LimitationType;
import de.frederickayser.trafficsignrecognition.trafficsign.Probability;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import de.frederickayser.trafficsignrecognition.util.Util;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * by Frederic on 02.05.20(14:35)
 */
public abstract class SignDetector {

    private final OpenCVFrameConverter openCVFrameConverter;

    private Type speedLimit, overtakeLimit;

    public SignDetector() {
        openCVFrameConverter = new OpenCVFrameConverter.ToMat();
        speedLimit = Type.FIFTY_KMH;
        overtakeLimit = Type.OVERTAKE_FORBIDDEN_END;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SignDetector.class);

    public Frame editFrame(BufferedImage bufferedImage, Frame frame) {
        Mat mat = openCVFrameConverter.convertToOrgOpenCvCoreMat(frame);
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

                Type type = Type.getTypeByID(probabilities[0].getSignID());

                if (probabilities[0].getProbability() > 0.9 && !type.equals(Type.UNDEFINED)) {

                    Imgproc.rectangle(mat, topLeft, rightBot, new Scalar(255, 255, 0), 3);

                    for(int i = 0; i < type.getLimitationTypes().length; i++) {
                        if(type.getLimitationTypes()[i].equals(LimitationType.SPEEDLIMIT)) {
                            speedLimit = type;
                        } else if(type.getLimitationTypes()[i].equals(LimitationType.OVERTAKELIMIT)) {
                            overtakeLimit = type;
                        }
                    }

                    Imgproc.putText(mat, Type.getTypeByID(probabilities[0].getSignID()) + ": " +
                            (Util.round(probabilities[0].getProbability(), 2)*100) + "%",
                            topLeft, Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 0), 1);
                }
            }
        }

        for(int i = 0; i < speedLimit.getLimitationTypes().length; i++) {
            if(speedLimit.getLimitationTypes()[i].equals(LimitationType.SPEEDLIMIT)) {
                Mat sign = speedLimit.getMats()[i];
                Mat submat = mat.submat(new Rect(mat.rows()-(sign.rows()*2)-1, mat.cols()-sign.cols()-1, sign.rows(), sign.cols()));
                sign.copyTo(submat);
            }
        }

        for(int i = 0; i < overtakeLimit.getLimitationTypes().length; i++) {
            if(overtakeLimit.getLimitationTypes()[i].equals(LimitationType.OVERTAKELIMIT)) {
                Mat sign = overtakeLimit.getMats()[i];
                Mat submat = mat.submat(new Rect(mat.rows()-sign.rows()-1, mat.cols()-sign.cols()-1, sign.rows(), sign.cols()));
                sign.copyTo(submat);
            }
        }

        Frame editedFrame = openCVFrameConverter.convert(mat);
        mat.release();
        gray.release();
        circles.release();
        return editedFrame;
    }


}
