import java.io.FileInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @autor Hanna Eismant
 * 13.11.12
 */
public class Util {

    private static CopyOnWriteArrayList<FileInputStream> tempFiles;

    static {
        tempFiles = new CopyOnWriteArrayList<FileInputStream>();
    }

    public static CopyOnWriteArrayList<FileInputStream> getTempFiles() {
        return tempFiles;
    }
}
