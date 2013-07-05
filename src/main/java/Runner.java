/**
 * @author Hanna Eismant
 * 13.11.12
 */

import org.apache.log4j.Logger;

import java.util.concurrent.ForkJoinPool;

/**
 * Запускатор программы.
 */
public class Runner {

    protected static Logger logger = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        MergeSortingFileTask mainTask = new MergeSortingFileTask();
        ForkJoinPool pool = new ForkJoinPool();
        mainTask.setMainFileName(ApplicationProperties.getInFileName());
        pool.invoke(mainTask);

        logger.info("Sorting is complete. Gratis!");
        long end = System.currentTimeMillis();
        long delay = end - start;
        logger.debug(String.format("Time: %s ms", delay));
    }
}