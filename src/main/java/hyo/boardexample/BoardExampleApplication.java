package hyo.boardexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "hyo.boardexample")
public class BoardExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardExampleApplication.class, args);
	}

}
