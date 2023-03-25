package todo.application;

import todo.application.domain.ArticleStatus;
import todo.application.domain.Member;

import java.time.LocalDate;

public class TestUtilsConstant {


    // Member
    public static String MEMBER_NICKNAME = "John";
    public static String MEMBER_JOINID = "JohnID";
    public static String PASSWORD = "ABCD";
    public static String EMAIL = "abcde@naver.com";

    // TO_MEMBER
    public static String TO_MEMBER_NICKNAME = "James";
    public static String TO_MEMBER_JOINID = "JamesID";
    public static String TO_PASSWORD = "QWER";
    public static String TO_EMAIL = "james@naver.com";



    // ARTICLE
    public static String ARTICLE_TITLE= "title1";
    public static String ARTICLE_CONTENT = "content1";
    public static String ARTICLE_WRITER = "dev1";
    public static LocalDate ARTICLE_DUE_DATE = LocalDate.of(2002, 1, 1);

    public static String UPDATE_ARTICLE_TITLE= "update-title";
    public static String UPDATE_ARTICLE_CONTENT = "update-content";
    public static String UPDATE_ARTICLE_WRITER = "dev2";
    public static LocalDate UPDATE_ARTICLE_DUE_DATE = LocalDate.of(2022, 1, 1);
    public static ArticleStatus UPDATE_ARTICLE_STATUS = ArticleStatus.ING;



    public static Member createMember() {
        return null;
    }


}
