/**
 * @author Hanna Eismant
 * 13.11.12
 */
import java.util.concurrent.ForkJoinPool;

/**
 * Запускатор программы.
 */
public class Runner {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        MergeSortingFileTask mainTask = new MergeSortingFileTask();
        ForkJoinPool pool = new ForkJoinPool();
        mainTask.setMainFileName(ApplicationProperties.getInFileName());
        pool.invoke(mainTask);

        System.out.println("[INFO] Sorting is complete. Gratis!");
        long end = System.currentTimeMillis();
        long delay = end - start;
        System.out.println("[INFO] Time: " + delay + "ms");
    }
}