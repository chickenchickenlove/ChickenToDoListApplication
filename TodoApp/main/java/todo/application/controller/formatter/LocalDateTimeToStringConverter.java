package todo.application.controller.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {

    @Override
    public String convert(LocalDateTime source) {
        log.info("localDateTImeConverter Work");
        return source.format(DateTimeFormatter.ofPattern("dd,MM,yyyy"));
    }
}
