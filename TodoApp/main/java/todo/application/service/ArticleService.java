package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.ArticleStatus;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepository;
import todo.application.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;


    //TODO : Validation 기능 개발. 글 수정, 삭제, 공유는 원본 글쓴이만 가능하다.
    

    // 새로운 글 저장
    @Transactional(readOnly = false)
    public Long saveNewArticle(String writeContents, String writeTitle, LocalDate dueDate, Long memberId) {

        Member findMember = memberRepository.findMemberById(memberId);
        Article article = Article.createArticle(writeTitle, writeContents, dueDate);


        MemberArticle memberArticle = new MemberArticle();
        memberArticle.addMemberArticle(findMember, article);

        article.setWriter(findMember.getNickname());

        articleRepository.saveArticle(article);

        return article.getId();
    }


    // Article 공유
    // 이미 있는 글이면 공유 못하게 하는 Validation 필요함.
    // 공유되는 놈이 memberId임
    @Transactional(readOnly = false)
    public void shareArticleWithOthers(Long memberId, Long articleId, Long originalMemberId) {

        // Validation : 권한이 있는지 확인함.
        if (!wasWrittenByThisMember(originalMemberId, articleId)) {
            return;
        }

        // validation :
        // 멤버 찾는다
        // memberId와 articleId가 정확히 만족하는 memberArticle이 있는지 확인한다
        List<MemberArticle> validation = articleRepository.findMemberArticleByMemberIdAndArticleId(memberId, articleId);

        // 있다면 에러메세지를 띄워준다
        if (validation.size() > 0) {
            log.info("동일한 글이 이미 해당 대상에게 있습니다. 따라서 공유가 안됩니다.");
            return;
        }

        // 없다면 뒷쪽으로 넘어간다
        Member findMember = memberRepository.findMemberById(memberId);

        // MemberArticle을 찾는다.
        Article findArticle = articleRepository.findArticleById(articleId);

        MemberArticle memberArticle = new MemberArticle();
        memberArticle.addMemberArticle(findMember, findArticle);

    }


    /**
     * 조회 로직
     */


    public List<MemberArticle> findArticleByMemberId(Long memberId){
        List<MemberArticle> articleByMemberId = articleRepository.findArticleByMemberId(memberId);

        Collections.sort(articleByMemberId);

        return articleByMemberId;
    }

    public Article findArticleByArticleId(Long articleId){
        return articleRepository.findArticleById(articleId);
    }


    /**
     * 수정 로직
     */

    // 글 수정
    @Transactional(readOnly = false)
    public void editNewArticle(Long articleId, ArticleStatus status,String editWriteTitle, String editWriteContents, Long memberId) {


        if (!wasWrittenByThisMember(memberId,articleId)) {
            log.info("적은 사람과 소유자가 달라 글을 수정할 수 없습니다. ");
            return;
        }


        // 더티 체킹

        log.info("더티 체킹 기대1");
        Article articleById = articleRepository.findArticleById(articleId);
        articleById.setStatus(status);
        articleById.setWriteTitle(editWriteTitle);
        articleById.setWriteContents(editWriteContents);

        log.info("더티 체킹 기대2");


    }


    // READY → COMPLETE로 상태 변환
    @Transactional(readOnly = false)
    public void completeArticle(Long articleId, Long memberId) {

        if (!wasWrittenByThisMember(memberId, articleId)) {
            return;
        }

        // 더티 체킹
        Article articleById = articleRepository.findArticleById(articleId);
        articleById.setStatus(ArticleStatus.COMPLETE);
    }



    /**
     * 삭제 로직
     */

    // 삭제 로직
    @Transactional(readOnly = false)
    public void deleteArticle(Long articleId, Long memberId) {

        if (!wasWrittenByThisMember(memberId,articleId)) {
            log.info("적은 사람과 소유자가 달라 글을 수정할 수 없습니다. ");
            return;
        }

        articleRepository.removeMemberArticle(articleId);



    }




    /**
     * Validation 로직
     */

    public boolean wasWrittenByThisMember(Long memberId, Long articleId) {

        log.info("wasWrittenByThisMember findMember {} : ", memberId);
        log.info("wasWrittenByThisMember findArticle {} : ", articleId);

        // 엔티티 조회
        Member findMember = memberRepository.findMemberById(memberId);
        Article findArticle = articleRepository.findArticleById(articleId);

        log.info("wasWrittenByThisMember findMember {} : ", findMember.toString());
        log.info("wasWrittenByThisMember findArticle {} : ", findArticle.toString());


        // 엔티티끼리 비교
        return findArticle.getWriter().equals(findMember.getNickname());
    }






}
