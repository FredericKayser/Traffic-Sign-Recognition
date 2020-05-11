package de.frederickayser.trafficsignrecognition.signdetection;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import de.frederickayser.trafficsignrecognition.image.ImageTransformer;
import de.frederickayser.trafficsignrecognition.image.ImageUtil;
import de.frederickayser.trafficsignrecognition.trafficsign.LimitationType;
import de.frederickayser.trafficsignrecognition.trafficsign.Probability;
import de.frederickayser.trafficsignrecognition.trafficsign.Sign;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import de.frederickayser.trafficsignrecognition.util.Tuple;
import de.frederickayser.trafficsignrecognition.util.Util;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * by Frederic on 02.05.20(14:35)
 */
public abstract class SignDetector {

    private final OpenCVFrameConverter openCVFrameConverter;
    private final HashMap<Integer, Sign> signConfirmer = new HashMap<>();

    private Type speedLimit, overtakeLimit;

    public SignDetector() {
        openCVFrameConverter = new OpenCVFrameConverter.ToMat();
        speedLimit = Type.FIFTY_KMH;
        overtakeLimit = Type.OVERTAKE_FORBIDDEN_END;
        signConfirmer.put(Type.HUNDRET_KMH.getId(), new Sign(Type.HUNDRET_KMH));
        signConfirmer.put(Type.HUNDRET_TWENTY_KMH.getId(), new Sign(Type.HUNDRET_TWENTY_KMH));
        signConfirmer.put(Type.THIRTY_KMH.getId(), new Sign(Type.THIRTY_KMH));
        signConfirmer.put(Type.FIFTY_KMH.getId(), new Sign(Type.FIFTY_KMH));
        signConfirmer.put(Type.SEVENTY_KMH.getId(), new Sign(Type.SEVENTY_KMH));
        signConfirmer.put(Type.EIGHTY_KMH.getId(), new Sign(Type.EIGHTY_KMH));
        signConfirmer.put(Type.EIGHTY_KMH_END.getId(), new Sign(Type.EIGHTY_KMH_END));
        signConfirmer.put(Type.OVERTAKE_FORBIDDEN.getId(), new Sign(Type.OVERTAKE_FORBIDDEN));
        signConfirmer.put(Type.OVERTAKE_FORBIDDEN_END.getId(), new Sign(Type.OVERTAKE_FORBIDDEN_END));
        signConfirmer.put(Type.SPEED_LIMIT_OVERTAKE_FORBIDDEN_END.getId(), new Sign(Type.SPEED_LIMIT_OVERTAKE_FORBIDDEN_END));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SignDetector.class);

    public Frame editFrame(BufferedImage bufferedImage, Frame frame) {
        Mat mat = openCVFrameConverter.convertToOrgOpenCvCoreMat(frame);
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.medianBlur(gray, gray, 5);

        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (gray.rows() / 5), 100, 33, 8, 50);

        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));

            int radius = (int) Math.round(c[2]);
            int x1, y1, x2, y2;
            x1 = (int) center.x - radius;
            y1 = (int) center.y - radius;

            x2 = (int) center.x + radius;
            y2 = (int) center.y + radius;

            x1 = (int) (x1 - ((x2 - x1) * ConfigurationHandler.getInstance().getCircleMultiplier()));
            if (x1 < 0) {
                x1 = 0;
            }
            y1 = (int) (y1 - ((y2 - y1) * ConfigurationHandler.getInstance().getCircleMultiplier()));
            if (y1 < 0) {
                y1 = 0;
            }
            x2 = (int) (x2 + ((x2 - x1) * ConfigurationHandler.getInstance().getCircleMultiplier()));
            if (x2 > bufferedImage.getWidth()) {
                x2 = bufferedImage.getWidth();
            }
            y2 = (int) (y2 + ((y2 - y1) * ConfigurationHandler.getInstance().getCircleMultiplier()));
            if (y2 > bufferedImage.getHeight()) {
                y2 = bufferedImage.getHeight();
            }

            if (!((x2 - x1) <= 0 || (y2 - y1) <= 0)) {
                BufferedImage smallImage = bufferedImage.getSubimage(x1, y1, (x2 - x1), (y2 - y1));
                ImageTransformer imageTransformer = new ImageTransformer(smallImage);
                BufferedImage transformedImage = imageTransformer.transformWithoutSaving();

                double[] output = TrafficSignRecognition.getInstance().getNeuralNetwork().output(transformedImage).toDoubleVector();
                Probability[] probabilities = new Probability[output.length];
                //MessageBuilder.send(MessageBuilder.MessageType.DEBUG, Arrays.toString(output));
                for (int j = 0; j < output.length; j++) {
                    probabilities[j] = new Probability(j, output[j]);
                }

                Arrays.sort(probabilities, Collections.reverseOrder());

                Point topLeft = new Point(x1, y1);
                Point rightBot = new Point(x2, y2);

                Type type = Type.getTypeByID(probabilities[0].getSignID());
                if (!type.equals(Type.UNDEFINED) && probabilities[0].getProbability() > 0.6) {

                    Imgproc.rectangle(mat, topLeft, rightBot, new Scalar(255, 255, 0), 3);

                    String percentage = String.valueOf(Util.round(probabilities[0].getProbability(), 4)*100);


                    Imgproc.putText(mat, type + ": " +
                                   percentage + "%",
                            topLeft, Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 0), 1);
                    signConfirmer.get(type.getId()).seen();
                }



            }
        }


        for(int id : signConfirmer.keySet()) {
            Sign sign = signConfirmer.get(id);
            if(sign.isConfirmed()) {
                for(int i = 0; i < sign.getType().getLimitationTypes().length; i++) {
                    if(sign.getType().getLimitationTypes()[i].equals(LimitationType.SPEEDLIMIT)) {
                        speedLimit = sign.getType();
                    } else if(sign.getType().getLimitationTypes()[i].equals(LimitationType.OVERTAKELIMIT)) {
                        overtakeLimit = sign.getType();
                    }
                }
            }
            if(!sign.isChanged()) {
                sign.notSeen();
            }
            signConfirmer.get(id).reset();
        }


        for(int i = 0; i < speedLimit.getLimitationTypes().length; i++) {
            if(speedLimit.getLimitationTypes()[i].equals(LimitationType.SPEEDLIMIT)) {
                Mat sign = speedLimit.getMats()[i];
                Mat submat = mat.submat(new Rect(mat.cols()-(sign.cols()*2), mat.rows()-sign.rows(), sign.rows(), sign.cols()));
                sign.copyTo(submat);
            }
        }

        for(int i = 0; i < overtakeLimit.getLimitationTypes().length; i++) {
            if(overtakeLimit.getLimitationTypes()[i].equals(LimitationType.OVERTAKELIMIT)) {
                Mat sign = overtakeLimit.getMats()[i];
                Mat submat = mat.submat(new Rect(mat.cols()-sign.cols(), mat.rows()-sign.rows(), sign.rows(), sign.cols()));
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
