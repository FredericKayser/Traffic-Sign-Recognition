package de.frederickayser.trafficsignrecognition.image;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

/**
 * by Frederic on 02.04.20(10:25)
 */
public class ImageUtil {

    public static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }

    public static BufferedImage cropImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int difference = 0;
        if (width > height) {
            difference = width - height;
            int left = Math.round(difference / 2);
            int right = (difference / 2);
            width = width - right;
            bufferedImage = bufferedImage.getSubimage(left, 0, width, height);

        } else if (width < height) {
            difference = height - width;
            int top = difference / 2;
            int bottom = difference / 2;
            if ((difference % 2) == 1) {
                top += 1;
            }
            height = height - top;
            bufferedImage = bufferedImage.getSubimage(0, bottom, width, height);

        }
        return bufferedImage;
    }

    public static BufferedImage scaleImage(BufferedImage bufferedImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, width, height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }

}
