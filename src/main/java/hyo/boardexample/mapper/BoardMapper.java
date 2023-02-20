package hyo.boardexample.mapper;

import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.BoardType;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    int delete(Board board);

    Board getBoardReplyInfo(Board board);

    int updateBoardReSeq(Board board);

    Long getBoardReRef();

    int deleteAnswer(Board board);

    int modifyBoardYn(Board board);

    String getKind(Integer num);
}
