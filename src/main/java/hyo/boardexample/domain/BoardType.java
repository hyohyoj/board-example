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
public class BoardType {
    private Long type_no;
    private String type_name;
    private String insert_user;
    private LocalDateTime insert_date;
    private String delete_yn;
}
