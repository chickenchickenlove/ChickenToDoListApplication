package todo.application.controller.form;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class LoginForm {

    @NotEmpty
    private String joinId;
    @NotEmpty
    private String password;


}
