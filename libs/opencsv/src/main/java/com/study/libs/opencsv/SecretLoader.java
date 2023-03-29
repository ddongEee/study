package com.study.libs.opencsv;

import com.opencsv.bean.CsvBindByName;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecretLoader {
    private static final String SECRET_FILE_PATH = "~/.secret/source.csv";

    public static String findByKey(final SourceKey key) throws Exception {
        Map<String, String> sourceMap = loadSource();
        return sourceMap.get(key.key);
    }

    private static Map<String, String> loadSource() throws Exception {
        Path path = Paths.get(SECRET_FILE_PATH);
        List<Source> sources = CsvHandler.readFrom(path, Source.class);
        return sources.stream()
                .collect(Collectors.toMap(source -> source.key, source -> source.value));
    }

    public static final class Source {
        @CsvBindByName(column = "key") public String key;
        @CsvBindByName(column = "value") public String value;

        public Source() {}
    }

    public enum SourceKey {
        DATA_GO_KR_API_KEY("data.go.kr.api.key"),
        PATH_USER_HOME("path.user.home")
        ;

        private String key;

        SourceKey(String key) {
            this.key = key;
        }
    }
}
