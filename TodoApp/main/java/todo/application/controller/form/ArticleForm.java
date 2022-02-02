package todo.application.controller.form;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@ToString
public class ArticleForm {

    private String writeTitle;
    private String writeContents;
    private LocalDate dueDate;


}
