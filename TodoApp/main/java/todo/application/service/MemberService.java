package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;
import todo.application.repository.MemberSearch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberServiceThreadFree memberServiceThreadFree;


    //== 비즈니스 로직==//

    // ID 찾기
    public Member findJoinIdByEmail(String email) {
        if (StringUtils.hasText(email)) {
            return memberRepository.findMemberByEmail(email);
        }
        return null;
    }


    // 비밀번호 찾기
    public Member findPassword(String email, String joinID) {

        if (StringUtils.hasText(email) && StringUtils.hasText(joinID)) {
            return memberRepository.findMemberByEmail(email);
        }
        return null;
    }





    //== 회원 저장==//

    @Transactional
    public Long saveMember(String nickname, String joinId, String password, String email) {


        // 동시성 문제 해결 필요.
        // 성공로직
        Member newMember = Member.createNewMember(nickname, joinId, password, email);
        Long memberId = memberRepository.saveMember(newMember);

        return memberId;
    }


    //== 회원 조회==//

    public List<Member> findMemberByJoinId(String joinId) {
        return memberRepository.findMemberByJoinId(joinId);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findMemberById(id);
    }


    public Member findMemberByJoinIdOne(String joinId) {
        return memberRepository.findMemberByJoinIdOneMan(joinId);
    }

    public List<Member> findMemberByMemberSearch(MemberSearch memberSearch) {
        return memberRepository.findMemberByMemberSearch(memberSearch);
    }


    public List<MemberArticle> findArticleListByMemberId(Long memberId) {
        return memberRepository.findMemberArticleByMemberId(memberId);
    }


    //== Validation 로직==//



    public int isNicknamePossible(String nickname) {
        /*
        TODO : 동시성 문제 해결 필요
         */
        List<Member> members = memberRepository.findMemberByNickname(nickname);
        return members.size();
    }


    public int isEmailPossible(String email) {
        /*
        TODO : 동시성 문제 해결 필요
         */
        Member memberByEmail = memberRepository.findMemberByEmail(email);
        return memberByEmail == null ? 0 : 1;
    }

    public int isJoinIdPossible(String joinId) {
        /*
        TODO : 동시성 문제 해결 필요
         */
        List<Member> members = memberRepository.findMemberByJoinId(joinId);
        return members.size();
    }






}
