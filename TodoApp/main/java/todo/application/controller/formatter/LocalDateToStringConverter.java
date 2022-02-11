package todo.application.controller.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateToStringConverter implements Converter<LocalDate, String> {

    @Override
    public String convert(LocalDate source) {
        log.info("=============LocalDateTime To String Converter ===================");
        return source.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
