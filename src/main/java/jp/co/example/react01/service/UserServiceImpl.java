package jp.co.example.react01.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.example.react01.dao.UserDao;
import jp.co.example.react01.entity.User;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    // コンストラクタでDIコンテナからUserDaoを取得
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User login(String userName, String userPass) {
        return userDao.login(userName, userPass);
    }

}
