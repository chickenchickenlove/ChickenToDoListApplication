package todo.application.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class MemberFindJoinIdForm {

    @Email
    private String email;
}
