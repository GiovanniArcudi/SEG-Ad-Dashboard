package model.models.enums;

public enum TimeInterval {
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR;

    public static TimeInterval fromString(String string) {
        switch (string) {
            case "Hourly":
                return HOUR;
            default:
                return DAY;
            case "Weekly":
                return WEEK;
            case "Monthly":
                return MONTH;
            case "Yearly":
                return YEAR;
        }
    }

    public static String toString(TimeInterval i) {
        switch (i) {
            case HOUR:
                return "Hourly";
            default:
                return "Daily";
            case WEEK :
                return "Weekly";
            case MONTH:
                return "Monthly";
            case YEAR:
                return "Yearly";
        }
    }
}
