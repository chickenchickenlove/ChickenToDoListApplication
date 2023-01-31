package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepositoryImpl;
import todo.application.repository.MemberRepository;
import todo.application.service.ArticleService;
import todo.application.service.MemberArticleService;
import todo.application.service.RequestShareArticleService;

import java.time.LocalDate;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final ArticleRepositoryImpl articleRepository;
    private final MemberArticleService memberArticleService;
    private final ArticleService articleService;
    private final RequestShareArticleService requestShareArticleService;



    @GetMapping("/c9999")
    public String makeAdmin() {

        Member adminMember = Member.createAdminMember("admin1234", "admin1234","1","admin1@gmail.com");
        memberRepository.saveMember(adminMember);
        return "redirect:/admin";
    }



    @GetMapping("/a9999")
    public String makeTestData() {

        Member newMember = Member.createNewMember("test","test","1", "test1234@gmail.com");
        memberRepository.saveMember(newMember);

        for (int i = 1; i < 30; i++) {
            LocalDate date = LocalDate.of(2022, 3, i);

            if (i%3 == 0){
                articleService.saveNewArticle("인사는 90도 " + i, "인사는 90도 " + i, date, newMember.getId());
            }else if(i%3 == 1 ){
                articleService.saveNewArticle("360도 바뀐 나 " + i, "360도 바뀐 나 " + i, date, newMember.getId());
            }else{
                articleService.saveNewArticle("270도의 회전 " + i, "270도의 회전" + i, date, newMember.getId());
            }
        }
        return "redirect:/";
    }


    @GetMapping("/b9999")
    public String makeTestDataShare() {

        Member newMember = Member.createNewMember("테스트맨","test3","1", "test12342@gmail.com");
        memberRepository.saveMember(newMember);

        for (int i = 1; i < 30; i++) {
            LocalDate date = LocalDate.of(2022, 3, i);

            if (i%3 == 0){
                articleService.saveNewArticle("공유하고 싶어요!  " + i, "공유하고 싶어요! " + i, date, newMember.getId());
            }else if(i%3 == 1 ){
                articleService.saveNewArticle("코딩 공부 열심히 " + i, "코딩 공부 열심히 " + i, date, newMember.getId());
            }else{
                articleService.saveNewArticle("안녕히 계세요 여러분 " + i, "안녕히 계세요 여러분" + i, date, newMember.getId());
            }
        }


        List<MemberArticle> findMemberArticle = memberArticleService.findArticleListByMemberId(newMember.getId());
        Member toMember = memberRepository.findMemberByJoinId("test");


        // Form에서 쿼리 파라미터로 MemberId를 넘겨줌 → 존재하는게 보장됨. Null Check 필요 X.
        Long toMemberId = toMember.getId();
        Long fromMemberId = newMember.getId();


        for (MemberArticle memberArticle : findMemberArticle) {
            Long shareArticleId = memberArticle.getArticle().getId();
            requestShareArticleService.saveRequestShareArticle(fromMemberId, toMemberId, shareArticleId);
        }

        return "redirect:/";
    }


}
