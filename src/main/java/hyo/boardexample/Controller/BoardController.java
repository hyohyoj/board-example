package hyo.boardexample.Controller;

import com.nhncorp.lucy.security.xss.XssPreventer;
import hyo.boardexample.Service.BoardService;
import hyo.boardexample.Service.BoardTypeService;
import hyo.boardexample.Service.FileInfoService;
import hyo.boardexample.Service.UserAuthService;
import hyo.boardexample.common.FileUtils;
import hyo.boardexample.common.SessionConstants;
import hyo.boardexample.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board/**")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardTypeService boardTypeService;
    private final FileInfoService fileInfoService;
    private final UserAuthService userAuthService;
    private final FileUtils fileUtils;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        System.out.println(e);
        return "/error";
    }

    /* 게시글 리스트에 이미지 경로 지정해주는 함수 */
    public List<Board> setImageUploadPath(List<Board> boardList) {
        List<FileInfo> fileList = null;

        String extension = "";
        String uploadDate = "";
        String uploadPath = "";

        for (Board board : boardList) {
            // 게시글의 첨부 파일 모두 가져옴
            fileList = fileInfoService.selectFileList(board.getBoard_no().intValue());

            for (FileInfo file : fileList) {
                // 파일 확장자 체크
                extension = FilenameUtils.getExtension(file.getOriginal_name());

                // 파일이 이미지인 경우
                if(extension.equals("jpg") || extension.equals("png")) {
                    // 파일 저장 경로 찾기
                    uploadDate = file.getInsert_time().format(DateTimeFormatter.ofPattern("yyMMdd"));
                    uploadPath = Paths.get("board","image", uploadDate, file.getSave_name()).toString();

                    board.setImageUploadPath(uploadPath);
                    break;
                } else {
                    // 이미지가 없는 경우 임시 이미지 출력
                    uploadPath = "../images/thumbnail.png";
                    board.setImageUploadPath(uploadPath);
                }
            }
        }   // end of for

        return boardList;
    }

    // ModelAndView 형태로 데이터가 세팅 된 뷰를 반환
    @PostMapping("/selectList")
    @ResponseBody
    public ModelAndView selectList(
//            @ModelAttribute Board boardModel, // form-data 형식으로 받아옴
//            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
//            @RequestParam(value = "mode", defaultValue = "text", required = false) String mode
            @RequestBody Board boardModel,      // JSON 형식으로 받아옴
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember
    )
    {
        UserAuth userAuth = new UserAuth();
        userAuth.setUser_id(loginMember.getUser_id());
        userAuth.setType_no(boardModel.getType_no());
        int managerCount = 0;

        List<Board> boardList = null;
        List<Board> boardNoticeList = null;
        List<FileInfo> fileList = null;
        BoardType boardType = null;
        int boardCount = 0;

        ModelAndView mv = new ModelAndView("jsonView"); // json 형태로 데이터 전송

        boardModel.setLimitPage((boardModel.getPage()-1) * 10);

        // 유저 아이디 보내서 권한 테이블 조회
        managerCount = userAuthService.getUserAuthManage(userAuth);

        try{
            boardList = boardService.boardList(boardModel);
            boardCount = boardService.boardCount(boardModel);
            boardType = boardTypeService.getBoardType(boardModel.getType_no());
            boardNoticeList = boardService.boardNoticeList(boardModel);

            // 갤러리 타입일 경우 게시글 목록에 표시할 이미지 불러옴
            if(boardType.getKind().equals("gallery")) {
                boardList = setImageUploadPath(boardList);
                boardNoticeList = setImageUploadPath(boardNoticeList);
            }

            mv.setViewName("/boards/setSelectList");
            mv.addObject("boardList", boardList);                   // 게시글 목록
            mv.addObject("boardNoticeList", boardNoticeList);       // 공지사항 목록
            mv.addObject("sessionId", loginMember.getUser_id());    // 세션 아이디
            mv.addObject("auth", loginMember.getAuth_code());       // 세션 권한
            mv.addObject("managerCount", managerCount);             // 매니저 권한 여부
            mv.addObject("boardCount", boardCount);                 // 총 게시글 수
            mv.addObject("boardKind", boardType.getKind());         // 게시글 타입(gallery, qna)
            mv.addObject("boardPage", boardModel.getPage());        // 현재 페이지
            mv.addObject("mode", boardModel.getMode());                             // 갤러리 정렬 모드(text, grid)
        } catch (Exception e) {
            System.out.println(e + " : 에러 발생");
            mv.setViewName("/error");
            return mv;
        }
        return mv;
    }

    // json 형태로 데이터 반환
