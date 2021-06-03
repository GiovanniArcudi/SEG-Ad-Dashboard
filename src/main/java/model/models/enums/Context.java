package model.models.enums;

public enum Context {
    Blog("Blog"),
    Shopping("Shopping"),
    Social_media("Social Media"),
    Hobbies("Hobbies"),
    Travel("Travel"),
    News("News");

    String identifier;

    Context(String identifier) {
        this.identifier = identifier;
    }

    public static Context fromIdentifier(String identifier) {
        switch (identifier) {
            case "Blog":
                return Blog;
            case "Shopping":
                return Shopping;
            case "Social Media":
                return Social_media;
            case "Travel":
                return Travel;
            case "Hobbies":
                return Hobbies;
            default:
                return News;
        }
    }

    public static boolean isValidContext(String context) {
        return context.equals("Blog") || context.equals("Shopping") || context.equals("Social Media") || context.equals("News") || context.equals("Hobbies") || context.equals("Travel");
    }
}