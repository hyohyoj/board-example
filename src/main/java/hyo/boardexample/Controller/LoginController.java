package hyo.boardexample.Controller;

import com.nhncorp.lucy.security.xss.XssPreventer;
import hyo.boardexample.Service.LoginService;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final PasswordEncoder pwEncoder;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        return "/error";
    }

    //xss 필터링 테스트
    @PostMapping("/parameter")
    @ResponseBody
    //@RequestParam String input
    public String strInput(@RequestParam String input){
        String convertMsg = XssPreventer.escape(input);

        return loginService.stringTest(convertMsg);
    }
    //xss 필터링 json 형식 테스트
    @PostMapping("/dto")
    @ResponseBody
    public Login dtoInput(@RequestBody Login login){
        return loginService.dtoTest(login);
    }

    @GetMapping(value = {"/login", "/admin/login"})
    public String loginForm(Model model, HttpServletRequest request) {
        String path = request.getRequestURI();
        System.out.println(path);

        model.addAttribute("loginForm", new Login());
        model.addAttribute("path", path);

        return "/login/loginForm";
    }

    @PostMapping("/loginCheck")
    @ResponseBody
    public String loginCheck(@ModelAttribute Login loginForm) {
        String rawPw = "";
        String encodePw = "";

        Login loginMember = loginService.getUser(loginForm);

        if(loginMember == null) {
            return "아이디가 일치하지 않습니다.";
        }

        rawPw = loginForm.getUser_pw();
        encodePw = loginMember.getUser_pw();

        //System.out.println("rawPw : " + rawPw + ", encodePw : " + encodePw);

        if(!pwEncoder.matches(rawPw, encodePw)) {
            return "비밀번호가 일치하지 않습니다.";
        }


        return "성공";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute Login loginForm,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request,
                        Model model) {

        Login loginMember = loginService.getUser(loginForm);
        String path = request.getParameter("path");

        // 바인딩 에러가 일어났을 경우 loginForm으로 다시 던짐
        if(bindingResult.hasErrors()) {
            model.addAttribute("loginForm", new Login());
            return "/login/loginForm";
        }
        if(loginMember == null || !pwEncoder.matches(loginForm.getUser_pw(), loginMember.getUser_pw())) {
            //bindingResult.addError(new FieldError("loginForm", "user_id", "아이디 또는 비밀번호가 맞지 않습니다."));
            //bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
            model.addAttribute("loginForm", loginForm);
            return "/login/loginForm";
        }

        // 관리자 페이지 주소로 접근 시
        if(path.equals("/admin/login")) {
            if(loginMember.getAuth_code().equals("admin")) {
                // 로그인 성공
                HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하여 반환
                session.setAttribute(SessionConstants.LOGIN_MEMBER, loginMember);   // 세션에 로그인 회원 정보 보관

                return "redirect:/admin";
            } else {
                model.addAttribute("loginForm", loginMember);
                model.addAttribute("msg", "관리자 권한이 없습니다.");
                model.addAttribute("path", path);
                return "/login/loginForm";
            }
        }

        // 로그인 성공
        HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하여 반환
        session.setAttribute(SessionConstants.LOGIN_MEMBER, loginMember);   // 세션에 로그인 회원 정보 보관

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        HttpSession session = request.getSession(false);
//
//        if(session != null) {
//            session.invalidate();
//        }

        // 해당 핸들러를 사용하면 logoutSuccessUrl(String)이 무시됨.
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        return "redirect:/admin";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signUpForm", new Login());
        return "/login/signUpForm";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Login signUpForm, Model model) {
        String rawPw = "";
        String encodePw = "";

        Login signUpMember = loginService.getUser(signUpForm);

        if(signUpMember != null) {  // 중복 아이디 존재 시, 다시 폼 화면으로 던짐
            model.addAttribute("signUpForm", new Login());
            return "/login/signUpForm";
        }

        rawPw = signUpForm.getUser_pw();
        encodePw = pwEncoder.encode(rawPw);
        signUpForm.setUser_pw(encodePw);

        int success = loginService.insertUser(signUpForm);

        if(success <= 0) {  // insert 실패
            model.addAttribute("signUpForm", new Login());
            return "/login/signUpForm";
        }

        return "/login/signUpSuccess";
    }

    @PostMapping("/signUpCheck")
    @ResponseBody
    public String signUpCheck(@ModelAttribute Login signUpForm) {

        Login loginMember = loginService.getUser(signUpForm);

        if(loginMember != null) {
            return "중복 아이디 입니다.";
        }

        return "성공";
    }
}
