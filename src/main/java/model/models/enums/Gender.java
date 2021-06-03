package model.models.enums;

public enum Gender {
    Male("Male"),
    Female("Female");

    String identifier;
    Gender(String identifier) {
        this.identifier = identifier;
    }

    public static Gender fromIdentifier(String identifier) {
        return identifier.equals("Male") ? Male : Female;
    }

    public static boolean isValidGender(String gender) {
        return gender.equals("Male") || gender.equals("Female");
    }
}
