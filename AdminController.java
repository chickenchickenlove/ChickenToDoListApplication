package todo.application.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import todo.application.repository.dto.MemberAdminDto;
import todo.application.repository.dto.ViewReadCountStaticalDto;
import todo.application.service.MemberService;
import todo.application.service.VisitorViewService;

import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final VisitorViewService visitorViewService;
    private final MemberService memberService;


    // admin 홈 화면
    @GetMapping("/admin")
    public String adminHome(Model model) {

        List<ViewReadCountStaticalDto> readCountStatDtoByDate = visitorViewService.findReadCountStatDtoByDate(LocalDate.now());
        List<MemberAdminDto> allMemberAdminDto = memberService.findAllMemberAdminDto();

        model.addAttribute("dtos", readCountStatDtoByDate);
        model.addAttribute("memberDtos", allMemberAdminDto);

        return "admin/index";
    }



    // admin list 화면
    @GetMapping("/admin/memberlist")
    public String adminHome3(Model model) {

        List<ViewReadCountStaticalDto> readCountStatDtoByDate = visitorViewService.findReadCountStatDtoByDate(LocalDate.now());
        List<MemberAdminDto> allMemberAdminDto = memberService.findAllMemberAdminDto();

        model.addAttribute("dtos", readCountStatDtoByDate);
        model.addAttribute("memberDtos", allMemberAdminDto);

        return "admin/memberAdmin";
    }

    // admin delete 삭제
    @GetMapping("/admin/delete")
    public String adminHome2(@RequestParam(name = "memberId") String memberId) {
        memberService.batchRemove(memberId);
        return "redirect:/admin";
    }

}
