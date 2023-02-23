package hyo.boardexample.common;

import hyo.boardexample.domain.Login;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {

        HttpSession session = request.getSession(false);
        ModelAndView mv = new ModelAndView("/alert");
        Login loginMember = null;

        try{
            loginMember = (Login) session.getAttribute(SessionConstants.LOGIN_MEMBER);

            if(loginMember == null) {
                mv.addObject("msg", "세션이 만료되어 로그아웃 되었습니다.");
                mv.addObject("url", "/login");
                throw new ModelAndViewDefiningException(mv);
            } else {
                return true;
            }

        } catch (Exception e) {
            mv.addObject("msg", "세션이 만료되어 로그아웃 되었습니다.");
            mv.addObject("url", "/login");
            throw new ModelAndViewDefiningException(mv);
        }

    }
}
