package com.study.libs.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public class SerializeObject {
    public static class ExtendableBean {
        public String name;
        private Map<String, String> properties;

        @JsonAnyGetter
        public Map<String, String> getProperties() {
            return properties;
        }
    }

    public enum TypeEnumWithValue {
        TYPE1(1, "TypeA"), TYPE2(2, "Type 2");

        private Integer id;
        private String name;

        TypeEnumWithValue(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }
}
