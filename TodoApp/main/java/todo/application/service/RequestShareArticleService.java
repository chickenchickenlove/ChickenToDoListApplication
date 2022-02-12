package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.domain.RequestShareArticle;
import todo.application.repository.MemberRepository;
import todo.application.repository.RequestShareArticleRepository;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RequestShareArticleService {

    private final RequestShareArticleRepository requestShareArticleRepository;
    private final MemberRepository memberRepository;
    private final MemberArticleService memberArticleService;


    // 조회 기능

    public RequestShareArticle findRequestShareArticleById(Long requestShareArticleId) {
        return requestShareArticleRepository.findRequestShareArticle(requestShareArticleId);
    }


    public Slice<RequestShareArticle> findSliceRequestShareArticle(Long memberId, Pageable pageable) {
        return requestShareArticleRepository.findSliceRequestShareArticle(memberId, pageable);
    }

    // 요청이 오면 만들어진다.
    @Transactional
    public Long saveRequestShareArticle(Long fromMemberId, Long toMemberId, Long articleId) {

        MemberArticle memberArticle = memberArticleService.findMemberArticleByMemberIdAndArticleId(fromMemberId, articleId);
        Member toMember = memberRepository.findMemberById(toMemberId);
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(toMember, memberArticle.getMember(), memberArticle.getArticle());
        return requestShareArticleRepository.saveRequestShareArticle(requestShareArticle);
    }

    @Transactional
    public void removeRequestShareArticle(Long requestShareArticleId) {
        RequestShareArticle requestShareArticle = requestShareArticleRepository.findRequestShareArticle(requestShareArticleId);
        requestShareArticleRepository.removeRequestShareArticle(requestShareArticle);
    }



    //== Validation==//
    public boolean isDuplicated(Long toMemberId, Long articleId) {
        RequestShareArticle findResult = requestShareArticleRepository.findRequestShareArticleByToMemberIdArticleId(toMemberId, articleId);
        return findResult != null;
    }




}
