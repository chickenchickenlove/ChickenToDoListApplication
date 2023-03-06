package todo.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class VisitorViewTest {

    @Test
    void createMethodTest() {
        //given + when
        VisitorView visitorView = VisitorView.createVisitorView();

        //then
        assertThat(visitorView.getView()).isEqualTo(0L);
        assertThat(visitorView.getLoginView()).isEqualTo(0L);
        assertThat(visitorView.getDate()).isEqualTo(LocalDate.now());
        assertThat(visitorView.getUserJoin()).isEqualTo(0L);
        assertThat(visitorView.getWriteCreatedNumber()).isEqualTo(0L);

    }

    @Test
    void unitTest1(){
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorView.addView(1L);

        //then
        assertThat(visitorView.getView()).isEqualTo(1L);
    }


    @Test
    void unitTest2(){
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorView.addLoginView(1L);

        //then
        assertThat(visitorView.getLoginView()).isEqualTo(1L);
    }


    @Test
    void unitTest3(){
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorView.addUserJoin(1L);

        //then
        assertThat(visitorView.getUserJoin()).isEqualTo(1L);
    }


    @Test
    void unitTest4(){
        //given
        VisitorView visitorView = VisitorView.createVisitorView();

        //when
        visitorView.addWriteCreatedNumber(1L);

        //then
        assertThat(visitorView.getWriteCreatedNumber()).isEqualTo(1L);
    }






}