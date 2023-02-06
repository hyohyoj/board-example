package hyo.boardexample.Controller;

import hyo.boardexample.Service.BoardService;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.Login;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/board/**")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        return "/error";
    }

    // json 형태로 데이터 반환
//    @GetMapping("/selectList")
//    @ResponseBody
//    public String selectList(@ModelAttribute Board boardModel) {
//
//        List<Board> boardList;
//        int totalCount;
//        JSONObject jo = new JSONObject();
//
//        boardModel.setPage((boardModel.getPage()-1) * 10);
//
//        try {
//            boardList = boardService.boardList(boardModel);
//            totalCount = boardService.boardCount(boardModel);
//
//            jo.put("boardList", boardList);
//            jo.put("totalCount", totalCount);
//        } catch (Exception e) {
//            return "/error";
//        }
//        return jo.toString();
//    }

    // ModelAndView 형태로 데이터가 세팅 된 뷰를 반환
    @GetMapping("/selectList")
    @ResponseBody
    public ModelAndView selectList(@ModelAttribute Board boardModel, @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember) {

        List<Board> boardList = null;
        ModelAndView mv = new ModelAndView();

        boardModel.setPage((boardModel.getPage()-1) * 10);


        try{
            boardList = boardService.boardList(boardModel);

            mv.setViewName("/boards/setSelectList");
            mv.addObject("boardList", boardList);
            mv.addObject("sessionId", loginMember.getUser_id());
        } catch (Exception e) {
            mv.setViewName("/error");
            return mv;
        }
        return mv;
    }

    @GetMapping("/getCount")
    @ResponseBody
    public int getTotalCount(@ModelAttribute Board boardModel) {
        return boardService.boardCount(boardModel);
    }

    @GetMapping("/selectAnswerList")
    @ResponseBody
    public String selectAnswerList(@RequestParam(value="boardNum") Integer num) {

        List<Board> boardList = null;
        JSONObject jo = new JSONObject();

        try{
            boardList = boardService.boardAnswerList(num);
            jo.put("boardAnswerList", boardList);
        } catch (Exception e) {
            return "/error";
        }
        return jo.toString();
    }

    @GetMapping("/insertForm")
    public String insertForm(Model model) {
        model.addAttribute("boardForm", new Board());
        return "/boards/insertForm";
    }

    @PostMapping("/insert")
    @ResponseBody
    public int insert(
            @RequestParam(value="user_id", defaultValue = "") String userId,
            @RequestParam(value="board_title", defaultValue = "") String title,
            @RequestParam(value="board_content", defaultValue = "") String content,
            @RequestParam(value="target", required = false) Long target
    ) {

        Board board = new Board();
        board.setUser_id(userId);
        board.setBoard_title(title);
        board.setBoard_content(content);
        if(target != null) {
            board.setTarget(target);
        }

        return boardService.insert(board);
    }

    @PutMapping("/update")
    @ResponseBody
    public int update(
            @RequestParam(value="boardNum", defaultValue = "1") Long num,
            @RequestParam(value="boardContent", defaultValue = "") String content)
    {
        Board board = new Board();
        board.setBoard_no(num);
        board.setBoard_content(content);

        //System.out.println(boardService.update(board));

        return boardService.update(board);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public int delete(@RequestParam(value="boardNum") Long num) {
        Board board = new Board();
        board.setBoard_no(num);

        boardService.deleteAnswer(board);

        return boardService.delete(board);
    }

    @GetMapping("/detail")
    public String detail(@RequestParam(value="num", defaultValue = "1") Integer num, Model model) {
        model.addAttribute("boardForm", boardService.boardOne(num));
        model.addAttribute("answerList", boardService.boardAnswerList(num));
        return "/boards/detail";
    }
}
