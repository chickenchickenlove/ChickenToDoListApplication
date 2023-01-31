package todo.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.VisitorView;
import todo.application.repository.VisitorViewRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

//@Component
@Transactional
@RequiredArgsConstructor
public class VisitorViewConfig {

    private final VisitorViewRepository visitorViewRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Long k = 10L;
        for (int i = 1; i < 16; i++) {
            VisitorView visitorView = VisitorView.createVisitorView();
            LocalDate date = LocalDate.of(2022, 2, i);

            if (i % 2 == 0) {
                visitorView.setTest(date, k, k, k, k);
            } else {
                visitorView.setTest(date, k * 10, k * 10, k * 10, k * 10);
            }
            visitorViewRepository.saveVisitorView(visitorView);
        }
    }
}