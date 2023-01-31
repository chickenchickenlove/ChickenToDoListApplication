package todo.application.repository.dto;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ViewReadCountStaticalDto {

    private String date; // 저장 날짜
    private Long view; // 오늘 방문자 수
    private Long loginView; // 오늘 로그인 방문자수
    private Long userJoin; // 회원 가입 수
    private Long writeCreatedNumber; // 글 생성 수

    public ViewReadCountStaticalDto(String date, Long view, Long loginView, Long userJoin, Long writeCreatedNumber) {
        this.date = date;
        this.view = view;
        this.loginView = loginView;
        this.userJoin = userJoin;
        this.writeCreatedNumber = writeCreatedNumber;
    }
}
