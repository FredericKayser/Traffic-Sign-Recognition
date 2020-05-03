package de.frederickayser.trafficsignrecognition.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * by Frederic on 03.04.20(19:49)
 */
public class Util {

    public static double round(double value, int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }

    public static double round(float value, int frac) {
        return Math.round((float) (Math.pow(10.0, frac) * value)) / Math.pow(10.0, frac);
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
