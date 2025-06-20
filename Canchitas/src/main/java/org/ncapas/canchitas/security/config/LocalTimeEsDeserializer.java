package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeEsDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);   // ← ENGLISH

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {

        String raw = p.getText().trim().toLowerCase(Locale.ROOT);

        /* 1 — quitar puntos/espacios y variantes */
        raw = raw.replace("a. m.", "am")
                .replace("p. m.", "pm")
                .replace("a.m.",  "am")
                .replace("p.m.",  "pm")
                .replace(" a m",  "am")
                .replace(" p m",  "pm");

        /* 2 — insertar espacio y mayúsculas   →  "07:00 AM" / "07:00 PM" */
        if (raw.endsWith("am"))
            raw = raw.substring(0, raw.length()-2).trim() + " AM";
        else if (raw.endsWith("pm"))
            raw = raw.substring(0, raw.length()-2).trim() + " PM";

        return LocalTime.parse(raw, FMT);
    }
}
