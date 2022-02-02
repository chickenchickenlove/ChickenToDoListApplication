package todo.application.controller.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberArticleForm {

    private Long memberId;
    private String nickname;
    private String joinId;

    public MemberArticleForm(Long memberId, String nickname, String joinId) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinId = joinId;
    }
}
