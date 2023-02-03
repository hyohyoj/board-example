package hyo.boardexample.mapper;

import hyo.boardexample.domain.Login;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginMapper {
    Login getUser(Login loginForm);

    int insertUser(Login signUpForm);
}
