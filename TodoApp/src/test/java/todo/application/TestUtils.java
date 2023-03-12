package todo.application;

import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Member;

public class TestUtils {

    public static String NEW_NICKNAME = "A";
    public static String NEW_JOINID = "AAA";
    public static String NEW_PASSWORD = "PASS";
    public static String NEW_EMAIL = "NEW_EMAIL@naver.com";


    public static MemberJoinForm createNewMemberJoinForm() {
        return MemberJoinForm.createMemberJoinForm(
                NEW_NICKNAME, NEW_JOINID, NEW_PASSWORD, NEW_EMAIL
        );
    }
}
