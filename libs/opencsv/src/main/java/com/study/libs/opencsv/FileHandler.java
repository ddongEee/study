package com.study.libs.opencsv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String TEMP_FILE_DIRECTORY = System.getProperty("user.home") + "/Projects/personal/study/temp-files";

    public static void appendOnFileInTempDir(String fileName, List<String> rows) throws IOException {
        appendOnFile(TEMP_FILE_DIRECTORY + "/" + fileName, rows);
    }

    public static void overwriteOnFileInTempDir(String fileName, List<String> rows) throws IOException {
        overwriteOnFile(TEMP_FILE_DIRECTORY + "/" + fileName, rows);
    }

    public static void appendOnFile(String filePath, List<String> rows) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        for (String row : rows) {
            writer.append("\n")
                  .append(row);
        }
        writer.close();
    }

    public static void overwriteOnFile(String filePath, List<String> rows) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (String row : rows) {
            printWriter.println(row);
        }
        printWriter.close();
    }
}
