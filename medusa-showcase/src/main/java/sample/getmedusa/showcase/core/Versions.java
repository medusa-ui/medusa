package sample.getmedusa.showcase.core;

public class Versions {

    private Versions() {}

    public static String V_MEDUSA_UI = "0.9.5";
    public static String V_JDK = System.getProperty("java.version");

    public static String getVersionFooter() {
        return java.lang.String.format("JDK%s w/ Medusa %s", V_JDK, V_MEDUSA_UI);
    }

}