//    @GetMapping("/selectList")
//    @ResponseBody
//    public String selectList(@ModelAttribute Board boardModel,
//                             @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember)
//    {
//        UserAuth userAuth = new UserAuth();
//        userAuth.setUser_id(loginMember.getUser_id());
//        userAuth.setType_no(boardModel.getType_no());
//        int managerCount = 0;
//
//        List<Board> boardList = null;
//        List<Board> boardNoticeList = null;
//        List<FileInfo> fileList = null;
//        BoardType boardType = null;
//        int boardCount = 0;
//
//        JSONObject jo = new JSONObject();
//
//        boardModel.setLimitPage((boardModel.getPage()-1) * 10);
//
//        // 유저 아이디 보내서 권한 테이블 조회
//        managerCount = userAuthService.getUserAuthManage(userAuth);
//
//        try {
//            boardList = boardService.boardList(boardModel);
//            boardCount = boardService.boardCount(boardModel);
//            boardType = boardTypeService.getBoardType(boardModel.getType_no());
//            boardNoticeList = boardService.boardNoticeList(boardModel);
//
//            // 갤러리 타입일 경우 게시글 목록에 표시할 이미지 불러옴
//            if(boardType.getKind().equals("gallery")) {
//                boardList = setImageUploadPath(boardList);
//                boardNoticeList = setImageUploadPath(boardNoticeList);
//            }
//
//            jo.put("boardList", boardList);                     // 게시글 목록
//            jo.put("boardNoticeList", boardNoticeList);         // 공지사항 목록
//            jo.put("sessionId", loginMember.getUser_id());      // 세션 아이디
//            jo.put("auth", loginMember.getAuth_code());         // 세션 권한
//            jo.put("managerCount", managerCount);               // 매니저 권한 여부
//            jo.put("boardCount", boardCount);                   // 총 게시글 수
//            jo.put("boardKind", boardType.getKind());           // 게시글 타입(gallery, qna)
//            jo.put("boardPage", boardModel.getPage());          // 현재 페이지
//        } catch (Exception e) {
//            System.out.println(e + " : 에러 발생");
//            return "/error";
//        }
//        return jo.toString();
//    }

    @GetMapping("/getBoardType")
    @ResponseBody
    public ModelAndView getBoardTypeList(@RequestParam(value = "auth", required = false) String auth) {
        List<BoardType> boardTypeList = null;
        ModelAndView mv = new ModelAndView();

        try {
            boardTypeList = boardTypeService.getBoardTypeList(auth);

            mv.setViewName("/boards/setBoardTypeList");
            mv.addObject("typeList", boardTypeList);
            mv.addObject("auth", auth);
        } catch (Exception e) {
            System.out.println(e + " : 에러 발생");
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

//    @GetMapping("/selectAnswerList")
//    @ResponseBody
//    public String selectAnswerList(@RequestParam(value="boardNum") Integer num) {
//
//        List<Board> boardList = null;
//        JSONObject jo = new JSONObject();
//
//        try{
//            boardList = boardService.boardAnswerList(num);
//            jo.put("boardAnswerList", boardList);
//        } catch (Exception e) {
//            System.out.println(e + " : 에러 발생");
//            return "/error";
//        }
//        return jo.toString();
//    }

    @GetMapping("/insertForm")
    public String insertForm(Model model,
                             @RequestParam(value="type_no", defaultValue = "")Long typeNo,
                             @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember)
    {
        List<BoardType> boardTypeList = boardTypeService.getBoardTypeList("user");

        Board board = new Board();
        board.setType_no(typeNo);

        UserAuth userAuth = new UserAuth();
        userAuth.setUser_id(loginMember.getUser_id());
        userAuth.setType_no(typeNo);
        int managerCount = userAuthService.getUserAuthManage(userAuth);

        model.addAttribute("boardForm", board);
        model.addAttribute("typeList", boardTypeList);
        model.addAttribute("managerCount", managerCount);
        return "/boards/insertForm";
    }

    @PostMapping("/insert")
    @ResponseBody
    public String insert(
            @RequestPart(value="files", required = false) MultipartFile[] files,
            MultipartHttpServletRequest multipartHttpServletRequest) throws IOException
    {
        // filter를 이용해 들어온 request를 중간에 가로채서 xss처리 후 반환해줌
        Board board = (Board) multipartHttpServletRequest.getAttribute("board");

        String result = "실패";

        Long boardReRef = boardService.getBoardReRef();
        board.setBoard_re_ref(boardReRef);
        int success = boardService.insert(board);

        if(success >= 1) {
            result = "성공";
            
            // insert한 게시글의 board_no 받아옴
            Long boardNo = board.getBoard_no();
            System.out.println("boardNo : " +  boardNo);

            // 해당 게시글 첨부 파일 업로드
            List<FileInfo> fileList = fileUtils.uploadFiles(files, boardNo);
            if(!CollectionUtils.isEmpty(fileList)) {
                success = fileInfoService.insertFiles(fileList);
                if(success < 1) {
                    result = "실패";
                }
            }
        }

        return result;
    }

    /* 갤러리 게시판 사진 첨부 필수 체크 */
    @PostMapping("/imageCheck")
    @ResponseBody
    public String imageCheck(@RequestPart(value="files", required = false) MultipartFile[] files,
                             @RequestPart(value="board") Board board) throws IOException
    {
        String result = "실패";

        // 첨부파일에 변화가 없을 경우
        if(board.getChangeYn().equals("N")) {
            result = "성공";
        }

        // 해당 게시글 사진파일 첨부 여부 체크
        List<FileInfo> fileList = fileUtils.uploadFiles(files, board.getType_no());
        // 첨부한 파일이 존재하며
        if(!CollectionUtils.isEmpty(fileList)) {
            for (FileInfo file : fileList) {
                // 파일이 이미지일 경우 성공
                if(file.getFileExtension().equals("jpg") || file.getFileExtension().equals("png")) {
                    result = "성공";
                }
            }
        }
        return result;
    }

    @PostMapping("/insertAnswer")
    @ResponseBody
    public int insertAnswer(@RequestBody Board board) {

        Map<String, Object> map = new HashMap<>();

        map = boardService.getBoardReplyInfo(board);

//        for (String key : map.keySet()) {
//            System.out.println(key + " : " + map.get(key));
//        }

        board.setBoard_re_ref(Long.valueOf(String.valueOf(map.get("board_re_ref"))));
        board.setBoard_re_lev(Long.valueOf(String.valueOf(map.get("board_re_lev"))));
        board.setBoard_re_seq(Long.valueOf(String.valueOf(map.get("board_re_seq"))));

        // 게시글 답글을 등록할 때 기존에 등록된 답글의 순서를 증가시키는 쿼리 추가
        boardService.updateBoardReSeq(map);

        return boardService.insert(board);
    }

    @PostMapping("/update")
    @ResponseBody
    public int update(
            @RequestPart(value="files", required = false) MultipartFile[] files,
            @RequestPart(value="board") Board board,
            MultipartHttpServletRequest multipartHttpServletRequest) throws IOException, MaxUploadSizeExceededException
    {
        List<Long> fileNumList = board.getFileNumList();
        board = (Board) multipartHttpServletRequest.getAttribute("board");

        int result = 0;
        int success = 0;

        success += boardService.update(board);
        success += boardService.updateReply(board);

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

        Map<String, Object> map = new HashMap<>();

        map = boardService.getBoardReplyInfo(board);

        // 게시글 답변 삭제
        boardService.deleteAnswer(map);
        // 게시글 첨부 파일 완전 삭제
        fileInfoService.completeDeleteFile(board.getBoard_no());

        return boardService.delete(map);
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
    public String detail(
            @ModelAttribute Board board,
            @RequestParam(value = "num") Integer num,
            @RequestParam(value = "mode", defaultValue = "text", required = false) String mode,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            Model model,
            HttpServletRequest request)
    {
        Board boardForm = boardService.boardOne(num);
        List<Board> answerList = boardService.boardAnswerList(boardForm);
        List<FileInfo> fileList = fileInfoService.selectFileList(num);
        String boardKind = boardService.getKind(num);
        List<BoardType> boardTypeList = boardTypeService.getBoardTypeList("user");

        String boardUser = boardForm.getUser_id();
        String sessionId = loginMember.getUser_id();
        String sessionAuth = loginMember.getAuth_code();

        UserAuth userAuth = new UserAuth();
        userAuth.setUser_id(sessionId);
        userAuth.setType_no(boardForm.getType_no());
        int managerCount = userAuthService.getUserAuthManage(userAuth);

        String extension = "";
        String uploadDate = "";
        String uploadPath = "";

        boolean validation = false;

        if(boardForm.getNotice_yn().equals("Y")) {
            // 공지사항 : 관리자, 작성자인 경우 수정 권한 부여
            validation = sessionAuth.equals("admin") || sessionId.equals(boardUser) ? true : false;
        } else {
            // 일반 게시글 : 관리자, 게시판 매니저, 작성자인 경우 수정 권한 부여
            validation = sessionAuth.equals("admin") || managerCount > 0 || sessionId.equals(boardUser) ? true : false;
        }

        for (FileInfo file : fileList) {
            // 파일 확장자 체크
            extension = FilenameUtils.getExtension(file.getOriginal_name());

            // 파일이 이미지인 경우
            if(extension.equals("jpg") || extension.equals("png")) {
                // 파일 저장 경로 찾기
                uploadDate = file.getInsert_time().format(DateTimeFormatter.ofPattern("yyMMdd"));
                uploadPath = Paths.get("image", uploadDate, file.getSave_name()).toString();
                break;
            }
        }
        if(uploadPath.isEmpty()) {
            // 이미지가 없는 경우 임시 이미지 출력
            uploadPath = "../images/thumbnail.png";
        }

        model.addAttribute("boardForm", boardForm);     // 게시글 상세 내용
        model.addAttribute("answerList", answerList);   // 게시글 답변 목록
        model.addAttribute("fileList", fileList);       // 게시글 첨부 파일 목록
        model.addAttribute("typeList", boardTypeList);  // 게시판 타입 리스트
        model.addAttribute("filePath", uploadPath);     // 이미지 경로
        model.addAttribute("boardKind", boardKind);     // 게시판 종류 (gallery, qna)
        model.addAttribute("validation", validation);   // 권한 인증
        // 페이지 값 유지
        model.addAttribute("type_no", board.getType_no());
        model.addAttribute("selected_page", board.getSelected_page());
        model.addAttribute("keyword", board.getKeyword());
        model.addAttribute("searchContent", board.getSearchContent());
        model.addAttribute("mode", mode);

        return "/boards/detail";
    }

    // 이미지 출력
    @GetMapping(value = "/image/{uploaddate}/{imagename}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> imageSearch(
            @PathVariable("imagename") String imagename,
            @PathVariable("uploaddate") String uploaddate) throws IOException
    {
        String uploadPath = Paths.get("C:", "develop", "upload", uploaddate, imagename).toString();

        InputStream imageStream = new FileInputStream(uploadPath);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }
}
