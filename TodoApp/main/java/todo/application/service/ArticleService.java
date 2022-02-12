package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.controller.form.EditArticleForm;
import todo.application.domain.Article;
import todo.application.domain.ArticleStatus;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepositoryImpl;
import todo.application.repository.MemberArticleRepository;
import todo.application.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepositoryImpl articleRepository;
    private final MemberRepository memberRepository;
    private final MemberArticleRepository memberArticleRepository;

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
    @Transactional(readOnly = false)
    public void shareArticleWithOthers(Long memberId, Long articleId, Long originalMemberId) {

        System.out.println("HEREHERE1");

        // Validation : 공유할 사람이 권한이 있는지 확인함.
        if (!wasWrittenByThisMember(originalMemberId, articleId)) {
            return;
        }


        System.out.println("HEREHERE2");


        // validation : 공유할 대상에게 동일한 글이 있는지 확인한다.
        MemberArticle validation = articleRepository.findMemberArticleByMemberIdAndArticleId(memberId, articleId);


        System.out.println("HEREHERE3");

        if (validation != null) {
            log.info("동일한 글이 이미 해당 대상에게 있습니다. 따라서 공유가 안됩니다.");
            return;
        }


        System.out.println("HEREHERE4");

        // 성공로직
        Member findMember = memberRepository.findMemberById(memberId);
        Article findArticle = articleRepository.findArticleById(articleId);
        MemberArticle memberArticle = new MemberArticle();
        memberArticle.addMemberArticle(findMember, findArticle);


        System.out.println("HEREHERE5");


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
    public void editNewArticle(Long articleId, EditArticleForm editArticle, Long memberId) {

        if (!wasWrittenByThisMember(memberId,articleId)) {
            log.info("적은 사람과 소유자가 달라 글을 수정할 수 없습니다. ");
            return;
        }

        Article articleById = articleRepository.findArticleById(articleId);

        // 더티 체킹

        articleById.setDueDate(editArticle.getDueDate());
        articleById.setStatus(editArticle.getStatus());
        articleById.setWriteTitle(editArticle.getWriteTitle());
        articleById.setWriteContents(editArticle.getWriteContents());

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

        MemberArticle findMemberArticle = memberArticleRepository.findMemberArticleByMemberIdArticleIdAndMemberNickEqualArticleWriter(memberId, articleId);

        return findMemberArticle != null ? true : false;
    }






}
