package hyo.boardexample.Controller;

import hyo.boardexample.Service.BoardService;
import hyo.boardexample.Service.FileInfoService;
import hyo.boardexample.common.FileUtils;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.FileInfo;
import hyo.boardexample.domain.Login;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadBase;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board/**")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileInfoService fileInfoService;
    private final FileUtils fileUtils;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        System.out.println(e);
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
    public String insert(
            @RequestPart(value="files", required = false) MultipartFile[] files,
            @RequestPart(value="board") Board board) throws IOException
    {
        for (MultipartFile file: files) {
            System.out.println("file name : " + file.getOriginalFilename());
        }
        System.out.println("board info : " + board.getUser_id() + ", " + board.getBoard_title() + ", " + board.getBoard_content());

        String result = "실패";

        int success = boardService.insert(board);

        if(success >= 1) {
            result = "게시글 업로드 성공";

            // insert한 게시글의 board_no 받아옴
            Long boardNo = board.getBoard_no();
            System.out.println("boardNo : " +  boardNo);

            // 해당 게시글 첨부 파일 업로드
            List<FileInfo> fileList = fileUtils.uploadFiles(files, boardNo);
            if(!CollectionUtils.isEmpty(fileList)) {
                success = fileInfoService.insertFiles(fileList);
                if(success >= 1) {
                    result = "파일 업로드 성공";
                }
            }
        }

        return result;
    }

    @PostMapping("/insertAnswer")
    @ResponseBody
    public int insertAnswer(
            @RequestParam(value="user_id", defaultValue = "") String userId,
            @RequestParam(value="board_content", defaultValue = "") String boardContent,
            @RequestParam(value="target") Long target
    ) {
        Board board = new Board();
        board.setUser_id(userId);
        board.setBoard_content(boardContent);
        board.setTarget(target);

        return boardService.insert(board);
    }

    @PostMapping("/update")
    @ResponseBody
    public int update(
            @RequestPart(value="files", required = false) MultipartFile[] files,
            @RequestPart(value="board") Board board) throws IOException, MaxUploadSizeExceededException
    {
        List<Long> fileNumList = board.getFileNumList();

        for (MultipartFile file: files) {
            System.out.println("file name : " + file.getOriginalFilename());
        }
        System.out.println(board.getBoard_no());

        int result = 0;

        int success = boardService.update(board);

        if(success >= 1) {
            result++;

            // 파일이 추가, 삭제, 변경된 경우
            if("Y".equals(board.getChangeYn())) {
                // 전체 삭제 처리
                fileInfoService.deleteFile(board.getBoard_no());

                // 변경되지 않은 기존 파일의 삭제여부를 N으로 변경
                if(!CollectionUtils.isEmpty(fileNumList)) {
                    success = fileInfoService.undeleteFile(fileNumList);
                    if(success >= 1) {
                        result++;
                    }
                }

                // 해당 게시글 첨부 파일 업로드
                List<FileInfo> fileList = fileUtils.uploadFiles(files, board.getBoard_no());
                if(!CollectionUtils.isEmpty(fileList)) {
                    success = fileInfoService.insertFiles(fileList);
                    if(success >= 1) {
                        result++;
                    }
                }
            }
        }
        return result;
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
        model.addAttribute("fileList", fileInfoService.selectFileList(num));
        return "/boards/detail";
    }
}
