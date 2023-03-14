package todo.application.service.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import todo.application.domain.Member;
import todo.application.repository.MemberRepository;
import todo.application.service.input.MemberServiceInput;
import todo.application.service.output.MemberServiceOutput;

import java.util.Objects;

@Slf4j
@Component
public class MemberServiceCore {


    public MemberServiceOutput doCreateMember(MemberServiceInput memberServiceInput) {

        RuntimeException exception = memberServiceInput.getException();
        if (Objects.nonNull(exception)) {
            throw exception;
        }

        String newEmail = memberServiceInput.getNewEmail();
        String newNickName = memberServiceInput.getNewNickName();
        String newPassword = memberServiceInput.getNewPassword();
        String newJoinId = memberServiceInput.getNewJoinId();

        Member newMember = Member.createNewMember(newNickName, newJoinId, newPassword, newEmail);

        return MemberServiceOutput.builder()
                .createdMember(newMember).build();
    }
}
