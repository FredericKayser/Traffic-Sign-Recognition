package de.frederickayser.trafficsignrecognition.util;

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


}
