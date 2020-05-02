package de.frederickayser.trafficsignrecognition.file;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * by Frederic on 25.11.19(09:56)
 */
public class JsonFile {

    @Getter
    private String name;
    @Getter
    @Setter
    private JSONObject jsonObject;

    private File file;

    public JsonFile(String name) {
        this.name = name;
        file = new File(name + ".json");
    }

    public boolean isExisting() {
        return file.exists();
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        if(content != null && content.length() > 0) {
            jsonObject = new JSONObject(content);
        }

        this.jsonObject = jsonObject;

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(getJsonObject().toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(getJsonObject().toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
