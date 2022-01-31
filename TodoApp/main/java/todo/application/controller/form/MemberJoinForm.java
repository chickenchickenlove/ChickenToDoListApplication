package todo.application.controller.form;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@ToString
public class MemberJoinForm {

    private String nickname;
    private String joinId;
    private String password;
    private String passwordRepeat;
    private String email;

}
