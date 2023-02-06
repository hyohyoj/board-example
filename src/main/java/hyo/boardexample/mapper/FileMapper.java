package hyo.boardexample.mapper;

import hyo.boardexample.domain.Board;
import hyo.boardexample.domain.FileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper {
    int insertFile(List<FileInfo> fileList);

    FileInfo selectFileDetail(Long idx);

    int deleteFile(Long boardIdx);

    List<FileInfo> selectAttachList(Long boardIdx);

    int selectFileTotalCount(Long boardIdx);
}
