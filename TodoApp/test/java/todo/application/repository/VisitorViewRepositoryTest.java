package todo.application.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.VisitorView;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class VisitorViewRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    VisitorViewRepository visitorViewRepository;


    @Test
    void saveTest() throws Exception{
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();

        //then
        Assertions.assertThat(visitorView.getId()).isNotNull();
    }

    @Test
    void findTestById1() throws Exception{
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();
        VisitorView findView = visitorViewRepository.findVisitorViewById(visitorView.getId());


        //then
        Assertions.assertThat(findView.getId()).isEqualTo(visitorView.getId());
    }


    @Test
    void findTestById2() throws Exception{
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();
        VisitorView findView = visitorViewRepository.findVisitorViewById(1000000L);


        //then
        Assertions.assertThat(findView).isNull();
    }


    @Test
    void findTestByDate1() throws Exception{
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();
        VisitorView findView = visitorViewRepository.findVisitorViewByDate(visitorView.getDate());

        //then
        Assertions.assertThat(findView.getId()).isEqualTo(visitorView.getId());
    }


    @Test
    void findTestByDate2() throws Exception{
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();
        VisitorView findView = visitorViewRepository.findVisitorViewByDate(LocalDate.of(2022,2,3));

        //then
        Assertions.assertThat(findView).isNull();
    }








}