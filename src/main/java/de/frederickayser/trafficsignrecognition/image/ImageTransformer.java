package de.frederickayser.trafficsignrecognition.image;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.file.ConfigurationHandler;
import de.frederickayser.trafficsignrecognition.trafficsign.Type;
import de.frederickayser.trafficsignrecognition.util.Setting;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * by Frederic on 01.04.20(16:00)
 */
public class ImageTransformer {

    private static HashMap<String, Integer> highestIdTraining = new HashMap<>();
    private static HashMap<String, Integer> highestIdTest = new HashMap<>();

    public static void loadHighestIds() {
        File[] folders = new File(ConfigurationHandler.getInstance().getTrainingPath() + "images/").listFiles();
        for(int i = 0; i < folders.length; i++) {
            int id = folders[i].listFiles().length-1;
            highestIdTraining.put(folders[i].getName(), id);
        }

        folders = new File(ConfigurationHandler.getInstance().getTestPath() + "images/").listFiles();
        for(int i = 0; i < folders.length; i++) {
            int id = folders[i].listFiles().length-1;
            highestIdTest.put(folders[i].getName(), id);
        }
    }

    private File file;
    private String fileName;
    private BufferedImage bufferedImage;
    private Type type;

    public ImageTransformer(String file) {
        String end = file.split("\\.")[file.split("\\.").length - 1];
        boolean found = false;
        for (int i = 0; i < ImageIO.getReaderFormatNames().length; i++) {
            if (ImageIO.getReaderFormatNames()[i].equals(end)) {
                found = true;
                break;
            }
        }
        if (!found) {
            try {
                String oldFile = file;
                file = ImageConverter.convert(file, "jpeg");
                new File(oldFile).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileName = file;
        this.file = new File(file);
        try {
            this.bufferedImage = ImageIO.read(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageTransformer(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public ImageTransformer(BufferedImage bufferedImage, Type type) {
        this.bufferedImage = bufferedImage;
        this.type = type;
    }

    public void transform() {
        try {

            bufferedImage = cropImage(bufferedImage);

            bufferedImage = scaleImage(bufferedImage);

            bufferedImage = grayscaleImage(bufferedImage);

            if(type == null) {
                file.delete();
                String folder = file.getPath().split("images/")[1].split("/")[0];
                String type = file.getPath().split("/images")[0].split("/")[file.getPath().split("/images")[0].split("/").length-1];
                if(type.equalsIgnoreCase("trainingset")) {
                    ImageIO.write(bufferedImage, "jpg", new File(ConfigurationHandler.getInstance().getTrainingPath() + "images/" + folder + "/" + getTrainingID(Type.getTypeByFolder(folder)) + "-final.jpg"));
                } else {
                    ImageIO.write(bufferedImage, "jpg", new File(ConfigurationHandler.getInstance().getTestPath() + "images/" + folder + "/" + getTestID(Type.getTypeByFolder(folder)) + "-final.jpg"));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage transformWithoutSaving() {
        bufferedImage = cropImage(bufferedImage);

        bufferedImage = scaleImage(bufferedImage);

        return grayscaleImage(bufferedImage);
    }

    public void transformTrainingImage() {
        transform();
        try {
            ImageIO.write(bufferedImage, "jpg", new File(ConfigurationHandler.getInstance().getTrainingPath() + "images/" + type.getFolder() + "/" + getTrainingID(type) + "-final.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transformTestImage() {
        transform();
        try {
            ImageIO.write(bufferedImage, "jpg", new File("data/testset/images/" + type.getFolder() + "/" + getTestID(type) + "-final.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage transformWithoutGrayscale() {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);

            bufferedImage = cropImage(bufferedImage);

            bufferedImage = scaleImage(bufferedImage);

            file.delete();

            ImageIO.write(bufferedImage, "jpg", new File(fileName.replaceAll("\\.jpeg", "-final.jpg")));

            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage cropImage(BufferedImage bufferedImage) {
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

    private BufferedImage scaleImage(BufferedImage bufferedImage) {
        BufferedImage resizedImage = new BufferedImage(Setting.IMAGE_SIZE, Setting.IMAGE_SIZE, bufferedImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, Setting.IMAGE_SIZE, Setting.IMAGE_SIZE, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }

    private BufferedImage grayscaleImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = bufferedImage.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                int avg = (r+g+b)/3;

                p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                bufferedImage.setRGB(x, y, p);
            }
        }
        return bufferedImage;

    }

    private static int getTrainingID(Type folder) {
        int id = highestIdTraining.get(folder.getFolder());
        id++;
        highestIdTraining.put(folder.getFolder(), id);
        return id;
    }

    private static int getTestID(Type folder) {
        int id = highestIdTest.get(folder.getFolder());
        id++;
        highestIdTest.put(folder.getFolder(), id);
        return id;
    }

}
