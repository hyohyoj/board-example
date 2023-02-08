package hyo.boardexample.Controller;

import com.nhncorp.lucy.security.xss.XssPreventer;
import hyo.boardexample.Service.BoardService;
import hyo.boardexample.Service.FileInfoService;
import hyo.boardexample.common.FileUtils;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.BoardType;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
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
    public ModelAndView selectList(
            @ModelAttribute Board boardModel,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember
    ) {

        List<Board> boardList = null;
        ModelAndView mv = new ModelAndView();

        boardModel.setPage((boardModel.getPage()-1) * 10);
        if(boardModel.getType_no() != null) {
            System.out.println(boardModel.getType_no());
        }

        try{
            boardList = boardService.boardList(boardModel);

            mv.setViewName("/boards/setSelectList");
            mv.addObject("boardList", boardList);
            mv.addObject("sessionId", loginMember.getUser_id());
            mv.addObject("auth", loginMember.getAuth_code());
        } catch (Exception e) {
            mv.setViewName("/error");
            return mv;
        }
        return mv;
    }

    @GetMapping("/getBoardType")
    @ResponseBody
    public ModelAndView getBoardTypeList(@RequestParam(value = "auth", required = false) String auth) {
        List<BoardType> boardTypeList = null;
        ModelAndView mv = new ModelAndView();

        try {
            boardTypeList = boardService.getBoardTypeList();

            mv.setViewName("/boards/setBoardTypeList");
            mv.addObject("typeList", boardTypeList);
            mv.addObject("auth", auth);
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
        System.out.println("convert board info : " + board.getUser_id() + ", " + XssPreventer.escape(board.getBoard_title()) + ", " + XssPreventer.escape(board.getBoard_content()));

        // xss escape 처리 (multipartResolver 처리 한 form-data는 lucy-xss-filter가 먹히지 않아서 따로 처리해줘야 함)
        board.setBoard_title(XssPreventer.escape(board.getBoard_title()));
        board.setBoard_content(XssPreventer.escape(board.getBoard_content()));

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

        // xss escape 처리 (multipartResolver 처리 한 form-data는 lucy-xss-filter가 먹히지 않아서 따로 처리해줘야 함)
        board.setBoard_title(XssPreventer.escape(board.getBoard_title()));
        board.setBoard_content(XssPreventer.escape(board.getBoard_content()));

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

        // 게시글 답변 삭제
        boardService.deleteAnswer(board);
        // 게시글 첨부 파일 완전 삭제
        fileInfoService.completeDeleteFile(board.getBoard_no());

        return boardService.delete(board);
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam(value = "fileNo", required = false) final Long fileNo, HttpServletResponse response) {
        if(fileNo == null) throw new RuntimeException("올바르지 않은 접근입니다.");

        FileInfo fileInfo = fileInfoService.selectFileDetail(fileNo);
        if(fileInfo == null || "Y".equals(fileInfo.getDelete_yn())) {
            throw new RuntimeException("파일 정보를 찾을 수 없습니다.");
        }

        // 파일 저장 경로 찾기
        String uploadDate = fileInfo.getInsert_time().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uploadPath = Paths.get("C:", "develop", "upload", uploadDate).toString();

        String fileName = fileInfo.getOriginal_name();
        File file = new File(uploadPath, fileInfo.getSave_name());

        try {
            byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            response.setContentType("application/octet-stream");
            response.setContentLength(data.length);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");

            response.getOutputStream().write(data); // 파일 다운로드 시작
            response.getOutputStream().flush();     // 파일 다운로드 완료
            response.getOutputStream().close();     // 버퍼 정리 후 닫음
        } catch (IOException e) {
            throw new RuntimeException(e + " : 파일 다운로드에 실패하였습니다.");
        } catch (Exception e) {
            throw new RuntimeException(e + " : 시스템에 문제가 발생하였습니다.");
        }

    }

    @GetMapping("/detail")
    public String detail(@RequestParam(value="num", defaultValue = "1") Integer num, Model model) {
        model.addAttribute("boardForm", boardService.boardOne(num));
        model.addAttribute("answerList", boardService.boardAnswerList(num));
        model.addAttribute("fileList", fileInfoService.selectFileList(num));
        return "/boards/detail";
    }
}
