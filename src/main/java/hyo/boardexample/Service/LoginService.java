package hyo.boardexample.Service;

import hyo.boardexample.domain.Login;
import hyo.boardexample.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final LoginMapper loginMapper;

    public Login getUser(Login loginForm) { return loginMapper.getUser(loginForm); }

    public int insertUser(Login signUpForm) { return loginMapper.insertUser(signUpForm); }
}
