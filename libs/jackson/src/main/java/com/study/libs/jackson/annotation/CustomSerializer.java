package com.study.libs.jackson.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomSerializer {
    public static class EventWithSerializer {
        public String name;

        @JsonSerialize(using = CustomDateSerializer.class)
        public Date eventDate;

        public EventWithSerializer(String name, Date eventDate) {
            this.name = name;
            this.eventDate = eventDate;
        }
    }

    public static class CustomDateSerializer extends StdSerializer<Date> {

        private static final SimpleDateFormat formatter
                = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        public CustomDateSerializer() {
            this(null);
        }

        public CustomDateSerializer(Class<Date> t) {
            super(t);
        }

        @Override
        public void serialize(
                Date value, JsonGenerator gen, SerializerProvider arg2)
                throws IOException {
            gen.writeString(formatter.format(value));
        }
    }
}
