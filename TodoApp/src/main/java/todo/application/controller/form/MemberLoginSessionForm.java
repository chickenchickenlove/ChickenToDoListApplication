package todo.application.controller.form;

import lombok.Data;
import todo.application.domain.ArticleStatus;
import todo.application.domain.MemberGrade;

@Data
public class MemberLoginSessionForm {

    private Long memberId;
    private String nickname;
    private String joinId;
    private MemberGrade memberGrade;

    public MemberLoginSessionForm(Long memberId, String nickname, String joinId, MemberGrade memberGrade) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinId = joinId;
        this.memberGrade = memberGrade;
    }
}
