// src/main/java/org/ncapas/canchitas/config/LocalTimeEsSerializer.java
package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Devuelve «07:00 p. m.» / «11:00 a. m.» en las respuestas JSON */
public class LocalTimeEsSerializer extends JsonSerializer<LocalTime> {

    /* 07:00 PM  (ENGLISH => AM/PM en mayúsculas) */
    private static final DateTimeFormatter BASE =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    @Override
    public void serialize(LocalTime value, JsonGenerator gen,
                          SerializerProvider sp) throws IOException {

        String out = value.format(BASE)             // 07:00 PM
                .replace("AM", "a. m.")
                .replace("PM", "p. m.");
        gen.writeString(out);
    }
}
