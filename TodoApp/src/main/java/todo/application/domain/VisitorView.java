package todo.application.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
public class VisitorView extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "visitor_view_id")
    private Long id;

    //== 비관적 락==//
//    @Version
//    private Long version;

    @Column(unique = true)
    private LocalDate date; // 저장 날짜
    private Long view; // 오늘 방문자 수
    private Long loginView; // 오늘 로그인 방문자수

    private Long userJoin; // 회원 가입 수
    private Long writeCreatedNumber; // 글 생성 수

    //= 비즈니스 메서드 ==//
    public Long addView(Long view) {
        if (view >= this.view) {
            this.view = view;
            return this.view;
        }
        throw new IllegalStateException("예외 발생");
    }

    public Long addLoginView(Long loginView) {
        if (loginView >= this.loginView) {
            this.loginView = loginView;
            return this.loginView;
        }
        throw new IllegalStateException("예외 발생");
    }

    public Long addUserJoin(Long userJoin) {
        if (userJoin >= this.userJoin) {
            this.userJoin = userJoin;
            return this.userJoin;
        }
        throw new IllegalStateException("예외 발생");
    }

    public Long addWriteCreatedNumber(Long writeCreatedNumber) {
        if (writeCreatedNumber >= this.writeCreatedNumber) {
            this.writeCreatedNumber = writeCreatedNumber;
            return this.writeCreatedNumber;
        }
        throw new IllegalStateException("예외 발생");
    }

    public Long getTotalValue() {
        return view + loginView + userJoin + writeCreatedNumber;

    }

    public void setTest(LocalDate date, Long view, Long loginView, Long userJoin, Long writeCreatedNumber ) {
        this.date = date;
        this.view = view;
        this.loginView = loginView;
        this.userJoin = userJoin;
        this.writeCreatedNumber = writeCreatedNumber;
    }

    //== 생성 메서드 ==//
    public static VisitorView createVisitorView() {
        VisitorView visitorView = new VisitorView();

        visitorView.date = LocalDate.now();
        visitorView.view = 0L;
        visitorView.loginView = 0L;
        visitorView.userJoin = 0L;
        visitorView.writeCreatedNumber = 0L;

        return visitorView;
    }

}
