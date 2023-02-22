package hyo.boardexample.Service;

import hyo.boardexample.domain.FileInfo;
import hyo.boardexample.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileInfoService {
    private final FileInfoMapper fileInfoMapper;

    public int insertFiles(List<FileInfo> fileList) {
        return fileInfoMapper.insertFile(fileList);
    }

    public List<FileInfo> selectFileList(Integer board_no) {
        return fileInfoMapper.selectFileList(board_no);
    }

    public FileInfo selectFileDetail(Long file_no) { return fileInfoMapper.selectFileDetail(file_no); }

    public int deleteFile(Long board_no) {
        return fileInfoMapper.deleteFile(board_no);
    }

    public int completeDeleteFile(Long board_no) {
        return fileInfoMapper.completeDeleteFile(board_no);
    }

    public int undeleteFile(List<Long> file_no) {
        return fileInfoMapper.undeleteFile(file_no);
    }

    public List<FileInfo> deleteFileList() {
        return fileInfoMapper.deleteFileList();
    }

    public int deleteFileOne(Long file_no) {
        return fileInfoMapper.deleteFileOne(file_no);
    }

}
