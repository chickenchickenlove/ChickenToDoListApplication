package todo.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import todo.application.domain.VisitorView;
import todo.application.service.VisitorViewService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
@Slf4j
public class ReportScheduleConfig {

    private final VisitorViewService visitorViewService;

    public ReportScheduleConfig(VisitorViewService visitorViewService) {
        this.visitorViewService = visitorViewService;
    }

    @PostConstruct
    public void init() {
        log.info("visitorViewService Sync. With DB");

        VisitorView findVisitorView = visitorViewService.findVisitorViewByDate(LocalDate.now());
        // 현재 날짜에 값이 없으면
        if (findVisitorView == null) {
            visitorViewService.newVisitorViewSave();
            return;
        }

        // 현재 날짜에 값이 있으면 초기화 해준다
        visitorViewService.setVisitorViewServiceField(findVisitorView);
        log.info("visitorViewService Sync. Completed With DB ");
    }

    // 매일 New Row 생성
    @Scheduled(cron = "0 0 0 * * *")
    public void createNewVisitorViewAndSave() {
        log.info("Create new VisitorView in {}", LocalDate.now());
        visitorViewService.newVisitorViewSave();
    }

    // 5분 마다 DB 갱신
    @Scheduled(fixedRate = 300000)
    public void dbUpdateInterval5Min() {
        log.info("DB Update Start with ReadCount");
        visitorViewService.dbUpdate();
        log.info("DB Update Completed with ReadCount");
    }

    // 5분 5초 마다 인스턴스 리커버리
    @Scheduled(fixedRate = 305000)
    public void dbBackInterval5Min5sec() {
        visitorViewService.readCountRecovery();
    }


}
