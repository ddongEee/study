package com.study.apps.poc.stock.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilePathEnvelop {
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private final FilePath filePath;
    private final LocalDateTime additionalLocalDateTime;

    public static FilePathEnvelop create(final FilePath filePath) {
        return builder()
                .filePath(filePath)
                .additionalLocalDateTime(LocalDateTime.now())
                .build();
    }

    public static FilePathEnvelop create(final FilePath filePath, String localDateTime) {
        return builder()
                .filePath(filePath)
                .additionalLocalDateTime(LocalDateTime.parse(localDateTime, DATETIME_FORMAT))
                .build();
    }

    public static FilePathEnvelop fromFullPath(String fullPath) {
        FilePath filePath = FilePath.findByNameContains(fullPath);
        String[] splitFullPath = fullPath.replaceAll(filePath.extension(), "").split("_");
        String datetime = splitFullPath[splitFullPath.length - 1];
        return builder()
                .filePath(filePath)
                .additionalLocalDateTime(LocalDateTime.parse(datetime, DATETIME_FORMAT))
                .build();
    }

    public boolean equalCategory(final FilePath filePath) {
        return this.filePath == filePath;
    }

    public FilePathEnvelop compareAndGetLatestOne(final FilePathEnvelop compEnvelop) {
        if (this.filePath != compEnvelop.filePath) {
            throw new RuntimeException("서로 파일 형식이 다릅니다..");
        }

        return this.additionalLocalDateTime.isAfter(compEnvelop.additionalLocalDateTime) ? this : compEnvelop;
    }

    public Path path() {
        String additional = this.additionalLocalDateTime.format(DATETIME_FORMAT);
        return filePath.generatePathWithAdditional(additional);
    }

    public String textPath() {
        String additional = this.additionalLocalDateTime.format(DATETIME_FORMAT);
        return this.filePath.generateTextPathWithAdditional(additional);
    }
}
