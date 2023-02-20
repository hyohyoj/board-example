package hyo.boardexample.Controller;

import hyo.boardexample.Service.BoardTypeService;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.BoardType;
import hyo.boardexample.domain.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardTypeService boardTypeService;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        return "/error";
    }

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
                       @ModelAttribute Board board,
                       Model model)
    {
        // 세션에 회원 데이터가 없으면 홈으로 이동
        if (loginMember == null) {
            return "index";
        }

        List<BoardType> boardTypeList = boardTypeService.getBoardTypeList("user");

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("member", loginMember);
        model.addAttribute("typeList", boardTypeList);
        // 페이지 값 유지
        if(board.getType_no() == null) {
            model.addAttribute("type_no", boardTypeList.get(0).getType_no());
        } else {
            model.addAttribute("type_no", board.getType_no());
        }
        model.addAttribute("selected_page", board.getSelected_page());
        model.addAttribute("keyword", board.getKeyword());
        model.addAttribute("searchContent", board.getSearchContent());

        return "/boards/hello";
    }
    
      // 관리자 페이지 프로젝트 분리
//    @GetMapping("/admin")
//    public String adminHome(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember, Model model, HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//
//        // 세션에 회원 데이터가 없으면 관리자 홈으로 이동
//        if (loginMember == null) {
//            return "/admin/home";
//        }
//        // 관리자가 아닐 경우 관리자 홈으로 이동
//        if (!loginMember.getAuth_code().equals("admin")) {
//            session.invalidate();
//            return "/admin/home";
//        }
//
//        // 세션이 유지되면 로그인 홈으로 이동
//        model.addAttribute("member", loginMember);
//
//        return "/admin/boardManage";
//    }

    // 날짜 테스트
    @GetMapping("/date")
    @ResponseBody
    public ModelAndView dateTest(
            @RequestParam(value = "now", defaultValue = "", required = false) String now,
            @RequestParam(value = "type", defaultValue = "", required = false) String type) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/dateTest");

        String nowDate = "";
        String preDate = "";
        String nextDate = "";

        if(now.isEmpty()) {
            nowDate = sdf.format(cal.getTime());

            cal.add(cal.DATE, -1);  // 하루 전
            preDate = sdf.format(cal.getTime());

            cal.add(cal.DATE, +2);  // 하루 후
            nextDate = sdf.format(cal.getTime());
        } else {
            // 이전날
            if(type.equals("pre")) {
                // 가운데 날짜를 담음
                cal.setTime(sdf.parse(now));

                cal.add(cal.DATE, -1);  // 현재
                nowDate = sdf.format(cal.getTime());

                cal.add(cal.DATE, -1);  // 하루 전
                preDate = sdf.format(cal.getTime());

                cal.add(cal.DATE, +2);  // 하루 후
                nextDate = sdf.format(cal.getTime());
            } // 다음날
            else if(type.equals("next")) {
                // 가운데 날짜를 담음
                cal.setTime(sdf.parse(now));

                cal.add(cal.DATE, +1);  // 현재
                nowDate = sdf.format(cal.getTime());

                cal.add(cal.DATE, -1);  // 하루 전
                preDate = sdf.format(cal.getTime());

                cal.add(cal.DATE, +2);  // 하루 후
                nextDate = sdf.format(cal.getTime());
            }
        }

        mv.addObject("nowDate", nowDate);
        mv.addObject("preDate", preDate);
        mv.addObject("nextDate", nextDate);

        return mv;
    }

}

