package com.study.apps.poc.stock.domain.page38coms.util;

import com.study.apps.poc.stock.infrastructure.FilePath;
import com.study.apps.poc.stock.infrastructure.FilePathEnvelop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class StockFileManager {
    public void removeFile(final FilePathEnvelop envelop) {
        File myObj = new File(envelop.textPath());
        if (myObj.delete()) {
            log.info("@@@ Deleted the file: {}", myObj.getName());
        } else {
            log.error("@@@ Failed to delete the file.");
        }
    }

    public void printCompareTwoFile(final FilePathEnvelop mainEnvelop, final FilePathEnvelop compEnvelop) throws IOException {
        List<String> main = loadLines(mainEnvelop);
        List<String> comp = loadLines(compEnvelop);
        List<String> plus = new ArrayList<>();

        for (String row : main) {
            if (comp.contains(row)) {
                comp.remove(row);
                continue; // 변경없음
            }
            plus.add(row);
        }

        List<String> minus = new ArrayList<>(comp);

        StringBuilder builder = new StringBuilder();
        builder.append("Results\n");
        builder.append("plus : ").append(plus.size()).append("\n");
        builder.append("minus : ").append(minus.size()).append("\n");
        builder.append("PLUS").append("\n");
        plus.forEach(p -> builder.append(p).append("\n"));
        builder.append("MINUS").append("\n");
        minus.forEach(m -> builder.append(m).append("\n"));
        log.info(builder.toString());
    }

    private List<String> loadLines(final FilePathEnvelop envelop) throws IOException {
        try (Stream<String> lines = Files.lines(envelop.path())) {
            return lines.collect(Collectors.toList());
        }
    }

    public List<String> loadLatestLines(final FilePath path) throws IOException {
        FilePathEnvelop envelop = latestEnvelop(path)
                .orElseThrow(() -> new RuntimeException("최근파일 존재안함 : " + path));
        try (Stream<String> lines = Files.lines(envelop.path())) {
            return lines.collect(Collectors.toList());
        }
    }

    public void write(final FilePathEnvelop envelop, List<String> contents) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(envelop.path(), StandardOpenOption.CREATE)) {
            contents.forEach(s -> {
                try {
                    bw.write(s);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        log.info("@@@ Done to write the file: {}", envelop.textPath());
    }

    public List<FilePathEnvelop> listEnvelop() {
        File folder = new File(FilePath.ROOT_FILE_PATH);
        File[] listOfFiles = folder.listFiles();
        List<FilePathEnvelop> results = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                results.add(FilePathEnvelop.fromFullPath(listOfFiles[i].getName()));
            }
        }
        return results;
    }

    public Optional<FilePathEnvelop> latestEnvelop(final FilePath target) {
        FilePathEnvelop latestFilePathEnvelop = null;
        for (FilePathEnvelop foundEnvelop : listEnvelop()) {
            if (foundEnvelop.equalCategory(target)) {
                if (latestFilePathEnvelop == null) {
                    latestFilePathEnvelop = foundEnvelop;
                }
                latestFilePathEnvelop = latestFilePathEnvelop.compareAndGetLatestOne(foundEnvelop);
            }
        }
        if (latestFilePathEnvelop == null) {
            return Optional.empty();
        }

        return Optional.of(latestFilePathEnvelop);
    }
}
