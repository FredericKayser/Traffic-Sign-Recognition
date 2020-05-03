package de.frederickayser.trafficsignrecognition.trafficsign;

import lombok.Getter;

/**
 * by Frederic on 01.04.20(15:44)
 */
public enum Type {

    HUNDRET_KMH("100kmh", 0),
    HUNDRET_TWENTY_KMH("120kmh", 1),
    THIRTY_KMH("30kmh", 2),
    SEVENTY_KMH("70kmh", 3),
    FIFTY_KMH("50kmh", 4),
    EIGHTY_KMH("80kmh", 5),
    EIGHTY_KMH_END("80kmhEnd", 6),
    OVERTAKE_FORBIDDEN("OvertakeForbidden", 7),
    SPEED_LIMIT_OVERTAKE_FORBIDDEN_END("SpeedLimitOvertakeForbiddenEnd",8),
    OVERTAKE_FORBIDDEN_END("OvertakeForbiddenEnd", 9),
    UNDEFINED("undefined", 10);

    @Getter
    private String folder;
    @Getter
    private int id;

    Type(String folder, int id) {
        this.folder = folder;
        this.id = id;
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
