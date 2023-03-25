package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.SpringBootBaseTest;
import todo.application.TestUtils;
import todo.application.TestUtilsConstant;
import todo.application.controller.form.EditArticleForm;
import todo.application.domain.Article;
import todo.application.domain.ArticleStatus;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepositoryImpl;
import todo.application.repository.MemberArticleRepository;
import todo.application.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class ArticleServiceTest extends SpringBootBaseTest {


    @Autowired
    ArticleService articleService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleRepositoryImpl articleRepository;

    @Autowired
    MemberArticleRepository memberArticleRepository;

    @Autowired
    EntityManager em;

    @Test
    void saveNewArticleSuccessTest() {

        // given
        String myTitle = TestUtilsConstant.ARTICLE_TITLE;
        String myContents = TestUtilsConstant.ARTICLE_CONTENT;
        Member newMember = Member.createNewMember(TestUtilsConstant.MEMBER_NICKNAME,
                TestUtilsConstant.MEMBER_JOINID
                ,TestUtilsConstant.PASSWORD,
                TestUtilsConstant.EMAIL);
        memberRepository.saveMember(newMember);

        flushAndClear();

        // when
        Long articleId = articleService.saveNewArticle(myContents, myTitle, LocalDate.now(), newMember.getId());

        // then
        flushAndClear();

        Article findArticle = articleRepository.findArticleById(articleId);
        assertThat(findArticle.getId()).isEqualTo(articleId);
        assertThat(findArticle.getWriteTitle()).isEqualTo(TestUtilsConstant.ARTICLE_TITLE);
        assertThat(findArticle.getWriter()).isEqualTo(TestUtilsConstant.MEMBER_NICKNAME);
    }


    @Test
    void editArticleSuccessTest(){

        // given
        String myTitle = TestUtilsConstant.ARTICLE_TITLE;
        String myContents = TestUtilsConstant.ARTICLE_CONTENT;
        Member newMember = Member.createNewMember(TestUtilsConstant.MEMBER_NICKNAME,
                TestUtilsConstant.MEMBER_JOINID
                ,TestUtilsConstant.PASSWORD,
                TestUtilsConstant.EMAIL);
        memberRepository.saveMember(newMember);
        Long articleId = articleService.saveNewArticle(myContents, myTitle,LocalDate.now(), newMember.getId());

        flushAndClear();

        EditArticleForm editArticleForm = new EditArticleForm(
                TestUtilsConstant.UPDATE_ARTICLE_DUE_DATE,
                TestUtilsConstant.UPDATE_ARTICLE_TITLE,
                TestUtilsConstant.UPDATE_ARTICLE_CONTENT,
                TestUtilsConstant.UPDATE_ARTICLE_STATUS);


        // when
        articleService.editNewArticle(articleId, editArticleForm, newMember.getId());

        // then
        flushAndClear();

        Article findArticle = articleRepository.findArticleById(articleId);

        assertThat(findArticle.getWriteContents()).isEqualTo(TestUtilsConstant.UPDATE_ARTICLE_CONTENT);
        assertThat(findArticle.getWriteTitle()).isEqualTo(TestUtilsConstant.UPDATE_ARTICLE_TITLE);
        assertThat(findArticle.getDueDate()).isEqualTo(TestUtilsConstant.UPDATE_ARTICLE_DUE_DATE);
        assertThat(findArticle.getStatus()).isEqualTo(TestUtilsConstant.UPDATE_ARTICLE_STATUS);

    }

    @Test
    void shareArticleSuccessTest(){

        // given
        String myTitle = TestUtilsConstant.ARTICLE_TITLE;
        String myContents = TestUtilsConstant.ARTICLE_CONTENT;
        Member fromMember = Member.createNewMember(TestUtilsConstant.MEMBER_NICKNAME,
                TestUtilsConstant.MEMBER_JOINID
                ,TestUtilsConstant.PASSWORD,
                TestUtilsConstant.EMAIL);
        memberRepository.saveMember(fromMember);
        Long articleId = articleService.saveNewArticle(myContents, myTitle,LocalDate.now(), fromMember.getId());

        flushAndClear();
        Member toMember = TestUtils.createMemberForTest("share-");
        memberRepository.saveMember(toMember);


        // when
        articleService.shareArticleWithOthers(toMember.getId(), articleId, fromMember.getId());

        // then
        flushAndClear();
        Member findToMember = memberRepository.findMemberById(toMember.getId());
        Member findFromMember = memberRepository.findMemberById(fromMember.getId());

        assertThat(findToMember.getArticles().size()).isEqualTo(1);
        assertThat(findFromMember.getArticles().size()).isEqualTo(1);
    }



    void flushAndClear() {
        em.flush();
        em.clear();
    }


}