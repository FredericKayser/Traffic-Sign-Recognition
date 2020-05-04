package de.frederickayser.trafficsignrecognition.trafficsign;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.image.ImageUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * by Frederic on 01.04.20(15:44)
 */
public enum Type {

    HUNDRET_KMH("100kmh", 0, new String[]{"signs/100kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT}, null),
    HUNDRET_TWENTY_KMH("120kmh", 1, new String[]{"signs/120kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT}, null),
    THIRTY_KMH("30kmh", 2, new String[]{"signs/30kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT},null),
    FIFTY_KMH("50kmh", 3, new String[]{"signs/50kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT},null),
    SEVENTY_KMH("70kmh", 4, new String[]{"signs/70kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT},null),
    EIGHTY_KMH("80kmh", 5, new String[]{"signs/80kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT},null),
    EIGHTY_KMH_END("80kmhEnd", 6, new String[]{"signs/100kmh.jpg"}, new LimitationType[]{LimitationType.SPEEDLIMIT},null),
    OVERTAKE_FORBIDDEN("OvertakeForbidden", 7, new String[]{"signs/OvertakeForbidden.jpg"}, new LimitationType[]{LimitationType.OVERTAKELIMIT},null),
    SPEED_LIMIT_OVERTAKE_FORBIDDEN_END("SpeedLimitOvertakeForbiddenEnd",8, new String[]{"signs/100kmh.jpg", "signs/OvertakeForbiddenEnd.jpg"},
            new LimitationType[]{LimitationType.SPEEDLIMIT, LimitationType.OVERTAKELIMIT},null),
    OVERTAKE_FORBIDDEN_END("OvertakeForbiddenEnd", 9, new String[]{"signs/OvertakeForbiddenEnd.jpg"},
            new LimitationType[]{LimitationType.OVERTAKELIMIT},null),
    UNDEFINED("undefined", 10, null, null,null);

    @Getter
    private String folder;
    @Getter
    private int id;
    @Getter
    private String[] signImageNames;
    @Getter
    private LimitationType[] limitationTypes;
    @Getter
    private Mat[] mats;

    Type(String folder, int id, String[] signImageNames, LimitationType[] limitationTypes, Mat[] mats) {
        this.folder = folder;
        this.id = id;
        this.signImageNames = signImageNames;
        this.limitationTypes = limitationTypes;
        this.mats = mats;
    }

    public void load() throws IOException {
        if(signImageNames != null) {
            mats = new Mat[signImageNames.length];
            for(int i = 0; i < signImageNames.length; i++) {
                MessageBuilder.send(MessageBuilder.MessageType.DEBUG, "Loading sign image "+ signImageNames[i]);
                BufferedImage bufferedImage = ImageIO.read(new File(signImageNames[i]));
                bufferedImage = ImageUtil.cropImage(bufferedImage);
                bufferedImage = ImageUtil.scaleImage(bufferedImage, 256, 256);
                mats[i] = ImageUtil.convertBufferedImageToMat(bufferedImage);
                MessageBuilder.send(MessageBuilder.MessageType.DEBUG, "Sign image " + signImageNames[i] + " loaded.");
            }
        }
    }

    public static void loadAllMats() {
        try {
            HUNDRET_KMH.load();
            HUNDRET_TWENTY_KMH.load();
            THIRTY_KMH.load();
            FIFTY_KMH.load();
            SEVENTY_KMH.load();
            EIGHTY_KMH.load();
            EIGHTY_KMH_END.load();
            OVERTAKE_FORBIDDEN.load();
            OVERTAKE_FORBIDDEN_END.load();
            SPEED_LIMIT_OVERTAKE_FORBIDDEN_END.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Type getTypeByFolder(String folder) {
        switch (folder) {
            case "70kmh":
                return SEVENTY_KMH;
            case "50kmh":
                return FIFTY_KMH;
            case "30kmh":
                return THIRTY_KMH;
            case "80kmh":
                return EIGHTY_KMH;
            case "80kmhEnd":
                return EIGHTY_KMH_END;
            case "100kmh":
                return HUNDRET_KMH;
            case "120kmh":
                return HUNDRET_TWENTY_KMH;
            case "OvertakeForbidden":
                return OVERTAKE_FORBIDDEN;
            case "SpeedLimitOvertakeForbiddenEnd":
                return SPEED_LIMIT_OVERTAKE_FORBIDDEN_END;
            case "OvertakeForbiddenEnd":
                return OVERTAKE_FORBIDDEN_END;
            default:
                return UNDEFINED;
        }
    }

    public static Type getTypeByID(int id) {
        switch (id) {
            case 0:
                return HUNDRET_KMH;
            case 1:
                return HUNDRET_TWENTY_KMH;
            case 2:
                return THIRTY_KMH;
            case 3:
                return FIFTY_KMH;
            case 4:
                return SEVENTY_KMH;
            case 5:
                return EIGHTY_KMH;
            case 6:
                return EIGHTY_KMH_END;
            case 7:
                return OVERTAKE_FORBIDDEN;
            case 8:
                return SPEED_LIMIT_OVERTAKE_FORBIDDEN_END;
            case 9:
                return OVERTAKE_FORBIDDEN_END;
            default:
                return UNDEFINED;

        }
    }

}
