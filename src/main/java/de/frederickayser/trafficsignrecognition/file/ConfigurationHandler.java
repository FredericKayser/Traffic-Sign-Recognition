package de.frederickayser.trafficsignrecognition.file;

import lombok.Getter;
import org.json.JSONObject;

import java.io.File;

/**
 * by Frederic on 01.04.20(19:13)
 */
public class ConfigurationHandler {

    private static ConfigurationHandler configurationHandler;

    private JsonFile jsonFile;

    public static ConfigurationHandler getInstance() {
        if(configurationHandler == null)
            configurationHandler = new ConfigurationHandler();
        return configurationHandler;
    }

    public ConfigurationHandler() {
        jsonFile = new JsonFile("config");
    }

    public void init() {
        if(!jsonFile.isExisting()) {
            jsonFile.load();
            JSONObject dataObject = new JSONObject();
            dataObject.put("trainingpath", "data/trainingset/");
            dataObject.put("testpath", "data/testset/");
            jsonFile.getJsonObject().put("opencv_lib_path", "/home/pi/opencv/build/lib/libopencv_java342.so");
            jsonFile.getJsonObject().put("data", dataObject);
            save();
        } else {
            jsonFile.load();
        }
        String trainingPath = getTrainingPath();
        new File(trainingPath + "images/").mkdirs();
        new File(trainingPath + "videos/").mkdirs();
        trainingPath = trainingPath + "images/";
        new File(trainingPath + "30kmh/").mkdirs();
        new File(trainingPath + "50kmh/").mkdirs();
        new File(trainingPath + "70kmh/").mkdirs();
        new File(trainingPath + "80kmh/").mkdirs();
        new File(trainingPath + "80kmhEnd/").mkdirs();
        new File(trainingPath + "100kmh/").mkdirs();
        new File(trainingPath + "120kmh/").mkdirs();
        new File(trainingPath + "OvertakeForbidden/").mkdirs();
        new File(trainingPath + "OvertakeForbiddenEnd/").mkdirs();
        new File(trainingPath + "SpeedLimitOvertakeForbiddenEnd/").mkdirs();
        new File(trainingPath + "undefined/").mkdirs();

        String testPath = getTestPath();
        new File(testPath + "images/").mkdirs();
        new File(testPath + "videos/").mkdirs();
        testPath = testPath + "images/";
        new File(testPath + "30kmh/").mkdirs();
        new File(testPath + "50kmh/").mkdirs();
        new File(testPath + "70kmh/").mkdirs();
        new File(testPath + "80kmh/").mkdirs();
        new File(testPath + "80kmhEnd/").mkdirs();
        new File(testPath + "100kmh/").mkdirs();
        new File(testPath + "120kmh/").mkdirs();
        new File(testPath + "OvertakeForbidden/").mkdirs();
        new File(testPath + "OvertakeForbiddenEnd/").mkdirs();
        new File(testPath + "SpeedLimitOvertakeForbiddenEnd/").mkdirs();
        new File(testPath + "undefined/").mkdirs();


    }

    public void save() {
        jsonFile.save();
    }

    public String getTrainingPath() {
        return jsonFile.getJsonObject().getJSONObject("data").getString("trainingpath");
    }
    public String getTestPath() {
        return jsonFile.getJsonObject().getJSONObject("data").getString("testpath");
    }

    public String getOpenCVLibaryPath() { return jsonFile.getJsonObject().getString("opencv_lib_path"); }


}
