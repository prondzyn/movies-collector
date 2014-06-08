package com.prondzyn.movies.collector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Arguments required: PATH_TO_DIRECTORY_TO_CHECK PATH_TO_OUTPUT_FILE");
            return;
        }
        List<FileInfo> fileInfos = getFileInfos(args[0]);
        String json = new Gson().toJson(fileInfos);
        saveAsFile(args[1], json);
    }

    private static List<FileInfo> getFileInfos(String filepath) {
        File file = new File(filepath);
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        if (file.exists()) {
            collectFileInfos(fileInfos, file);
        }
        return fileInfos;
    }

    private static void collectFileInfos(List<FileInfo> fileInfos, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subfile : files) {
                collectFileInfos(fileInfos, subfile);
            }
        } else {
            fileInfos.add(new FileInfo(file));
        }
    }

    private static void saveAsFile(String filepath, String jsonString) {
        BufferedWriter bufferedWriter = null;
        try {

            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonString);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                //ignore
            }
        }
    }

    private static class FileInfo implements Serializable {

        private static final int MIN_COUNT_OF_VALUES_AFTER_SPLIT = 4;

        @SuppressWarnings("unused")
        private String name;
        @SuppressWarnings("unused")
        private String path;
        @SuppressWarnings("unused")
        private String size;
        @SuppressWarnings("unused")
        private String lastModified;
        @SuppressWarnings("unused")
        private String title;
        @SuppressWarnings("unused")
        private String originalTitle;
        @SuppressWarnings("unused")
        private String year;
        @SuppressWarnings("unused")
        private String format;
        @SuppressWarnings("unused")
        private String meta;

        public FileInfo(File file) {
            this.name = file.getName();
            this.path = file.getParent();
            this.size = prepareSizeInMB(file.length());
            this.lastModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));
            this.title = prepareTitle(file.getName());
            this.originalTitle = prepareOriginalTitle(file.getName());
            this.year = prepareYear(file.getName());
            this.meta = prepareMeta(file.getName());
            this.format = prepareFormat(file.getName());
        }

        private static String prepareSizeInMB(long sizeInBytes) {
            BigDecimal divisor = new BigDecimal(1024);
            divisor.setScale(16, BigDecimal.ROUND_HALF_UP);
            BigDecimal sizeInB = new BigDecimal(sizeInBytes);
            sizeInB.setScale(16, BigDecimal.ROUND_HALF_UP);
            BigDecimal sizeInKB = sizeInB.divide(divisor, 16, BigDecimal.ROUND_HALF_UP);
            BigDecimal sizeInMB = sizeInKB.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
            return sizeInMB.toString();
        }

        private static String prepareTitle(String filename) {
            return getSplittedValue(filename, 0);
        }

        private static String prepareOriginalTitle(String filename) {
            return getSplittedValue(filename, 1);
        }

        private static String prepareYear(String filename) {
            return getSplittedValue(filename, 2);
        }

        private static String prepareMeta(String filename) {
            String value = getSplittedValue(filename, 3);
            if (isBlank(value)) {
                return null;
            }
            int lastSpaceIndex = value.lastIndexOf(" ");
            if (lastSpaceIndex == -1) {
                return null;
            }
            String result = value.substring(0, lastSpaceIndex);
            return isBlank(result) ? null : result;
        }

        private static String prepareFormat(String filename) {
            int lastDotIndex = filename.lastIndexOf(".");
            if (lastDotIndex == -1) {
                return null;
            }
            try {
                return filename.substring(lastDotIndex + 1).toUpperCase();
            } catch (Exception ex) {
                return null;
            }
        }

        private static String getSplittedValue(String base, int index) {
            String[] splitted = splitByUnderscore(base);
            String value = null;
            if (splitted.length >= MIN_COUNT_OF_VALUES_AFTER_SPLIT) {
                value = replaceDots(splitted[index]);
            }
            return value;
        }

        private static String[] splitByUnderscore(String filename) {
            return filename.split("_");
        }

        private static String replaceDots(String value) {
            return value.replace(".", " ");
        }

        private static boolean isBlank(String value) {
            return value == null || "".equals(value.trim());
        }
    }
}
