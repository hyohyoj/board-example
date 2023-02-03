package hyo.boardexample.Controller;

import hyo.boardexample.Service.BoardService;
import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.Login;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board/**")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/selectList")
    @ResponseBody
    public String selectList(
//            @RequestParam(value="page", defaultValue = "1") Integer page,
//            @RequestParam(value="keyword", defaultValue = "") String keyword,
//            @RequestParam(value="searchContent", defaultValue = "") String searchContent
            @ModelAttribute Board boardModel
    ) {

        boardModel.setPage((boardModel.getPage()-1) * 10);

//        System.out.println("keyword : " + boardModel.getKeyword() + ", searchContent : " + boardModel.getSearchContent() + ", page : " + boardModel.getPage());
//        List<Board> boardList = boardService.boardList((page-1) * 10);
        List<Board> boardList = boardService.boardList(boardModel);
        int totalCount = boardService.boardCount(boardModel);

        JSONObject jo = new JSONObject();
        jo.put("boardList", boardList);
        jo.put("totalCount", totalCount);

        //System.out.println(jo.toString());
        return jo.toString();
    }

    @GetMapping("/selectAnswerList")
    @ResponseBody
    public String selectAnswerList(@RequestParam(value="boardNum") Long num) {

        List<Board> boardList = boardService.boardAnswerList(num);

        JSONObject jo = new JSONObject();
        jo.put("boardAnswerList", boardList);

        //System.out.println(jo.toString());
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

        return boardService.delete(board);
    }

    @GetMapping("/detail")
    public String detail(@RequestParam(value="num", defaultValue = "1") Integer num, Model model) {
        model.addAttribute("boardForm", boardService.boardOne(num));
        return "/boards/detail";
    }
}
