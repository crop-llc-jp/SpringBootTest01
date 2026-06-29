package jp.co.example.react01.service;

import java.util.List;

import jp.co.example.react01.entity.User;

public interface UserService {

    public List<User> findAll();

    public User login(String userName, String userPass);

}
