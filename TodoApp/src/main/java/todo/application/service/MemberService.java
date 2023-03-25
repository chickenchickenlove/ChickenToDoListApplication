package todo.application.service;

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
import todo.application.service.input.MemberServiceInput;
import todo.application.service.output.MemberServiceOutput;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //== 회원 저장==//
    public Long saveMember(String nickname, String joinId, String password, String email) {

        Member findMember = memberRepository.findMemberByJoinId(joinId);
        if (findMember.cannotJoinMember()) {
            throw new IllegalStateException("같은 UID로 이미 회원이 존재합니다.");
        }

        Member newMember = Member.createNewMember(nickname, joinId, password, email);
        memberRepository.saveMember(newMember);

        return newMember.getId();
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
    public Member findMemberByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException();
        }

        return memberRepository.findMemberByEmail(email);
    }

    public String findPassword(String email, String joinID) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(joinID)) {
            return null;
        }

        return memberRepository.findMemberByEmail(email).getPassword();
    }


    public List<MemberArticle> findArticleListByMemberId(Long id) {
        return null;
    }
}
