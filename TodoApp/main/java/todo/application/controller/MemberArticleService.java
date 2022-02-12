package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberArticleRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberArticleService {

    private final MemberArticleRepository memberArticleRepository;


    public MemberArticle findMemberArticleByMemberIdAndArticleId(Long memberId, Long articleId) {
        return memberArticleRepository.findMemberArticleByMemberIdArticleId(memberId, articleId);
    }


    public Slice<MemberArticle> findPagingArticleByMemberIdNotCompleted(Long memberId, Pageable pageable) {
        return memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberId, pageable);
    }

    public Slice<MemberArticle> findPagingArticleByMemberIdCompletedOnly(Long memberId, Pageable pageable) {
        return memberArticleRepository.findSliceArticleByMemberIdCompleted(memberId, pageable);
    }









}
