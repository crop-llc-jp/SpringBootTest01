(function () {
    // ログインフォームを表示します
    function LoginView(props) {
        return (
            <React.Fragment>
                <h1>ログイン</h1>
                {props.message && <p className="error">{props.message}</p>}
                <form className="form" onSubmit={props.onLogin}>
                    <label>
                        <span>ユーザー名</span>
                        <input
                            type="text"
                            name="userName"
                            value={props.form.userName}
                            onChange={props.onChange}
                            autoComplete="username"
                        />
                    </label>
                    <label>
                        <span>パスワード</span>
                        <input
                            type="password"
                            name="userPass"
                            value={props.form.userPass}
                            onChange={props.onChange}
                            autoComplete="current-password"
                        />
                    </label>
                    <button type="submit" disabled={props.loading}>
                        {props.loading ? "ログイン中..." : "ログイン"}
                    </button>
                </form>
            </React.Fragment>
        );
    }

    // App.jsx から使えるように画面部品を登録します
    window.React01Views = Object.assign({}, window.React01Views, {
        LoginView: LoginView
    });
})();
