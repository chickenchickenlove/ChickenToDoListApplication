package todo.application.service.output;

import lombok.Builder;
import lombok.Getter;
import todo.application.domain.Member;

@Builder
@Getter
public class MemberServiceOutput {

    private Member createdMember;

}
