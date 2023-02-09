package hyo.boardexample.mapper;

import hyo.boardexample.domain.UserAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuthMapper {
    List<UserAuth> getUserAuthList(String userId);

    int insertUserAuth(UserAuth userAuth);

    int deleteUserAuth(UserAuth userAuth);

    int updateUserAuth(UserAuth userAuth);
}
