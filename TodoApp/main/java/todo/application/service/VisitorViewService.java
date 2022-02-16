package todo.application.service;

import jdk.swing.interop.SwingInterOpUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.application.controller.aop.annotation.Retry;
import todo.application.domain.VisitorView;
import todo.application.repository.VisitorViewRepository;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;



@Service
@Slf4j
@Transactional(readOnly = true)
@Getter
public class VisitorViewService {

    private final VisitorViewRepository visitorViewRepository;

    private LocalDate date; // 저장 날짜
    private AtomicLong view; // 오늘 방문자 수
    private AtomicLong loginView; // 오늘 로그인 방문자수
    private AtomicLong userJoin; // 회원 가입 수
    private AtomicLong writeCreatedNumber; // 글 생성 수

    public VisitorViewService(VisitorViewRepository visitorViewRepository) {
        this.visitorViewRepository = visitorViewRepository;
        this.date = LocalDate.now();
        this.view = new AtomicLong();
        this.loginView = new AtomicLong();
        this.userJoin = new AtomicLong();
        this.writeCreatedNumber = new AtomicLong();
    }


    //== 조회 로직==//

    public VisitorView findVisitorViewByDate(LocalDate date) {
        return visitorViewRepository.findVisitorViewByDate(date);
    }



    // DB에 저장한다.
    @Transactional
    public void dbUpdate() {

        log.info("DB Update Start");

        // 더티 체킹
        VisitorView visitorView = visitorViewRepository.findVisitorViewByDate(date);
        if (visitorView == null) {

            log.info("THERE IS NO DATA in DB");

            return;
        }


        visitorView.addView(view.get());
        visitorView.addUserJoin(userJoin.get());
        visitorView.addLoginView(loginView.get());
        visitorView.addWriteCreatedNumber(writeCreatedNumber.get());

        log.info("DB Update Completed");

    }

    // DB BackUP 로직
    public void readCountRecovery() {
        log.info("ReadCount Instance Recovery Start");
        VisitorView findVisitorView = visitorViewRepository.findVisitorViewByDate(LocalDate.now());

        // 날짜가 바뀌어, 아직 새로운 것이 만들어지지 않은 경우
        if (findVisitorView == null) {
            log.info("There is no ReadCount Instance in DB on {}", LocalDate.now());
            log.info("Plz, Wait a moment. ReadCount Instance will be created on midnight automatically");
            log.info("ReadCount Instance Recovery Completed");
            return;
        }

        // 날짜가 바뀌어, 새로운 것이 만들어졌을 때
        if (!findVisitorView.getDate().equals(LocalDate.now())) {
            log.info("There is no ReadCount Instance in DB on {}", LocalDate.now());
            log.info("Plz, Wait a moment. ReadCount Instance will be created on midnight automatically");
            log.info("ReadCount Instance Recovery Completed");
            return;
        }


        // DB에 문제가 없는 경우
        Long visitorViewSum = view.get() + loginView.get() + userJoin.get() + writeCreatedNumber.get();
        if (findVisitorView.getTotalValue() <= visitorViewSum) {
            log.info("ReadCount Instance has No Problem");
            log.info("ReadCount Instance Recovery Completed");
            return;
        }

        log.info("ReadCount Instance Has Problem");
        // DB에 문제가 있는 경우
        view.set(findVisitorView.getView());
        loginView.set(findVisitorView.getLoginView());
        userJoin.set(findVisitorView.getUserJoin());
        writeCreatedNumber.set(findVisitorView.getWriteCreatedNumber());
        date = findVisitorView.getDate();
        log.info("ReadCount Instance Recovery Completed");

    }




    // 스케쥴러로 매일 정각에 만든다.
    @Transactional
    public void newVisitorViewSave() {
        VisitorView visitorView = VisitorView.createVisitorView();
        visitorViewRepository.saveVisitorView(visitorView);
    }


    //== 비즈니스 메서드==//
    public void addView() {
        view.incrementAndGet();
    }

    public void addLoginView() {
        loginView.incrementAndGet();
    }

    public void adduserJoin() {
        userJoin.incrementAndGet();
    }

    public void addWriteCreatedNumber() {
        writeCreatedNumber.incrementAndGet();
    }

    public void setVisitorViewServiceField(VisitorView visitorView) {

        view.set(visitorView.getView());
        loginView.set(visitorView.getLoginView());
        userJoin.set(visitorView.getUserJoin());
        writeCreatedNumber.set(visitorView.getWriteCreatedNumber());
    }






}
