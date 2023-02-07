package hyo.boardexample.common;

import hyo.boardexample.domain.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// @Bean과 달리 직접 작성한 클래스를 스프링 컨테이너에 등록하는 데 사용됨.
@Component
public class FileUtils {

    private final String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

    /** 업로드 경로 */
    private final String uploadPath = Paths.get("C:", "develop", "upload", today).toString();

    /**
     * 서버에 생성할 파일명을 처리할 랜덤 문자열 반환
     * @return 랜덤 문자열
     */
    private final String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 서버에 첨부 파일을 생성하고, 업로드 파일 목록 반환
     * @param files    - 파일 Array
     * @param boardNo - 게시글 번호
     * @return 업로드 파일 목록
     */
    public List<FileInfo> uploadFiles(MultipartFile[] files, Long boardNo) {

        int count = 0;
        // 파일이 비어있을 경우 비어있는 리스트 반환
        for (MultipartFile file : files) {
            if(file.getSize() < 1) {
                count++;
            }
        }
        if(count == files.length) {
            return Collections.emptyList();
        }

        List<FileInfo> fileList = new ArrayList<>();

        // uploadPath에 해당하는 디렉터리가 존재하지 않으면, 부모 디렉터리를 포함한 모든 디렉터리를 생성
        File dir = new File(uploadPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        for(MultipartFile file : files) {
            try {
                // 파일 확장자
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

                // 서버에 저장할 파일명 (랜덤 문자열 + 확장자)
                final String saveName = getRandomString() + "." + extension;

                // 업로드 경로에 파일 생성
                File target = new File(uploadPath, saveName);
                file.transferTo(target);

                FileInfo fileInfo = new FileInfo();
                fileInfo.setBoard_no(boardNo);
                fileInfo.setOriginal_name(file.getOriginalFilename());
                fileInfo.setSave_name(saveName);
                fileInfo.setSize(file.getSize());

                if(!file.getOriginalFilename().equals("")) {
                    fileList.add(fileInfo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } // end of for

        return fileList;
    }

}
