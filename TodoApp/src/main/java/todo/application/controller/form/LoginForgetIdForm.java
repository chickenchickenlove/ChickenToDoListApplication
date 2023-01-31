package todo.application.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginForgetIdForm {

    @NotEmpty
    @Email
    private String email;
    private String joinId;

}
