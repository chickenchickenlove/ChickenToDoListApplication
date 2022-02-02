package todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
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


    //TODO : TODO LIST 공유
    

    // 새로운 글 저장
    @Transactional(readOnly = false)
    public Long saveNewArticle(String writeContents, String writeTitle, LocalDate dueDate, Long memberId) {

        Member findMember = memberRepository.findMemberById(memberId);
        Article article = Article.createArticle(LocalDateTime.now(), writeTitle, writeContents, dueDate);

        MemberArticle memberArticle = new MemberArticle();
        memberArticle.addMemberArticle(findMember, article);
        articleRepository.saveArticle(article);

        return article.getId();
    }


    // 글 수정
    @Transactional(readOnly = false)
    public void editNewArticle(Long articleId, String editWriteTitle, String editWriteContents) {

        // 더티 체킹
        Article articleById = articleRepository.findArticleById(articleId);
        articleById.setWriteTitle(editWriteTitle);
        articleById.setWriteContents(editWriteContents);

    }


    public List<MemberArticle> findArticleByMemberId(Long memberId){
        List<MemberArticle> articleByMemberId = articleRepository.findArticleByMemberId(memberId);

        Collections.sort(articleByMemberId);

        return articleByMemberId;
    }

    public Article findArticleByArticleId(Long articleId){
        return articleRepository.findArticleById(articleId);
    }


}
