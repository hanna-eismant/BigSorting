/**
 * @author Hanna Eismant
 *         13.11.12
 */
public final class ApplicationProperties {

    private static int    thresholdMemory;
    private static int    sizeArray;
    private static String inFileName;

    static {
        thresholdMemory = 32000;
        sizeArray = 128000;
        inFileName = "/home/hanna/tosort.file";
    }

    public static int getThresholdMemory() {
        return thresholdMemory;
    }

    public static int getSizeArray() {
        return sizeArray;
    }

    public static String getInFileName() {
        return inFileName;
    }
}