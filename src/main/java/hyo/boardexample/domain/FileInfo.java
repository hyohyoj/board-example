package hyo.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private Long file_no;
    private Long board_no;
    private String original_name;
    private String save_name;
    private Long size;
    private String delete_yn;
    private LocalDateTime insert_time;
    private LocalDateTime delete_time;
    private String fileExtension;
}
