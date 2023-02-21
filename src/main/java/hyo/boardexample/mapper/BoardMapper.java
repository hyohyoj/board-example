package hyo.boardexample.mapper;

import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.BoardType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface BoardMapper {
    int boardCount(Board board); // 곧 생성할 BoardMapper.xml 첫번째 sql 문의 id 와 같음.
    
    List<Board> getList(Board board);

    List<Board> getNoticeList(Board board);

    List<Board> getAnswerList(Board board);

    Board getOne(Integer num);

    int update(Board board);

    int updateReply(Board board);

    int insert(Board board);

    int delete(Map<String, Object> map);

    Map<String, Object> getBoardReplyInfo(Board board);

    int updateBoardReSeq(Map<String, Object> map);

    Long getBoardReRef();

    int deleteAnswer(Map<String, Object> map);

    int modifyBoardYn(Board board);

    String getKind(Integer num);
}
