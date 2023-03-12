package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.TestUtils;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;
import todo.application.service.core.MemberServiceCore;
import todo.application.service.input.MemberServiceInput;
import todo.application.service.output.MemberServiceOutput;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberServiceUnitTest {

    MemberServiceCore memberServiceCore;

    @BeforeEach
    void initInstance() {
        memberServiceCore = new MemberServiceCore();
    }

    @Test
    void createNewMemberSuccessTest() {

        // given
        MemberJoinForm memberJoinForm = TestUtils.createNewMemberJoinForm();
        MemberServiceInput memberServiceInput = MemberServiceInput.builder()
                .newPassword(memberJoinForm.getPassword())
                .newNickName(memberJoinForm.getNickname())
                .newEmail(memberJoinForm.getEmail())
                .newJoinId(memberJoinForm.getJoinId())
                .build();

        // when
        MemberServiceOutput memberServiceOutput = memberServiceCore.doCreateMember(memberServiceInput);

        // then
        Member createdMember = memberServiceOutput.getCreatedMember();
        assertThat(createdMember.getPassword()).isEqualTo(memberServiceInput.getNewPassword());
        assertThat(createdMember.getEmail()).isEqualTo(memberServiceInput.getNewEmail());
        assertThat(createdMember.getJoinId()).isEqualTo(memberServiceInput.getNewJoinId());
        assertThat(createdMember.getNickname()).isEqualTo(memberServiceInput.getNewNickName());
    }

    @Test
    void createNewMemberFailTest() {

        // given
        MemberJoinForm memberJoinForm = TestUtils.createNewMemberJoinForm();
        MemberServiceInput memberServiceInput = MemberServiceInput.builder()
                .newPassword(memberJoinForm.getPassword())
                .exception(new IllegalStateException())
                .newNickName(memberJoinForm.getNickname())
                .newEmail(memberJoinForm.getEmail())
                .newJoinId(memberJoinForm.getJoinId())
                .build();

        // when + then
        assertThatThrownBy(() -> memberServiceCore.doCreateMember(memberServiceInput))
                .isInstanceOf(memberServiceInput.getException().getClass());
    }

}