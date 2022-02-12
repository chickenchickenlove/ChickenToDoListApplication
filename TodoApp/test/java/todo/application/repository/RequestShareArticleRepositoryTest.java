package todo.application.repository;

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
import todo.application.domain.RequestShareArticle;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class RequestShareArticleRepositoryTest {

    @Autowired
    RequestShareArticleRepository requestShareArticleRepository;

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleRepositoryImpl articleRepository;


    @BeforeEach
    void init() {

        Member fromMember = Member.createNewMember("fromMember1", "fromMember1", "abc", "abc11@abc.com");
        Member toMember = Member.createNewMember("toMember1", "toMember1", "abc", "abdfdf11c@abc.com");
        memberRepository.saveMember(fromMember);
        memberRepository.saveMember(toMember);

        for (int i = 0; i < 100; i++) {
            Article article = Article.createArticle("share", "shareshare", LocalDate.now());
            article.setWriter(fromMember.getNickname());
            articleRepository.saveArticle(article);

            RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(toMember, fromMember, article);
            requestShareArticleRepository.saveRequestShareArticle(requestShareArticle);

        }

        em.flush();
        em.clear();

    }





    @Test
    @DisplayName("정상으로 저장")
    void saveTest() {
        //given
        Member fromMember = Member.createNewMember("fromMember", "fromMember", "abc", "abc@abc.com");
        Member toMember = Member.createNewMember("toMember", "toMember", "abc", "abdfdfc@abc.com");
        Article article = Article.createArticle("share", "shareshare", LocalDate.now());
        article.setWriter(fromMember.getNickname());

        memberRepository.saveMember(fromMember);
        memberRepository.saveMember(toMember);
        articleRepository.saveArticle(article);

        //when
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(fromMember, toMember, article);
        requestShareArticleRepository.saveRequestShareArticle(requestShareArticle);
        em.flush();
        em.clear();

        RequestShareArticle findRequestArticle = em.find(RequestShareArticle.class, requestShareArticle.getId());

        //then
        assertThat(findRequestArticle.getArticleTitle()).isEqualTo(requestShareArticle.getArticleTitle());

    }


    @Test
    @DisplayName("정상으로 조회")
    void findTest1() {
        //given
        Member fromMember = Member.createNewMember("fromMember", "fromMember", "abc", "abc@abc.com");
        Member toMember = Member.createNewMember("toMember", "toMember", "abc", "abdfdfc@abc.com");
        Article article = Article.createArticle("share", "shareshare", LocalDate.now());
        article.setWriter(fromMember.getNickname());

        memberRepository.saveMember(fromMember);
        memberRepository.saveMember(toMember);
        articleRepository.saveArticle(article);

        //when
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(fromMember, toMember, article);
        requestShareArticleRepository.saveRequestShareArticle(requestShareArticle);
        em.flush();
        em.clear();

        RequestShareArticle findRequestArticle = requestShareArticleRepository.findRequestShareArticle(requestShareArticle.getId());


        //then
        assertThat(findRequestArticle.getArticleTitle()).isEqualTo(requestShareArticle.getArticleTitle());
    }

    @Test
    @DisplayName("조회 실패 --> null 반환됨. ")
    void findTest2() {
        //given
        Member fromMember = Member.createNewMember("fromMember", "fromMember", "abc", "abc@abc.com");
        Member toMember = Member.createNewMember("toMember", "toMember", "abc", "abdfdfc@abc.com");
        Article article = Article.createArticle("share", "shareshare", LocalDate.now());
        article.setWriter(fromMember.getNickname());

        memberRepository.saveMember(fromMember);
        memberRepository.saveMember(toMember);
        articleRepository.saveArticle(article);

        //when
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(fromMember, toMember, article);
        requestShareArticleRepository.saveRequestShareArticle(requestShareArticle);
        em.flush();
        em.clear();

        RequestShareArticle findRequestArticle = requestShareArticleRepository.findRequestShareArticle(1000000L);
        //then
        assertThat(findRequestArticle).isNull();
    }


    @Test
    @DisplayName("페이징 테스트")
    void sliceTest1() {

        //given
        PageRequest pageable = PageRequest.of(0, 10);
        Member findMember = memberRepository.findMemberByJoinId("toMember1");


        //when
        Slice<RequestShareArticle> result = requestShareArticleRepository.findSliceRequestShareArticle(findMember.getId(), pageable);


        //then

        assertThat(result.getNumberOfElements()).isEqualTo(10);
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.hasNext()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
    }


    @Test
    @DisplayName("페이징 테스트 - 순서대로 정렬 되어있어야 한다.")
    void sliceTest2() {

        //given
        PageRequest pageable = PageRequest.of(0, 10);
        Member findMember = memberRepository.findMemberByJoinId("toMember1");


        //when
        Slice<RequestShareArticle> result = requestShareArticleRepository.findSliceRequestShareArticle(findMember.getId(), pageable);
        List<RequestShareArticle> content = result.getContent();

        //then
        assertThat(content.get(0).getId() < content.get(1).getId()).isTrue();
    }


    @Test
    @DisplayName("페이징 테스트 -> 없는 페이지 검색 시, 하나도 게시글이 없어야 한다.")
    void sliceTest3() {

        //given
        PageRequest pageable = PageRequest.of(999, 10);
        Member findMember = memberRepository.findMemberByJoinId("toMember1");


        //when
        Slice<RequestShareArticle> result = requestShareArticleRepository.findSliceRequestShareArticle(findMember.getId(), pageable);


        //then
        assertThat(result.getNumberOfElements()).isEqualTo(0);
        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
    }


    @Test
    @DisplayName("페이징 테스트 -> 멤버 없는 ID로 검색 시 0이 나와야한다.")
    void sliceTest4() {

        //given
        PageRequest pageable = PageRequest.of(0, 10);

        //when
        Slice<RequestShareArticle> result = requestShareArticleRepository.findSliceRequestShareArticle(124124124L, pageable);


        //then
        assertThat(result.getNumberOfElements()).isEqualTo(0);
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }






}