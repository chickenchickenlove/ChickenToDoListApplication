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
import todo.application.service.core.MemberServiceCore;
import todo.application.service.input.MemberServiceInput;
import todo.application.service.output.MemberServiceOutput;
import todo.application.service.shell.MemberServiceShell;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityManager em;
    private final MemberServiceShell memberServiceShell;
    private final MemberServiceCore memberServiceCore;

    public MemberService(MemberRepository memberRepository, EntityManager em, MemberServiceShell memberServiceShell, MemberServiceCore memberServiceCore) {
        this.memberRepository = memberRepository;
        this.em = em;
        this.memberServiceShell = memberServiceShell;
        this.memberServiceCore = memberServiceCore;
    }

    //== 회원 저장==//
    public Long saveMember(String nickname, String joinId, String password, String email) {
        MemberServiceInput memberServiceInput = memberServiceShell.readyForSaveMember(nickname, joinId, password, email);
        MemberServiceOutput memberServiceOutput = memberServiceCore.doCreateMember(memberServiceInput);
        return memberServiceShell.wrapUpAfterSaveMember(memberServiceOutput);
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
