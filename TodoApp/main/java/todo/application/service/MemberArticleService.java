package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberArticleRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberArticleService {

    private final MemberArticleRepository memberArticleRepository;


    // 단건 조회 //

    public MemberArticle findMemberArticleByMemberIdAndArticleId(Long memberId, Long articleId) {
        return memberArticleRepository.findMemberArticleByMemberIdArticleId(memberId, articleId);
    }


    // 리스트 조회 //

    public List<MemberArticle> findArticleListByMemberId(Long memberId) {
        return memberArticleRepository.findMemberArticleByMemberId(memberId);
    }


    // 슬라이싱 조회 //
    public Slice<MemberArticle> findPagingArticleByMemberIdNotCompleted(Long memberId, Pageable pageable) {
        return memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberId, pageable);
    }

    public Slice<MemberArticle> findPagingArticleByMemberIdCompletedOnly(Long memberId, Pageable pageable) {
        return memberArticleRepository.findSliceArticleByMemberIdCompleted(memberId, pageable);
    }









}
