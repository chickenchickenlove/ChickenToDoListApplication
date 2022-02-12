package todo.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.service.MemberService;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
public class ArticleRepositoryTest {


    @Autowired
    ArticleRepositoryImpl articleRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;


    @BeforeEach
    public void init() {

        Long memberId = memberService.saveMember("김정수", "1", "1", "abc@abc");

        for (int i = 0; i < 25; i++) {
            memberService.saveMember(UUID.randomUUID().toString().substring(0,4), "test" + i, "test", i + "abc1@abc");
        }

        Member findMember = memberService.findMemberById(memberId);

        for (int i = 0; i < 25; i++) {
            Article saveArticle = Article.createArticle("ARTICLE" +i , "article" + i, LocalDate.now());
            saveArticle.setWriter(findMember.getNickname());
            MemberArticle saveMemberArticle = new MemberArticle();
            saveMemberArticle.addMemberArticle(findMember, saveArticle);
        }

        for (int i = 0; i < 25; i++) {
            memberService.saveMember("가나다" + i, "가나다" + i, "abc", "abc@!ab" + i);
        }


    }

    // Article : 25 , MemberArticle : 25, Member 51

    @Test
    @DisplayName("0페이지만 출력")
    void sliceArticleTest1() {

        System.out.println("TEST START ===========================");

        //given
        Member findMember = memberService.findMemberByJoinId("1");
        PageRequest pageable = PageRequest.of(0, 10);


        //when
        Slice<Article> findArticleSlice = articleRepository.findSliceArticleByMemberId(findMember.getId(), pageable);

        //then
        assertThat(findArticleSlice.getNumberOfElements()).isEqualTo(10);
        assertThat(findArticleSlice.isFirst()).isTrue();
        assertThat(findArticleSlice.isLast()).isFalse();
        assertThat(findArticleSlice.hasPrevious()).isFalse();


        List<Article> content = findArticleSlice.getContent();
        for (Article article : content) {
            System.out.println("article = " + article.getWriteTitle());
        }
    }



    @Test
    @DisplayName("0~2페이지 검증페이지만 출력")
    void sliceArticleTest2() {

        //given
        Member findMember = memberService.findMemberByJoinId("1");
        PageRequest pageable = PageRequest.of(0, 10);


        //when
        Slice<Article> findArticleSlicePage1 = articleRepository.findSliceArticleByMemberId(findMember.getId(), pageable);
        Slice<Article> findArticleSlicePage2 = articleRepository.findSliceArticleByMemberId(findMember.getId(), findArticleSlicePage1.nextPageable());
        Slice<Article> findArticleSlicePageLast = articleRepository.findSliceArticleByMemberId(findMember.getId(), findArticleSlicePage2.nextPageable());

        //then
        assertThat(findArticleSlicePage1.getNumberOfElements()).isEqualTo(10);
        assertThat(findArticleSlicePage1.isFirst()).isTrue();
        assertThat(findArticleSlicePage1.isLast()).isFalse();
        assertThat(findArticleSlicePage1.hasPrevious()).isFalse();


        assertThat(findArticleSlicePage2.getNumberOfElements()).isEqualTo(10);
        assertThat(findArticleSlicePage2.isFirst()).isFalse();
        assertThat(findArticleSlicePage2.isLast()).isFalse();
        assertThat(findArticleSlicePage2.hasPrevious()).isTrue();


        assertThat(findArticleSlicePageLast.getNumberOfElements()).isEqualTo(5);
        assertThat(findArticleSlicePageLast.isFirst()).isFalse();
        assertThat(findArticleSlicePageLast.isLast()).isTrue();
        assertThat(findArticleSlicePageLast.hasPrevious()).isTrue();
    }



    @Test
    @DisplayName("page = 0, size = 0이 들어올 경우? --> 예외 발생")
    void sliceArticleTest3() {

        //given
        Member findMember = memberService.findMemberByJoinId("1");


        // when + then

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    PageRequest pageable = PageRequest.of(0, 0);
                });
    }


}

