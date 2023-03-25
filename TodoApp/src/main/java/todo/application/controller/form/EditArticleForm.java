package todo.application.controller.form;

import lombok.Data;
import todo.application.domain.Article;
import todo.application.domain.ArticleStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Data
public class EditArticleForm {

    private LocalDate dueDate;
    private String writeTitle;
    private String writeContents;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    public EditArticleForm() {
    }

    public EditArticleForm(LocalDate dueDate, String writeTitle, String writeContents, ArticleStatus status) {
        this.dueDate = dueDate;
        this.writeTitle = writeTitle;
        this.writeContents = writeContents;
        this.status = status;
    }

    public void setArticleForm(Article article){
        writeTitle = article.getWriteTitle();
        writeContents = article.getWriteContents();
        dueDate = article.getDueDate();
        // duedate를 Type Converting해서 넣어줘야한다.

    }

}
