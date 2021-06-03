package model.models.enums;

public enum Income {
    Low("Low"),
    Medium("Medium"),
    High("High");

    String identifier;
    Income(String identifier) {
        this.identifier = identifier;
    }

    public static Income fromIdentifier(String identifier) {

        if(identifier.equals("Low")) {
            return Low;
        } else if(identifier.equals("Medium")) {
            return Medium;
        }

        return High;
    }

    public static boolean isValidIncome(String income) {
        return income.equals("Low") || income.equals("Medium") || income.equals("High");
    }
}