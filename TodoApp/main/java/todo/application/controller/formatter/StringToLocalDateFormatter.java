package todo.application.controller.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class StringToLocalDateFormatter implements Converter<String, LocalDate> {


    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
