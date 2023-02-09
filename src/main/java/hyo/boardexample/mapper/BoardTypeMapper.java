package hyo.boardexample.mapper;

import hyo.boardexample.domain.BoardType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardTypeMapper {
    List<BoardType> getBoardTypeList(String auth);
    BoardType getBoardType(Long typeNo);
    int modifyBoardType(BoardType boardType);

    int insertBoardType(BoardType boardType);
}
