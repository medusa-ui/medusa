package io.getmedusa.medusa.core.util;

public class IdentifierGenerator {

    private IdentifierGenerator() {}

    private static int counter = 0;

    public static String generateIfID() {
        return "if-" + counter++;
    }

    public static String generateTemplateID() {
        return "t-" + counter++;
    }

    public static String generateIterationID() {
        return "i-" + counter++;
    }

}
