package todo.application.repository.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDto {

    private Long id;
    private String nickname;
    private String joinId;
    private String email;
    private LocalDateTime joinDate;


    public MemberDto(Long id, String nickname, String joinId, String email, LocalDateTime joinDate) {
        this.id = id;
        this.nickname = nickname;
        this.joinId = joinId;
        this.email = email;
        this.joinDate = joinDate;
    }
}
