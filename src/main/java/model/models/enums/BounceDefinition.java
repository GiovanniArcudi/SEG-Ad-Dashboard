package model.models.enums;

public enum BounceDefinition {
    SINGLE_PAGE,
    THRESHOLD;

    public static String toString(BounceDefinition m) {
        if(m == SINGLE_PAGE) {
            return "Visitor only views a single page";
        } else {
            return "Visitor leaves site within threshold";
        }
    }
}
