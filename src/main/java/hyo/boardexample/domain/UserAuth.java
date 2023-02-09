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
public class UserAuth {
    private Long auth_no;
    private String user_id;
    private String auth_code;
    private Long type_no;
    private String delete_yn;
    private String insert_user;
    private LocalDateTime insert_date;
}
