package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeEsSerializer extends JsonSerializer<LocalTime> {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);   // ← ENGLISH

    @Override
    public void serialize(LocalTime value, JsonGenerator gen,
                          SerializerProvider sp) throws IOException {

        String out = value.format(FMT)               // "07:00 AM"
                .replace("AM", "a. m.")
                .replace("PM", "p. m.");
        gen.writeString(out);
    }
}
