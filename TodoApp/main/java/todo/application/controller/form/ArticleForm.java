package todo.application.controller.form;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@ToString
public class ArticleForm {

    @NotEmpty
    private String writeTitle;
    @NotEmpty
    private String writeContents;
    private LocalDate dueDate;


}
