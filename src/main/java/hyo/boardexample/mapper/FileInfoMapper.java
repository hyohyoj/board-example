package hyo.boardexample.mapper;

import hyo.boardexample.domain.FileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoMapper {
    int insertFile(List<FileInfo> fileList);

    FileInfo selectFileDetail(Long fileNo);

    int deleteFile(Long boardNo);

    List<FileInfo> selectFileList(Integer boardNo);

    int selectFileTotalCount(Long boardNo);

    public int undeleteFile(List<Long> fileNo);
}
