/**
 * @author Hanna Eismant
 * 13.11.12
 */

package org.bigsorting;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

/**
 * Запускатор программы.
 */
public class Runner {

    protected static Logger logger = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Properties properties = new Properties();
        try {
            properties.load(Runner.class
                    .getClassLoader().getResourceAsStream("org/bigsorting/config.properties"));
            String inFileName = properties.getProperty("inFileName");

            MergeSortingFileTask mainTask = new MergeSortingFileTask();
            ForkJoinPool pool = new ForkJoinPool();
            mainTask.setMainFileName(inFileName);
            pool.invoke(mainTask);

            logger.info("Sorting is complete. Gratis!");
            long end = System.currentTimeMillis();
            long delay = end - start;
            logger.debug(String.format("Time: %s ms", delay));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }
}