package hyo.boardexample.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long board_no;
    private String user_id;
    private String board_title;
    private String board_content;

    // response (서버->클라이언트) 전달 시 @JsonFormat 사용
    // request (클라이언트->서버) 전달 시 @DateTimeFormat 사용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime insert_date;
    private Long target = null;
    private Long type_no;
    private String delete_yn;
    private String notice_yn;

    private String type_name;
    private int page;
    private int limitPage;
    private String keyword;
    private String searchContent;
    private String changeYn;
    private List<Long> fileNumList;
    private String selected_page;
    private String imageUploadPath;

}
