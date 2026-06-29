package jp.co.example.react01.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jp.co.example.react01.entity.User;
import jp.co.example.react01.form.UserForm;
import jp.co.example.react01.service.UserService;

@RestController
@RequestMapping("/api")
public class LogInController {

    // ユーザー検索やログイン認証を行うサービスです
    private final UserService userService;

    // Spring のDIにより UserService を受け取ります
    public LogInController(UserService userService) {
        this.userService = userService;
    }

    // 現在のログイン状態をReactへ返します
    @GetMapping("/me")
    public CurrentUserResponse me(HttpSession session) {
        // セッションに保存されているログインユーザーを取得します
        User loginUser = (User) session.getAttribute("loginUser");

        // ログイン済みかどうかと、ログインユーザー情報をJSONで返します
        return new CurrentUserResponse(loginUser != null, UserResponse.from(loginUser));
    }

    // ログイン済みユーザーだけがユーザー一覧を取得できます
    @GetMapping("/users")
    public ResponseEntity<?> list(HttpSession session) {
        // 未ログインの場合は 401 Unauthorized を返します
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("ログインしてください"));
        }

        // DBから全ユーザーを取得します
        List<User> list = userService.findAll();

        // Reactへ返す形式に変換します
        List<UserListItem> users = list.stream()
                .map(UserListItem::from)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Reactから送られたユーザー名とパスワードでログインします
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserForm form, HttpSession session) {
        // 入力値が空の場合は 400 Bad Request を返します
        if (form == null || isBlank(form.getUserName()) || isBlank(form.getUserPass())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("ユーザー名とパスワードを入力してください"));
        }

        // DBにユーザー名とパスワードが一致するユーザーがいるか確認します
        User user = userService.login(form.getUserName(), form.getUserPass());

        // 一致するユーザーがいない場合は 401 Unauthorized を返します
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("ユーザー名またはパスワードが間違っています"));
        }

        // ログイン成功したユーザーをセッションに保存します
        session.setAttribute("loginUser", user);

        // Reactへログインユーザー情報を返します
        return ResponseEntity.ok(new LoginResponse(UserResponse.from(user)));
    }

    // ログアウトしてセッションを破棄します
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    // セッションにログインユーザーがあるか確認します
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginUser") != null;
    }

    // null、空文字、空白だけの文字列を未入力として扱います
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // ログイン成功時に返すJSON形式です
    private record LoginResponse(UserResponse user) {
    }

    // ログイン状態確認時に返すJSON形式です
    private record CurrentUserResponse(boolean authenticated, UserResponse user) {
    }

    // パスワードを含めず、画面表示に必要なユーザー情報だけを返します
    private record UserResponse(Integer userId, String userName) {
        private static UserResponse from(User user) {
            if (user == null) {
                return null;
            }
            return new UserResponse(user.getUserId(), user.getUserName());
        }
    }

    // 一覧画面に表示するユーザー情報です
    private record UserListItem(Integer userId, String userName, String userPass) {
        private static UserListItem from(User user) {
            return new UserListItem(user.getUserId(), user.getUserName(), user.getUserPass());
        }
    }

    // エラー時に返すJSON形式です
    private record ErrorResponse(String message) {
    }

}
