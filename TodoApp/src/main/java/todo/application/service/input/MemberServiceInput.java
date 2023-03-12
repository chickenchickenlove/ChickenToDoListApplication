package todo.application.service.input;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberServiceInput {

    private String newNickName;
    private String newJoinId;
    private String newPassword;
    private String newEmail;
    private RuntimeException exception;

}
