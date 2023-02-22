package hyo.boardexample;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing	// Spring Batch 기능 활성화
@EnableScheduling		// Spring Scheduler 기능 활성화
@SpringBootApplication
@MapperScan(basePackages = "hyo.boardexample")
public class BoardExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardExampleApplication.class, args);
	}

}
