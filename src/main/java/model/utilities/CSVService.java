package model.utilities;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import model.models.enums.FileType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * CSVService handles interaction with a CSV file.
 * It contains methods to, given a file path, read the file and convert
 * each line of the file into a list of strings, allowing for the CSV file
 * to be processed by other classes.
 */
public class CSVService {
    /**
     * The parse method converts each line of the file at filepath into a list of strings.
     *
     * @param filepath the path from source root to a file as a String.
     * @return a list of lists of strings (a 2D matrix) representing the CSV file.
     */
    public static List<String[]> parse(String filepath, FileType fileType) {

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filepath));
            parser.beginParsing(reader);

            String[] header = parser.parseNext();
            switch (fileType) {
                case CLICK_LOG:
                    if (!validateClickHeader(header)) {
                        System.err.println("Click log invalid");
                        return new ArrayList<String[]>();
                    }
                    break;
                case IMPRESSION_LOG:
                    if (!validateImpressionHeader(header)) {
                        System.err.println("Impression log invalid");
                        return new ArrayList<String[]>();
                    }
                    break;
                case SERVER_LOG:
                    if (!validateServerHeader(header)) {
                        System.err.println("Server log invalid");
                        return new ArrayList<String[]>();
                    }
                    ;
                    break;

            }
            return parser.parseAll(new InputStreamReader(new FileInputStream(filepath)));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String[]>();
        }
    }

    private static boolean validateClickHeader(String[] line) {
        return line[0].equals("Date") && line[1].equals("ID") && line[2].equals("Click Cost");
    }

    private static boolean validateImpressionHeader(String[] line) {
        return line[0].equals("Date") && line[1].equals("ID") && line[2].equals("Gender") && line[3].equals("Age") && line[4].equals("Income") && line[5].equals("Context") && line[6].equals("Impression Cost");
    }

    private static boolean validateServerHeader(String[] line) {
        return Arrays.equals(line, new String[]{"Entry Date", "ID", "Exit Date", "Pages Viewed", "Conversion"});
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
