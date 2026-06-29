package jp.co.example.react01.dao;

import java.util.List;

import jp.co.example.react01.entity.User;

public interface UserDao {

    public List<User> findAll();

    public User login(String userName, String userPass);

}
