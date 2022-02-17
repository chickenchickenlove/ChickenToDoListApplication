package todo.application.repository.dto;

import lombok.Data;
import todo.application.domain.MemberArticle;
import todo.application.domain.RequestShareArticle;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
public class MemberAdminDto {

    //==  기본 정보 ==//
    private Long id;
    private String nickname;
    private String joinId;
    private String email;
    private String joinDate;

    public MemberAdminDto(Long id, String nickname, String joinId, String email, String joinDate) {
        this.id = id;
        this.nickname = nickname;
        this.joinId = joinId;
        this.email = email;
        this.joinDate = joinDate;
    }
}
