package todo.application.controller.form;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Data
@ToString
public class MemberJoinForm {

    @NotEmpty
    private String nickname;
    @NotEmpty
    private String joinId;


    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordRepeat;

    @NotEmpty
    @Email
    private String email;

}
