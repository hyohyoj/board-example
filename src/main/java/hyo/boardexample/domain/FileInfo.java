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
    private Long fileNo;
    private Long boardNo;
    private String originalName;
    private String saveName;
    private Long size;
    private String deleteYn;
    private LocalDateTime insertDate;
    private LocalDateTime deleteDate;
}
