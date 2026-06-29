package jp.co.example.react01.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jp.co.example.react01.entity.User;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String SELECT = "SELECT * FROM users ORDER BY user_id";
    private static final String SELECT_LOGIN = "SELECT * FROM users WHERE user_name = :userName AND user_pass = :userPass";

    // JDBC Templateを使用してデータベース操作を行うためのフィールド
    private final NamedParameterJdbcTemplate jdbcTemplate;

    // コンストラクタでJdbcTemplateを注入
    public UserDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 全件取得するメソッド
    public List<User> findAll() {
        String sql = SELECT;
        List<User> ulist = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
        return ulist;
    }

    // ログイン認証を行うメソッド
    public User login(String userName, String userPass) {
        String sql = SELECT_LOGIN;

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userName", userName);
        param.addValue("userPass", userPass);

        // sql：SQLクエリ文字列
        // param：SQLクエリの入れ替えパラメータを記述
        List<User> resultList 
            = jdbcTemplate.query(sql, 
                                param, 
                                new BeanPropertyRowMapper<User>(User.class));
        
        // 結果が空の場合はnullを返し、そうでない場合は最初の要素を返す
        return resultList.isEmpty() ? null : resultList.get(0);
    }

}
