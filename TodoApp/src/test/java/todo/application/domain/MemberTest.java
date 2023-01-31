package todo.application.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class MemberTest {


    @Test
    @Rollback(value = false)
    void test() throws Exception{

        // given + when
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");


        log.info("member = {}", newMember.toString());

        // then
        Assertions.assertThat(newMember.getNickname()).isEqualTo("abc");
        Assertions.assertThat(newMember.getJoinId()).isEqualTo("abcd");
        Assertions.assertThat(newMember.getPassword()).isEqualTo("abcde");
        Assertions.assertThat(newMember.getEmail()).isEqualTo("abcde@naver.com");
    }







}