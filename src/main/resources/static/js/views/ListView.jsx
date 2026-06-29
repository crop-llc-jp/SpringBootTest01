(function () {
    // ユーザー一覧画面を表示します
    function ListView(props) {
        return (
            <React.Fragment>
                <div className="title-row">
                    <h1>一覧表示画面テーブル</h1>
                    <div className="actions">
                        <button
                            type="button"
                            className="secondary"
                            onClick={props.onReload}
                            disabled={props.loading}
                        >
                            再読み込み
                        </button>
                        <button
                            type="button"
                            className="secondary"
                            onClick={props.onLogout}
                            disabled={props.loading}
                        >
                            ログアウト
                        </button>
                    </div>
                </div>

                {props.message && <p className="error">{props.message}</p>}
                {props.loading && <p className="muted">読み込み中...</p>}

                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>名前</th>
                                <th>パスワード</th>
                            </tr>
                        </thead>
                        <tbody>
                            {props.users.length === 0 ? (
                                <tr>
                                    <td colSpan="3" className="empty">データがありません</td>
                                </tr>
                            ) : (
                                props.users.map(function (user) {
                                    return (
                                        <tr key={user.userId}>
                                            <td>{user.userId}</td>
                                            <td>{user.userName}</td>
                                            <td>{user.userPass}</td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>
            </React.Fragment>
        );
    }

    // App.jsx から使えるように画面部品を登録します
    window.React01Views = Object.assign({}, window.React01Views, {
        ListView: ListView
    });
})();
