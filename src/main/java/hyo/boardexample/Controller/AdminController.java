package hyo.boardexample.Controller;

import hyo.boardexample.Service.BoardService;
import hyo.boardexample.Service.BoardTypeService;
import hyo.boardexample.Service.LoginService;
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
import java.util.List;

@Controller
@RequestMapping("/admin/**")
@RequiredArgsConstructor
public class AdminController {

    private final BoardTypeService boardTypeService;
    private final BoardService boardService;
    private final LoginService loginService;

    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        System.out.println(e);
        return "/error";
    }

    // 관리자 페이지 내부 기능 권한 체크
    public String adminSessionRedirect(Login loginMember, String redirect, HttpServletRequest request) {
        if(!loginMember.getAuth_code().equals("admin")) {
            HttpSession session = request.getSession();
            session.invalidate();

            request.setAttribute("msg", "관리자 권한이 없습니다.");
            request.setAttribute("url", "/admin");
            return "/alert";
        } else {
            return redirect;
        }
    }

    @GetMapping("/modifyForm")
    public String modifyForm(
            Model model,
            @RequestParam(value = "num") Long typeNo,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            HttpServletRequest request)
    {
        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);

        BoardType boardType = boardTypeService.getBoardType(typeNo);

        model.addAttribute("boardTypeForm", boardType);
        return url;
    }

    @PostMapping("/boardType/modify")
    @ResponseBody
    public int modifyBoardType(@ModelAttribute BoardType boardType) {
        Board board = new Board();
        board.setType_no(boardType.getType_no());
        board.setDelete_yn(boardType.getDelete_yn());

        // 게시판 활성화 및 비활성화 시, 해당 게시글도 같이 변경
        boardService.modifyBoardYn(board);
        return boardTypeService.modifyBoardType(boardType);
    }

    @GetMapping("/insertForm")
    public String insertForm(
            Model model,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            HttpServletRequest request)
    {
        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);

        model.addAttribute("boardTypeForm", new BoardType());

        return url;
    }

    @PostMapping("/boardType/insert")
    @ResponseBody
    public int insertBoardType(@ModelAttribute BoardType boardType) {
        return boardTypeService.insertBoardType(boardType);
    }

    @GetMapping("/getUserList")
    @ResponseBody
    public ModelAndView getUserList() {
        List<Login> userList = null;
        ModelAndView mv = new ModelAndView();

        try {
            userList = loginService.getUserList();

            mv.setViewName("/admin/setUserList");
            mv.addObject("userList", userList);
        } catch (Exception e) {
            mv.setViewName("/error");
            return mv;
        }

        return mv;
    }

}

