package todo.application.repository;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = {"nickname", "joinId"})
public class MemberSearch {

    private String nickname;
    private String joinId;

}
