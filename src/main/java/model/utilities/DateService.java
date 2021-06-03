package model.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateService {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    /**
     * Converts the string representation of time to a Java object. It is more performant to create this when needed,
     * rather than running this function in the constructor.
     * @param time the string representation of time.
     * @return a LocalDateTime object that represents the time when the click occurred.
     */
    public static LocalDateTime parseDate(String time) {
        return LocalDateTime.parse(time, formatter);
    }

    public static boolean isValidDateString(String string) {
        return string.equals(formatter.format(parseDate(string)));
    }

    public static String toString(LocalDateTime date) {
        return date.format(formatter);
    }
}
