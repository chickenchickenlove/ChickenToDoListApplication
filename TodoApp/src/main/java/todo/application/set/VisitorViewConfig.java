package todo.application.set;

import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.VisitorView;
import todo.application.repository.VisitorViewRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

//@Component
public class VisitorViewConfig {

    private final InitClass2 initClass2;

    public VisitorViewConfig(InitClass2 initClass2) {
        this.initClass2 = initClass2;
    }

    @PostConstruct
    public void init() {
        initClass2.init();

    }

//    @Component
    @Transactional
    static class InitClass2 {

        private final VisitorViewRepository visitorViewRepository;

        public InitClass2(VisitorViewRepository visitorViewRepository) {
            this.visitorViewRepository = visitorViewRepository;
        }

        public void init() {
            Long k = 10L;
            for (int i = 1; i < 16; i++) {
                VisitorView visitorView = VisitorView.createVisitorView();
                LocalDate date = LocalDate.of(2022, 2, i);

                if(i%2==0){
                    visitorView.setTest(date, k,k,k,k);
                }else{
                    visitorView.setTest(date, k*10,k*10,k*10,k*10);
                }
                visitorViewRepository.saveVisitorView(visitorView);
            }
        }

    }



}
