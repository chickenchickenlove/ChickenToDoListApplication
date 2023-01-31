package todo.application.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginForgetPasswordForm {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String joinId;

    private String password;

}
