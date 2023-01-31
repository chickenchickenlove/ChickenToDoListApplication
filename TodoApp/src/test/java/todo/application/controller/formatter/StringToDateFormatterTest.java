package todo.application.controller.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


class StringToDateFormatterTest {

    @Test

    void test1() {
        StringToDateFormatter formatter = new StringToDateFormatter();
        LocalDate convert = formatter.convert("2011-01-22");
        System.out.println("convert = " + convert);
        Assertions.assertThat(convert).isInstanceOf(LocalDate.class);
    }





}