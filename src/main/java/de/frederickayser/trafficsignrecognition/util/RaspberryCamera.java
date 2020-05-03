package de.frederickayser.trafficsignrecognition.util;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * by Frederic on 02.05.20(23:50)
 */
public class RaspberryCamera {

    @Getter
    @Setter
    private int width, height, rotation;

    public RaspberryCamera(int width, int height) {
        this(width, height, 0);
    }

    public RaspberryCamera(int width, int height, int rotation) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public BufferedImage getImage() {
        List<String> command = new ArrayList<>();
        command.add("raspistill");
        command.add("-o");
        command.add("-v");
        command.add("-w");
        command.add(String.valueOf(this.width));
        command.add("-h");
        command.add(String.valueOf(this.height));
        command.add("-rot");
        command.add(String.valueOf(this.rotation));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();

            BufferedImage bufferedImage = ImageIO.read(process.getInputStream());

            process.getInputStream().close();
            process.destroy();

            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
