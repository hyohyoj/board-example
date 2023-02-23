package hyo.boardexample.job;

import hyo.boardexample.Service.FileInfoService;
import hyo.boardexample.domain.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final FileInfoService fileInfoService;

    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("testJob")
                .incrementer(new RunIdIncrementer())
                .start(this.fileDeleteStep())
                .build();

    }

    @Bean
    public Step fileDeleteStep(){
        return stepBuilderFactory.get("fileDeleteStep")
                .tasklet((contribution, chunkContext) -> {

                    log.warn("============ 파일 삭제 배치 스케줄러 시작 ============");

                    // 삭제 할 파일 리스트 받아옴
                    List<FileInfo> fileInfoList = fileInfoService.deleteFileList();
                    int success;

                    if(fileInfoList == null) {
                        log.warn("삭제할 파일 리스트 존재하지 않음");
                        return RepeatStatus.FINISHED;
                    }

                    for (FileInfo fileInfo : fileInfoList) {
                        success = 0;

                        // 파일 저장 경로 찾기
                        String uploadDate = fileInfo.getInsert_time().format(DateTimeFormatter.ofPattern("yyMMdd"));
                        String uploadPath = Paths.get("C:", "develop", "upload", uploadDate).toString();

                        File file = new File(uploadPath, fileInfo.getSave_name());

                        if(file.exists()) {
                            log.warn(uploadPath + " 폴더 내 " + fileInfo.getSave_name() +  " 파일 존재!");
                            file.delete();
                            success = fileInfoService.deleteFileOne(fileInfo.getFile_no());

                            if(success == 1) {
                                log.warn("파일 삭제 완료");
                            }
                        } else {
                            log.warn("파일 존재하지 않음");
                        }
                    }

                    log.warn("============ 파일 삭제 배치 스케줄러 종료 ============");

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
