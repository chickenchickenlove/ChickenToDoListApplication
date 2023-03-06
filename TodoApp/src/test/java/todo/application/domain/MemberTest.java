package todo.application.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.TestUtilsConstant;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MemberTest {


    @Test
    void createMemberTest(){

        // when
        Member newMember = Member.createNewMember(TestUtilsConstant.MEMBER_NICKNAME, TestUtilsConstant.MEMBER_JOINID,
                TestUtilsConstant.PASSWORD, TestUtilsConstant.EMAIL);

        // then
        assertThat(newMember.getNickname()).isEqualTo(TestUtilsConstant.MEMBER_NICKNAME);
        assertThat(newMember.getJoinId()).isEqualTo(TestUtilsConstant.MEMBER_JOINID);
        assertThat(newMember.getPassword()).isEqualTo(TestUtilsConstant.PASSWORD);
        assertThat(newMember.getEmail()).isEqualTo(TestUtilsConstant.EMAIL);
    }
}