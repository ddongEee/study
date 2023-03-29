package com.study.libs.opencsv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.study.libs.opencsv.strategy.AnnotationStrategy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CsvHandler {
    public static void initFile(Path filePath) {
        try {
            Files.delete(filePath);
        } catch(NoSuchFileException e) {
            System.out.println("이미 파일 삭제됨 : " + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readFrom(Path path, Class clazz) throws Exception {
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<T> cb = new CsvToBeanBuilder<T>(reader)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withType(clazz)
                    .build();
            return cb.parse();
        }
    }

    public static <T> void writeTo(Path path, List<T> sampleData) throws Exception {
        try (Writer writer  = new FileWriter(path.toString())) {
            AnnotationStrategy annotationStrategy = new AnnotationStrategy(sampleData.iterator().next().getClass());
            StatefulBeanToCsv<T> sbc = new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withMappingStrategy(annotationStrategy)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();
            sbc.write(sampleData);
        }
    }
}
