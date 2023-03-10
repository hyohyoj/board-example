package hyo.boardexample.Service;

import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.BoardType;
import hyo.boardexample.domain.FileInfo;
import hyo.boardexample.mapper.BoardMapper;
import hyo.boardexample.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardMapper boardMapper;

    public int boardCount(Board board) {
        return boardMapper.boardCount(board);
    }

    public List<Board> boardList(Board board) {
        return boardMapper.getList(board);
    }

    public List<Board> boardNoticeList(Board board) {
        return boardMapper.getNoticeList(board);
    }

    public List<Board> boardAnswerList(Board board) {
        return boardMapper.getAnswerList(board);
    }

    public Board boardOne(Integer num) {
        return boardMapper.getOne(num);
    }

    public int update(Board board) {
        return boardMapper.update(board);
    }

    public int updateReply(Board board) {
        return boardMapper.updateReply(board);
    }

    public int insert(Board board) {
        return boardMapper.insert(board);
    }

    public int delete(Map<String, Object> map) {
        return boardMapper.delete(map);
    }

    public Map<String, Object> getBoardReplyInfo(Board board) {
        return boardMapper.getBoardReplyInfo(board);
    }

    public int updateBoardReSeq(Map<String, Object> map) {
        return boardMapper.updateBoardReSeq(map);
    }

    public Long getBoardReRef() {
        return boardMapper.getBoardReRef();
    }

    public int deleteAnswer(Map<String, Object> map) {
        return boardMapper.deleteAnswer(map);
    }

    public int modifyBoardYn(Board board) {
        return boardMapper.modifyBoardYn(board);
    }

    public String getKind(Integer num) {
        return boardMapper.getKind(num);
    }
}
