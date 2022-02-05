package todo.application.controller.form;

import lombok.Data;
import todo.application.domain.ArticleStatus;

import javax.persistence.Version;

@Data
public class MemberArticleForm {

    private Long memberId;
    private String nickname;
    private String joinId;
    private ArticleStatus status;





    public MemberArticleForm(Long memberId, String nickname, String joinId) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinId = joinId;
        this.status = ArticleStatus.ING;
    }
}
