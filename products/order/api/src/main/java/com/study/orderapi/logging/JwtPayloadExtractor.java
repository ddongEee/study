package com.study.orderapi.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class JwtPayloadExtractor {
    private static final int PAYLOAD_INDEX_1 = 1;
    private final static ObjectMapper mapper = new ObjectMapper();

    public static Payload extract(Optional<String> jwtFromRequest) throws JsonProcessingException {
        if (jwtFromRequest.isEmpty()) {
            return Payload.empty();
        }
        String[] contents = jwtFromRequest.get().split("\\.");
        byte[] bytes = Base64.getUrlDecoder().decode(contents[PAYLOAD_INDEX_1]);
        String decodedPayload = new String(bytes, StandardCharsets.UTF_8);
        JsonNode jsonNode = mapper.readTree(decodedPayload);
        String name = jsonNode.get("name").asText();
        return new Payload(name);
    }

    public static final class Payload {
        private final String name;

        public Payload(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Payload empty() {
            return new Payload("unknown");
        }
    }
}
