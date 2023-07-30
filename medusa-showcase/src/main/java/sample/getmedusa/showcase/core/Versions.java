package sample.getmedusa.showcase.core;

public class Versions {

    private Versions() {}

    private static final String V_MEDUSA_UI = "0.9.5";
    private static final String V_JDK = "20";

    public static String getVersionFooter() {
        return String.format("JDK%s w/ Medusa %s", V_JDK, V_MEDUSA_UI);
    }

}
