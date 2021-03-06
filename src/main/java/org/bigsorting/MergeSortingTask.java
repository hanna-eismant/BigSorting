/**
 * @author Hanna Eismant
 * 13.11.12
 */

package org.bigsorting;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.RecursiveTask;

/**
 * Merge sort Complexity O(n log n).
 */
public class MergeSortingTask extends RecursiveTask {

    protected static Logger     logger     = Logger.getLogger(MergeSortingTask.class.getName());
    protected static Properties properties = new Properties();

    protected List<Integer> data = new ArrayList<>();

    static {
        try {
            properties.load(MergeSortingTask.class
                    .getClassLoader().getResourceAsStream("org/bigsorting/config.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Set array to sort.
     *
     * @param pData Array to sort.
     */
    public void setData(final List<Integer> pData) {
        data = pData;
    }

    /**
     * Sorting an array. Use recursion. Sort the in ascending order.
     *
     * @param dataToSort Array to sort.
     *
     * @return Sorted array.
     */
    public List<Integer> sort(List<Integer> dataToSort) {
        int dataSize = dataToSort.size();

//        если в массиве один элемент, то нам ничего не надо делать просто возвращаем как есть
        if (dataSize <= 1) {
            return dataToSort;
        } else {
//             если в массиве несколько элемнтов, то делим массив пополам и уходим в рекурсию
//             и возвращаем объединение результатов
            int middleIndex = dataSize / 2;
//            все впихиваем в одну мегаконструкцию, чтобы не создавать дополнительных объектов и уменьшить расход памяти
            return merge(
                    sort(dataToSort.subList(0, middleIndex)),
                    sort(dataToSort.subList(middleIndex, dataSize))
            );
        }
    }

    /**
     * Объединение двух сортированных массивов.
     *
     * @param dataToMerge0 Первый массив для объединения. Должен быть отсортирован по возрастанию.
     * @param dataToMerge1 Второй массив для объединения. Должен быть отсортирован по возрастанию.
     *
     * @return Отсортированный масссив, полученный при объединении входных.
     */
    protected List<Integer> merge(List<Integer> dataToMerge0, List<Integer> dataToMerge1) {
//        индекс просматривемого элемента первого массива
        int a = 0;
//        индекс просматриваемого элемента второго массива
        int b = 0;
//        размер объединенного массива
        int mergedLen = dataToMerge0.size() + dataToMerge1.size();
//        объединенный массив
        List<Integer> merged = new ArrayList<>(mergedLen);

//        количество посмотров\сравнений столько же, сколько элементов в результирующем массиве
        for (int i = 0; i < mergedLen; i++) {
//            если мы не просмотрели элементы обоих массивов до конца (одновременно)
            if (a < dataToMerge0.size() && b < dataToMerge1.size()) {
//                сравниваем элементы обоих массивов и меньший добавляем в результирующий
//                при этом увеличиваем соответствующий счетчик (a или b)
                if (dataToMerge0.get(a) < dataToMerge1.get(b)) {
                    merged.add(dataToMerge0.get(a++));
                } else {
                    merged.add(dataToMerge1.get(b++));
                }
//                проверяем, какой массив недосмотрели и берем из него элемент(ы)
            } else if (a < dataToMerge0.size()) {
                merged.add(dataToMerge0.get(a++));
            } else if (b < dataToMerge1.size()) {
                merged.add(dataToMerge1.get(b++));
            }
        }

//        возвращаем результат
        return merged;
    }

    /**
     * Вычислительная часть задачи по сортировке массива.
     *
     * @return Отсортированный массив. При возникновении ошибки возвращает {@literal null}.
     */
    @Override
    protected Object compute() {
        logger.debug("Starting sort");

        int hresholdMemory;

        try {
            hresholdMemory = Integer.parseInt(properties.getProperty("hresholdMemory"));
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return null;
        }

        int dataSize = data.size();

//        если мы достигли порогового значения
        if (dataSize < hresholdMemory) {
//            то выполняем сортировку своей части
            return sort(data);
        } else {
//            в противном случае продолжаем разбивать
            MergeSortingTask subTask0 = new MergeSortingTask();
            MergeSortingTask subTask1 = new MergeSortingTask();

            int middleIndex = dataSize / 2;

            subTask0.setData(data.subList(0, middleIndex));
            subTask1.setData(data.subList(middleIndex, dataSize));

//            запускаем подзадачи
            subTask0.fork();
            subTask1.fork();

//            получаем результат подзадач
            List<Integer> result0 = (List<Integer>) subTask0.join();
            List<Integer> result1 = (List<Integer>) subTask1.join();

//            объединяем с сортировкой результат подзадач и возвращаем его головной задаче
            return merge(result0, result1);
        }
    }
}