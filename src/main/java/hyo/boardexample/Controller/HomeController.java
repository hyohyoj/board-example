package hyo.boardexample.Controller;

import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        return "/error";
    }

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

    @GetMapping("/admin")
    public String adminHome(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        // 세션에 회원 데이터가 없으면 관리자 홈으로 이동
        if (loginMember == null) {
            return "/admin/home";
        }
        // 관리자가 아닐 경우 관리자 홈으로 이동
        if (!loginMember.getAuth_code().equals("admin")) {
            session.invalidate();
            return "/admin/home";
        }

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("member", loginMember);

        return "/admin/boardManage";
    }

}

