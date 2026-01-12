package adapter.in.controller.QuarkusProvider;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider // Wichtig! Damit Quarkus die Klasse automatisch findet
public class LocalDateTimeParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(LocalDateTime.class)) {
            return (ParamConverter<T>) new LocalDateTimeConverter();
        }
        return null;
    }

    public static class LocalDateTimeConverter implements ParamConverter<LocalDateTime> {
        @Override
        public LocalDateTime fromString(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            // Unterstützt Standard ISO-Format (z.B. 2026-01-10T20:00:00)
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        @Override
        public String toString(LocalDateTime value) {
            if (value == null) return null;
            return value.toString();
        }
    }
}