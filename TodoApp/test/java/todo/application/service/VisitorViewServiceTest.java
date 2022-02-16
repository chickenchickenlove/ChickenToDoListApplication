package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.VisitorView;
import todo.application.repository.VisitorViewRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class VisitorViewServiceTest {


    private static final int threadNumber = 10000;


    @Autowired
    VisitorViewService visitorViewService;

    @Autowired
    VisitorViewRepository visitorViewRepository;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("addView 멀티 쓰레드 성공")
    void test() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addView();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getView().get()).isEqualTo(threadNumber);
    }

    @Test
    @DisplayName("addView 멀티 쓰레드 실패")
    void test1() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addView();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getView().get()).isNotEqualTo(threadNumber - 1000);
    }


    @Test
    @DisplayName("addLoginView 멀티 쓰레드 성공")
    void test2() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addLoginView();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getLoginView().get()).isEqualTo(threadNumber);
    }


    @Test
    @DisplayName("addLoginView 멀티 쓰레드 실패")
    void test3() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addLoginView();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getLoginView().get()).isNotEqualTo(threadNumber - 1000);
    }


    @Test
    @DisplayName("addUserJoin 멀티 쓰레드 성공")
    void test4() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.adduserJoin();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getUserJoin().get()).isEqualTo(threadNumber);
    }


    @Test
    @DisplayName("addUserJoin 멀티 쓰레드 실패")
    void test5() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.adduserJoin();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getUserJoin().get()).isNotEqualTo(threadNumber - 1000);
    }


    @Test
    @DisplayName("addWriteCreatedNumber 멀티 쓰레드 성공")
    void test6() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addWriteCreatedNumber();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getWriteCreatedNumber().get()).isEqualTo(threadNumber);
    }


    @Test
    @DisplayName("addWriteCreatedNumber 멀티 쓰레드 실패")
    void test7() throws Exception {

        //given
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);

        //when
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(() ->
                    {
                        visitorViewService.addWriteCreatedNumber();
                        latch.countDown();
                    }
            );
        }
        latch.await();

        //then
        assertThat(visitorViewService.getWriteCreatedNumber().get()).isNotEqualTo(threadNumber - 1000);
    }


    @Test
    void db_updateTest() throws Exception{
        //given
        int testNum = 100000;

        VisitorView visitorView = VisitorView.createVisitorView();
        visitorViewRepository.saveVisitorView(visitorView);
        em.flush();
        em.clear();

        for (int i = 0; i < testNum; i++) {
            visitorViewService.addView();
        }

        visitorViewService.dbUpdate();

        //when
        VisitorView findVisitorView = visitorViewRepository.findVisitorViewByDate(LocalDate.now());

        //then
        assertThat(findVisitorView.getView()).isEqualTo(testNum);

    }


    @Test
    void newVisitorViewSaveTest() {
        //given
        visitorViewService.newVisitorViewSave();

        //when
        VisitorView findVisitorView = visitorViewRepository.findVisitorViewByDate(LocalDate.now());

        //then
        assertThat(findVisitorView.getDate()).isEqualTo(LocalDate.now());



    }






}





