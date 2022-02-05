package todo.application.controller.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class LocalDateTimeToStringConverter {

    @Test

    void test1() {
        String abc = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd,MM,yyyy"));
        System.out.println("abc = " + abc);
        StringBuilder sb = new StringBuilder();






    }





}