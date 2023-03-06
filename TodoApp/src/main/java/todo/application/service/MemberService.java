package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;
import todo.application.repository.MemberSearch;
import todo.application.repository.dto.MemberAdminDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityManager em;

    //== 회원 저장==//
    public Long saveMember(String nickname, String joinId, String password, String email) {

        Member newMember = Member.createNewMember(nickname, joinId, password, email);
        Long memberId;

        try {
            memberId = memberRepository.saveMember(newMember);
            em.flush();
        } catch (PersistenceException e) {
            log.info("유니크 제약 조건으로 인해 문제가 발생했습니다.");
            return null;
        }

        return memberId;
    }

    //== Batch 연산==//
    public void batchRemove(String memberId) {

        String[] split = memberId.split("-");
        List<Long> memberIds = Arrays.stream(split).map(s -> Long.valueOf(s)).collect(Collectors.toList());
        memberRepository.batchRemoveMember(memberIds);

    }

    //== 회원 단건 조회==//
    public Member findMemberByJoinId(String joinId) {
        return memberRepository.findMemberByJoinId(joinId);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findMemberById(id);
    }

    public Member findMemberByJoinIdOne(String joinId) {
        return memberRepository.findMemberByJoinIdOneMan(joinId);
    }

    //== 슬라이스 조회==//
    public Slice<Member> findMemberByMemberSearch(MemberSearch memberSearch, Pageable pageable) {
        return memberRepository.findMemberByMemberSearch(memberSearch, pageable);
    }

    //== DTO 조회==//
    public List<MemberAdminDto> findAllMemberAdminDto() {
        return memberRepository.findMemberAdminDto();
    }


    //== 비즈니스 로직==//
    public Member findJoinIdByEmail(String email) {
        if (StringUtils.hasText(email)) {
            return memberRepository.findMemberByEmail(email);
        }
        return null;
    }

    public Member findPassword(String email, String joinID) {

        if (StringUtils.hasText(email) && StringUtils.hasText(joinID)) {
            return memberRepository.findMemberByEmail(email);
        }
        return null;
    }


    public List<MemberArticle> findArticleListByMemberId(Long id) {
        return null;
    }
}
