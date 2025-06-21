// src/main/java/org/ncapas/canchitas/security/config/LocalTimeEsDeserializer.java
package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeEsDeserializer extends JsonDeserializer<LocalTime> {

    // 24h sin sufijo
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    // tu patrón de 12h con AM/PM (usamos Locale.ENGLISH para AM/PM)
    private static final DateTimeFormatter AMPMP_FMT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        String raw = p.getText().trim();

        // 1) Si es puro "HH:mm", lo parseamos directamente
        if (raw.matches("\\d{1,2}:\\d{2}")) {
            return LocalTime.parse(raw, ISO_FMT);
        }

        // 2) Sino, lo normalizamos para AM/PM en español
        String low = raw.toLowerCase(Locale.ROOT)
                .replace("a. m.", "am")
                .replace("p. m.", "pm")
                .replace("a.m.",  "am")
                .replace("p.m.",  "pm")
                .replace(" a m",  "am")
                .replace(" p m",  "pm");

        String suf;
        if (low.endsWith("am")) {
            suf = " AM";
            low = low.substring(0, low.length()-2).trim();
        } else if (low.endsWith("pm")) {
            suf = " PM";
            low = low.substring(0, low.length()-2).trim();
        } else {
            // no reconoce el sufijo: error de parse
            throw new IOException("Formato de hora no soportado: " + raw);
        }

        String normalized = low + suf;    // ej. "07:00 PM"
        return LocalTime.parse(normalized, AMPMP_FMT);
    }
}
