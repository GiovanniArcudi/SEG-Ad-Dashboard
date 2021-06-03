package model.models.enums;

public enum AgeBracket {

    Under_25("<25"),
    Between_25_34("25-34"),
    Between_35_44("35-44"),
    Between_45_54("45-54"),
    Above_54(">54");

    String identifier;

    AgeBracket(String identifier) {
        this.identifier = identifier;
    }

    public static AgeBracket fromIdentifier(String identifier) {
        switch (identifier) {
            case "<25":
                return Under_25;
            case "25-34":
                return Between_25_34;
            case "35-44":
                return Between_35_44;
            case "45-54":
                return Between_45_54;
            default:
                return Above_54;
        }
    }

    public static boolean isValidAgeBracket(String string) {
        return string.equals("<25") || string.equals("25-34") || string.equals("35-44") || string.equals("45-54") || string.equals(">54");
    }
}
