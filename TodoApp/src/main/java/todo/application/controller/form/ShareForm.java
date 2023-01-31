package todo.application.controller.form;

import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

@Data
public class ShareForm {

    private Long memberId;
    private Long articleId;
}
