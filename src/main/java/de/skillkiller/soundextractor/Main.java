package de.skillkiller.soundextractor;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        File targetDir = getInputFromUser("Output Directoy", "output/", CheckState.NONE);
        File indexJson = getInputFromUser("Version Index Json", System.getenv("APPDATA") + "/.minecraft/assets/indexes/1.14.json", CheckState.FILE);
        File objectsDir = getInputFromUser("Objects Dir", System.getenv("APPDATA") + "/.minecraft/assets/objects/", CheckState.DIRECTORY);
        targetDir.mkdirs();

        FileReader versionReader = new FileReader(indexJson.getAbsoluteFile());
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(versionReader);
        JSONObject minecraftObjects = (JSONObject) jsonObject.get("objects");

        for (Object o : minecraftObjects.keySet()) {
            String key = o.toString();
            if (key.endsWith(".ogg")) {
                JSONObject soundObject = (JSONObject) minecraftObjects.get(key);
                String hash = (String) soundObject.get("hash");

                File soundFile = new File(objectsDir.getAbsolutePath() + "/" + hash.substring(0, 2) + "/" + hash);
                if (soundFile.exists()) {
                    File targetFile = new File(targetDir.getAbsolutePath() + "/" + key.replaceAll("/", "-"));
                    FileUtils.copyFile(soundFile, targetFile);
                    System.out.println(soundFile.getAbsolutePath() + "/ -> " + targetFile.getAbsolutePath());
                } else {
                    System.err.println(key + " not found: " + soundFile.getAbsolutePath());
                }
            }
        }
    }

    private static File getInputFromUser(String message, String sdefault, CheckState check) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%s[%s]: ", message, sdefault);
        String returnString = scanner.nextLine();

        if (returnString.length() == 0) {
            return new File(sdefault);
        } else {
            File file = new File(returnString);
            switch (check) {
                case NONE:
                    return file;
                case FILE:
                    if (file.isFile()) {
                        return file;
                    } else {
                        System.out.println("Input wrong!");
                        return getInputFromUser(message, sdefault, check);
                    }
                case DIRECTORY:
                    if (file.isDirectory()) {
                        return file;
                    } else {
                        System.out.println("Input wrong!");
                        return getInputFromUser(message, sdefault, check);
                    }
                default:
                    return new File(sdefault);
            }
        }
    }

    private enum CheckState {
        NONE, FILE, DIRECTORY
    }
}
