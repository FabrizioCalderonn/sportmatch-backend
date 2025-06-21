// src/main/java/org/ncapas/canchitas/security/config/LocalTimeEsDeserializer.java
package org.ncapas.canchitas.security.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Acepta:
 *   • Formato 24 h sin sufijo: "7:00", "07:00"
 *   • Formato 12 h con sufijo: "7:00 p. m.", "07:00 p.m.", "12 p . m.", "11p.m.", etc.
 */
public class LocalTimeEsDeserializer extends JsonDeserializer<LocalTime> {

    // Patrón 24 h (hora sin o con cero inicial)
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("H:mm");

    // Patrón 12 h sin cero inicial + espacio + AM/PM
    private static final DateTimeFormatter AMPMP_FMT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String raw = p.getText().trim();

        // 1) Si viene en 24 h puro ("7:00" o "07:00"), parseamos directamente
        if (raw.matches("\\d{1,2}:\\d{2}")) {
            return LocalTime.parse(raw, ISO_FMT);
        }

        // 2) Normalizar todas las variantes de "a. m." / "p. m." → "am"/"pm"
        String low = raw.toLowerCase(Locale.ROOT)
                // elimina puntos y espacios redundantes
                .replaceAll("(?i)\\s*a\\s*\\.\\s*m\\s*\\.?", "am")
                .replaceAll("(?i)\\s*p\\s*\\.\\s*m\\s*\\.?", "pm");

        // 3) Asegurar un espacio antes de AM/PM y convertir a mayúsculas
        low = low.replaceAll("(?i)(\\d)(am|pm)$", "$1 $2")
                .toUpperCase(Locale.ENGLISH);

        // 4) Parsear con el patrón de 12 h
        return LocalTime.parse(low, AMPMP_FMT);
    }
}
