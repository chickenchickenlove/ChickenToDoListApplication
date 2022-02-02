package todo.application.controller.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class StringToDateFormatter implements Converter<String, LocalDate> {


    @Override
    public LocalDate convert(String source) {

        log.info("source = {}", source);

        String[] split = source.split("-", 3);

        StringBuilder sb = new StringBuilder();

        sb.append(split[0]);
        sb.append("-");

        if (split[1].length() == 1) {
            sb.append("0");
        }
        sb.append(split[1]);
        sb.append("-");

        if (split[2].length() == 1) {
            sb.append("0");
        }

        sb.append(split[2]);


        String transString = sb.toString();

        log.info("transString = {}", transString);

        LocalDate result = LocalDate.parse(transString, DateTimeFormatter.ISO_DATE);

        return result;
    }
}
