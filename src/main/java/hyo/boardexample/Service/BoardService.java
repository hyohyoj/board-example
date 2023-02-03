package hyo.boardexample.Service;

import hyo.boardexample.domain.Board;
import hyo.boardexample.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardMapper boardMapper;

    public int boardCount(Board board) {
        return boardMapper.boardCount(board);
    }

//    public List<Board> boardList(Integer page) {
//        return boardMapper.getList(page);
//    }
    public List<Board> boardList(Board board) {
        return boardMapper.getList(board);
    }

    public List<Board> boardAnswerList(Long num) {
        return boardMapper.getAnswerList(num);
    }

    public Board boardOne(Integer num) {
        return boardMapper.getOne(num);
    }

    public int update(Board board) {
        return boardMapper.update(board);
    }
    public int insert(Board board) {
        return boardMapper.insert(board);
    }
    public int delete(Board board) {
        return boardMapper.delete(board);
    }
}
