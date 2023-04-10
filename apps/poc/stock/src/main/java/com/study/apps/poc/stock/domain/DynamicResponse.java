package com.study.apps.poc.stock.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DynamicResponse {
    @Getter
    @JsonAnySetter
    private final Map<String, Object> resultMap = new LinkedHashMap<>();

    @JsonIgnore
    public static DynamicResponse of(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, DynamicResponse.class);
    }
}
