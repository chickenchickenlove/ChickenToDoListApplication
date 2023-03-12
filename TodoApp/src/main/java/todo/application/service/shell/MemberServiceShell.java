package todo.application.service.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import todo.application.domain.Member;
import todo.application.repository.MemberRepository;
import todo.application.service.input.MemberServiceInput;
import todo.application.service.output.MemberServiceOutput;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MemberServiceShell {

    private final MemberRepository memberRepository;

    public MemberServiceInput readyForSaveMember(String nickname, String joinId, String password, String email) {

        Member memberByJoinId = memberRepository.findMemberByJoinId(joinId);

        MemberServiceInput.MemberServiceInputBuilder builder = MemberServiceInput.builder();
        if (Objects.nonNull(memberByJoinId)) {
            builder.exception(new IllegalStateException("같은 ID로 이미 회원이 존재합니다."));
        }

        return builder
                .newNickName(nickname)
                .newJoinId(joinId)
                .newPassword(password)
                .newEmail(email).build();
    }

    public Long wrapUpAfterSaveMember(MemberServiceOutput memberServiceOutput) {
        Member createdMember = memberServiceOutput.getCreatedMember();
        return memberRepository.saveMember(createdMember);
    }


}
