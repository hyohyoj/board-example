package hyo.boardexample.Service;

import hyo.boardexample.domain.UserAuth;
import hyo.boardexample.mapper.UserAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {
    private final UserAuthMapper userAuthMapper;

    public List<UserAuth> getUserAuthList(String userId) {
        return userAuthMapper.getUserAuthList(userId);
    }

    public int insertUserAuth(UserAuth userAuth) {
        return userAuthMapper.insertUserAuth(userAuth);
    }

    public int deleteUserAuth(UserAuth userAuth) {
        return userAuthMapper.deleteUserAuth(userAuth);
    }

    public int updateUserAuth(UserAuth userAuth) {
        return userAuthMapper.updateUserAuth(userAuth);
    }
}
