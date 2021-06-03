package model.utilities;

import model.models.enums.AgeBracket;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class PreferencesService {
    public static boolean hasPreviousCampaign() {
        File f = new File("settings.campaign");
        return !f.isDirectory() && f.exists();
    }

    public static void saveCampaign(String name, String clickLog, String impressionLog, String serverLog) {
        try {
            FileWriter writer = new FileWriter("settings.campaign");
            writer.write("clickLog=" + clickLog + '\n');
            writer.write("impressionLog=" + impressionLog + '\n');
            writer.write("serverLog=" + serverLog + '\n');
            writer.write("name=" + name);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Will be of the form filepath=''
    public static String getClickLog() throws IOException {
        return Files.lines(Paths.get("settings.campaign")).findFirst().get().split("=")[1];

    }

    public static String getImpressionLog() throws IOException {
        return Files.lines(Paths.get("settings.campaign")).skip(1).findFirst().get().split("=")[1];
    }

    public static String getServerLog() throws IOException {
        return Files.lines(Paths.get("settings.campaign")).skip(2).findFirst().get().split("=")[1];
    }

    public static String getPreviousName() throws IOException {
        String line = Files.lines(Paths.get("settings.campaign")).skip(3).findFirst().get();
        if(line.split("=").length < 2) {
            return "";
        }
        return line.split("=")[1];
    }
}
