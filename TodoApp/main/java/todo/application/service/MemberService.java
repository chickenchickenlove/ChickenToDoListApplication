package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long saveMember(String nickname, String joinId, String password, String email) {
        Member newMember = Member.createNewMember(nickname, joinId, password, email, LocalDateTime.now());
        Long memberId = memberRepository.saveMember(newMember);
        return memberId;
    }

    public List<MemberArticle> findArticleListByMemberId(Long memberId) {
        return memberRepository.findMemberArticleByMemberId(memberId);
    }





}
