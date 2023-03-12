package todo.application.controller.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Data
@ToString
@NoArgsConstructor
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

    public static MemberJoinForm createMemberJoinForm(String nickname, String joinId, String password, String email) {
        return new MemberJoinForm(nickname, joinId, password, email);

    }

    private MemberJoinForm(String nickname, String joinId, String password, String email) {
        this.nickname = nickname;
        this.joinId = joinId;
        this.password = password;
        this.email = email;
    }


}
