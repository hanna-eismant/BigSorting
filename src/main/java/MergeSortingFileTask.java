/**
 * @author Hanna Eismant
 * 13.11.12
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;


public class MergeSortingFileTask extends RecursiveAction {

    //    имя файла для обработки
    private String mainFileName;
    //    имя одного временного файла
    private String tempFileName0;
    //    имя второго временного файла
    private String tempFileName1;

    /**
     * Задать файл для обработки.
     *
     * @param pMainFileName Полный путь к файлу.
     */
    public void setMainFileName(final String pMainFileName) {
        mainFileName = pMainFileName;
    }

    private void merge() throws IOException {
        System.out.println("[INFO] Merge files");

//        файлы
        FileOutputStream resultFile = new FileOutputStream(mainFileName);
        FileInputStream tempFile0 = new FileInputStream(tempFileName0);
        FileInputStream tempFile1 = new FileInputStream(tempFileName1);

//        сюда читаем из temp файлов
        Integer from0 = null;
        Integer from1 = null;

//        количество доступных байт для чтения
        int available0 = tempFile0.available();
        int available1 = tempFile1.available();

//        пока есть что читать в обоих файлах одновременно
        while (available0 > 0 && available1 > 0) {
            if (from0 == null) {
                from0 = tempFile0.read();
            }

            if (from1 == null) {
                from1 = tempFile1.read();
            }

            if (from0 < from1) {
                resultFile.write(from0);
                from0 = null;
            } else {
                resultFile.write(from1);
                from1 = null;
            }

            available0 = tempFile0.available();
            available1 = tempFile1.available();
        }

//        после цикла выше в одном из файлов еще что-нить осталось вот это мы и дочитываем
        while (available0 > 0) {
            from0 = tempFile0.read();
            resultFile.write(from0);
            available0 = tempFile0.available();
        }

        while (available1 > 0) {
            from1 = tempFile1.read();
            resultFile.write(from1);
            available1 = tempFile1.available();
        }

//        теперь имеем отсортированные данные в исходном файле временные файлы можно удалить
        resultFile.close();
        tempFile0.close();
        tempFile1.close();
        File f0 = new File(tempFileName0);
        f0.delete();
        File f1 = new File(tempFileName1);
        f1.delete();
    }

    /**
     * Вычислительная часть по разбиению файла на части и отправки их на сортирвку.
     */
    @Override
    protected void compute() {
//        если не задано имя исходного (главного) файла, то выдаем ошибку и закрываемся
        if (mainFileName == null || mainFileName.isEmpty()) {
            System.err.println("[ERROR] Filename not set");
            return;
        }

        try {
//            главный файл, с которго читаем
            FileInputStream mainFile = new FileInputStream(mainFileName);

//            количество байтов, изначально доступных для чтеия
            int availableSize = mainFile.available();

            System.out.println("[INFO] Total size: " + availableSize);

//            устанавливем первоначальный размер для чтения
            int sizeToRead = availableSize;

//            если читаемый кусок можно поместить в память и обработать, то его отправляем на сортировку
            if (sizeToRead < ApplicationProperties.getSizeArray()) {
//                читаем данные из файла и запихиваем их в массив
                ArrayList<Integer> data = new ArrayList<Integer>();

                for (int i = 0; i < sizeToRead; i++) {
                    data.add(mainFile.read());
                }

                mainFile.close();
//                создаем и запускаем задачу на сортировку массива
                MergeSortingTask task = new MergeSortingTask();
                task.setData(data);
                task.fork();
//                получаем отсортированный массив
                List<Integer> sort = (List<Integer>) task.join();
//                открываем главный файл для записи
                FileOutputStream mainResult = new FileOutputStream(mainFileName);

                System.out.println("[INFO] Writing result into file: '" + mainFileName + "'");

//                записываем в него отсортированный массив
                for (Integer aSort : sort) {
                    mainResult.write(aSort);
                }

                mainResult.close();

                System.out.println("[INFO] Task is complete");

//                закрываем текущую задачу
                return;
            }

//            пути для временных файлов
            tempFileName0 = File.createTempFile("bigSort", "tmp").getAbsolutePath();
            tempFileName1 = File.createTempFile("bigSort", "tmp").getAbsolutePath();

            System.out.println("[INFO] Create temp file: '" + tempFileName0 + "'");
            System.out.println("[INFO] Create temp file: '" + tempFileName1 + "'");

//            открываем temp для записи
            FileOutputStream tempFileWrite0 = new FileOutputStream(tempFileName0);
            FileOutputStream tempFileWrite1 = new FileOutputStream(tempFileName1);

            while (availableSize > 0) {
                System.out.println("[INFO] Riding size: " + sizeToRead);

//                находим середину
                int middleToRead = sizeToRead / 2;

//                первую половину куска записываем в 0й temp
                for (int i = 0; i < middleToRead; i++) {
                    tempFileWrite0.write(mainFile.read());
                }

//                а вторую половину куска записываем в 1й temp
                for (int i = middleToRead + 1; i <= sizeToRead; i++) {
                    tempFileWrite1.write(mainFile.read());
                }

//                перечитываем количество осавшихся байтов для чтения
                sizeToRead = availableSize = mainFile.available();

                System.out.println("[INFO] Total size: " + availableSize);
            }

//            закрываем все файлы
            mainFile.close();
            tempFileWrite0.close();
            tempFileWrite1.close();

//            создаем подзадачи
            MergeSortingFileTask subTask0 = new MergeSortingFileTask();
            MergeSortingFileTask subTask1 = new MergeSortingFileTask();
            subTask0.setMainFileName(tempFileName0);
            subTask1.setMainFileName(tempFileName1);

//            запускаем подзадачи и ждем их выполнения
            invokeAll(subTask0, subTask1);

//           объединяем temp файлы
            merge();

        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] File not found: '" + ApplicationProperties.getInFileName() + "'");
        } catch (IOException e) {
            System.err.println("[ERROR] I\\O error ");
        }
    }
}