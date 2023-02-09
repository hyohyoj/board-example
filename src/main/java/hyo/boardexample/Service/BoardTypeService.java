package hyo.boardexample.Service;

import hyo.boardexample.domain.BoardType;
import hyo.boardexample.mapper.BoardTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardTypeService {

    private final BoardTypeMapper boardTypeMapper;

    public List<BoardType> getBoardTypeList(String auth) { return boardTypeMapper.getBoardTypeList(auth); }

    public BoardType getBoardType(Long typeNo) { return boardTypeMapper.getBoardType(typeNo); }

    public int modifyBoardType(BoardType boardType) {
        return boardTypeMapper.modifyBoardType(boardType);
    }

    public int insertBoardType(BoardType boardType) {
        return boardTypeMapper.insertBoardType(boardType);
    }
}
