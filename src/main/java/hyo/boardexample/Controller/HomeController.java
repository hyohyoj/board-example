package hyo.boardexample.Controller;

import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember, Model model) {
        // 세션에 회원 데이터가 없으면 홈으로 이동
        if (loginMember == null) {
            return "index";
        }

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("member", loginMember);

        return "/boards/hello";
    }
}

//
