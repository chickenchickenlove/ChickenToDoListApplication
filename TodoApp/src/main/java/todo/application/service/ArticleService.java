package todo.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

// TODO : 로그를 남기는 것보다 Exception을 만들어서 던지는 것이 좋을 듯. (뭔가 처리하지 않을 것이기 때문?)
// 넘어오는 것을 받아서 응답 코드를 만들어주는 형태가 더 좋을 듯 하다.
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepositoryImpl articleRepository;
    private final MemberRepository memberRepository;
    private final MemberArticleRepository memberArticleRepository;

    // 새로운 글 저장
    public Long saveNewArticle(String writeContents, String writeTitle, LocalDate dueDate, Long memberId) {

        Member findMember = memberRepository.findMemberById(memberId);

        Article newArticle = Article.createArticle(writeTitle, writeContents, dueDate, findMember.getNickname());
        MemberArticle memberArticle = MemberArticle.createMemberArticle(findMember, newArticle);

        articleRepository.saveArticle(newArticle);

        return newArticle.getId();
    }

    // Article 공유
    public void shareArticleWithOthers(Long toMemberId, Long articleId, Long fromMemberId) {

        // Validation : 공유할 사람이 권한이 있는지 확인함.
        if (!wasWrittenByThisMember(fromMemberId, articleId)) {
            return;
        }

        // validation : 공유할 대상에게 동일한 글이 있는지 확인한다.
        MemberArticle validation = articleRepository.findMemberArticleByMemberIdAndArticleId(toMemberId, articleId);

        if (validation != null) {
            log.info("동일한 글이 이미 해당 대상에게 있습니다. 따라서 공유가 안됩니다.");
            return;
        }

        // 성공 로직
        Member findMember = memberRepository.findMemberById(toMemberId);
        Article findArticle = articleRepository.findArticleById(articleId);
        MemberArticle memberArticle = MemberArticle.createMemberArticle(findMember, findArticle);
    }

    /**
     * 조회 로직
     */

    public List<MemberArticle> findArticleByMemberId(Long memberId){
        List<MemberArticle> memberArticleList = articleRepository.findArticleByMemberId(memberId);
        Collections.sort(memberArticleList);
        return memberArticleList;
    }

    public Article findArticleByArticleId(Long articleId){
        return articleRepository.findArticleById(articleId);
    }

    /**
     * 수정 로직
     */

    // 글 수정
    public void editNewArticle(Long articleId, EditArticleForm editArticle, Long memberId) {

        if (!wasWrittenByThisMember(memberId,articleId)) {
            log.info("적은 사람과 소유자가 달라 글을 수정할 수 없습니다. ");
            return;
        }

        Article findArticle = articleRepository.findArticleById(articleId);
        findArticle.update(editArticle.getDueDate(), editArticle.getStatus(), editArticle.getWriteTitle(), editArticle.getWriteContents());
    }

    // READY → COMPLETE로 상태 변환
    public void completeArticle(Long articleId, Long memberId) {
        if (!wasWrittenByThisMember(memberId, articleId)) {
            return;
        }
        // 더티 체킹
        Article articleById = articleRepository.findArticleById(articleId);
        articleById.setStatus(ArticleStatus.COMPLETE);
    }

    // 삭제 로직
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
        return findMemberArticle != null;
    }

}
