package hyo.boardexample.Service;

import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.FileInfo;
import hyo.boardexample.mapper.BoardMapper;
import hyo.boardexample.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;

    public int boardCount(Board board) {
        return boardMapper.boardCount(board);
    }

    public List<Board> boardList(Board board) {
        return boardMapper.getList(board);
    }

    public List<Board> boardAnswerList(Integer num) {
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
    public int insertFiles(List<FileInfo> fileList) {
        return fileMapper.insertFile(fileList);
    }
    public int delete(Board board) {
        return boardMapper.delete(board);
    }
    public int deleteAnswer(Board board) {
        return boardMapper.deleteAnswer(board);
    }
}
