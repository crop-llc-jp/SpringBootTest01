(function () {
    // Reactで使用するフックを取り出します
    // import React, { useEffect, useMemo, useState } from "react"; 
    // の代わりに、グローバル変数 React から取り出します
    const { useEffect, useMemo, useState } = React;

    // API通信処理をまとめたオブジェクトを取得します
    const api = window.React01Api;

    // 画面遷移とURL判定の処理を取得します
    const router = window.React01Router;

    // 各画面コンポーネントをまとめたオブジェクトを取得します
    const views = window.React01Views;

    // Reactアプリ全体の状態と画面切替を管理します
    function App() {
        // 現在のURLパスを保持します
        const [path, setPath] = useState(router.normalizePath(window.location.pathname));

        // セッション確認が終わったかどうかを保持します
        const [sessionChecked, setSessionChecked] = useState(false);

        // ログイン中のユーザー情報を保持します
        const [currentUser, setCurrentUser] = useState(null);

        // 一覧画面に表示するユーザー一覧を保持します
        const [users, setUsers] = useState([]);

        // ログインフォームの入力値を保持します
        const [form, setForm] = useState({ userName: "", userPass: "" });

        // 画面に表示するエラーメッセージを保持します
        const [message, setMessage] = useState("");

        // API通信中かどうかを保持します
        const [loading, setLoading] = useState(false);

        // 現在のURLから表示する画面を決めます
        const view = useMemo(function () {
            // path が変わるたびに login / loginResult / list のどれかを取得します
            return router.getView(path);
        }, [path]);

        // ブラウザの戻る・進む操作に対応します
        useEffect(function () {
            const onPopState = function () {
                // ブラウザ履歴の移動後、現在のURLをReactの状態へ反映します
                setPath(router.normalizePath(window.location.pathname));
            };

            // 戻る・進む操作を監視します
            window.addEventListener("popstate", onPopState);

            // Appが破棄される時にイベント登録を解除します
            return function () {
                window.removeEventListener("popstate", onPopState);
            };
        }, []);

        // 初回表示時にログイン状態を確認します
        useEffect(function () {
            // Spring Boot のセッションにログインユーザーがあるか確認します
            api.getCurrentUser()
                .then(function (data) {
                    // ログイン済みならユーザー情報を保存し、未ログインなら null にします
                    setCurrentUser(data.authenticated ? data.user : null);
                })
                .finally(function () {
                    // 成功・失敗に関係なく、確認済みとして画面描画を進めます
                    setSessionChecked(true);
                });
        }, []);

        // 未ログイン時はログイン画面へ戻します
        useEffect(function () {
            // セッション確認が終わるまでは画面遷移を判定しません
            if (!sessionChecked) {
                return;
            }

            // 未ログインでログイン画面以外を開いた場合は、ログイン画面へ戻します
            if (!currentUser && view !== "login") {
                navigate("/login");
                return;
            }

            // ログイン済みでトップURLを開いた場合は、一覧画面へ移動します
            if (currentUser && path === "/") {
                navigate("/list");
            }
        }, [sessionChecked, currentUser, view, path]);

        // 一覧画面を開いたらユーザー一覧を取得します
        useEffect(function () {
            // ログイン済みで list 画面の場合だけ一覧APIを呼び出します
            if (sessionChecked && currentUser && view === "list") {
                loadUsers();
            }
        }, [sessionChecked, currentUser, view]);

        function navigate(nextPath) {
            // URLを変更し、React側の path state も更新します
            setPath(router.pushPath(nextPath));

            // 画面遷移時に古いメッセージを消します
            setMessage("");
        }

        function handleChange(event) {
            // 変更された入力欄の name と value を取得します
            const name = event.target.name;
            const value = event.target.value;

            // userName または userPass の該当項目だけを更新します
            setForm(function (current) {
                return Object.assign({}, current, { [name]: value });
            });
        }

        // ログインフォームの送信処理です
        function handleLogin(event) {
            // 開発者ツールを開いている時、この行でトレース実行を止められます
            debugger;

            // HTMLフォームの通常送信を止め、React側で処理します
            event.preventDefault();

            // ボタン連打を防ぐため、通信中状態にします
            setLoading(true);

            // 前回のエラーメッセージを消します
            setMessage("");

            // 入力されたユーザー名とパスワードをログインAPIへ送ります
            api.login(form)
                .then(function (result) {
                    // ログイン失敗時はメッセージを表示して終了します
                    if (!result.ok) {
                        setMessage(result.body.message || "ログインに失敗しました");
                        return;
                    }

                    // ログイン成功時はユーザー情報を保存します
                    setCurrentUser(result.body.user);

                    // 入力欄を空に戻します
                    setForm({ userName: "", userPass: "" });

                    // ログイン結果画面へ移動します
                    navigate("/login-result");
                })
                .catch(function () {
                    // 通信そのものに失敗した場合のメッセージです
                    setMessage("通信に失敗しました");
                })
                .finally(function () {
                    // 成功・失敗に関係なく通信中状態を解除します
                    setLoading(false);
                });
        }

        // ユーザー一覧をAPIから読み込みます
        function loadUsers() {
            // 一覧取得中の表示に切り替えます
            setLoading(true);

            // 前回のエラーメッセージを消します
            setMessage("");

            // ログイン済みセッションを使って一覧APIを呼び出します
            api.fetchUsers()
                .then(function (result) {
                    // 401 は未ログインなので、ログイン画面へ戻します
                    if (result.status === 401) {
                        setCurrentUser(null);
                        navigate("/login");
                        return;
                    }

                    // APIの結果が配列なら一覧に反映し、それ以外なら空配列にします
                    setUsers(Array.isArray(result.body) ? result.body : []);
                })
                .catch(function () {
                    // 一覧APIの通信に失敗した場合のメッセージです
                    setMessage("一覧の取得に失敗しました");
                })
                .finally(function () {
                    // 成功・失敗に関係なく通信中状態を解除します
                    setLoading(false);
                });
        }

        // セッションを破棄してログイン画面へ戻します
        function logout() {
            // ログアウト中はボタンを無効化します
            setLoading(true);

            // Spring Boot 側のセッションを破棄します
            api.logout()
                .finally(function () {
                    // React側に残っているログイン情報を消します
                    setCurrentUser(null);

                    // 一覧データも消します
                    setUsers([]);

                    // 通信中状態を解除します
                    setLoading(false);

                    // ログイン画面へ戻します
                    navigate("/login");
                });
        }

        if (!sessionChecked) {
            // セッション確認が終わるまでは読み込み表示を出します
            return (
                <main className="page">
                    <section className="panel">
                        <p className="muted">読み込み中...</p>
                    </section>
                </main>
            );
        }

        return (
            <main className="page">
                <section className="panel">
                    {/* ログイン画面を表示します */}
                    {view === "login" && (
                        <views.LoginView
                            form={form}
                            message={message}
                            loading={loading}
                            onChange={handleChange}
                            onLogin={handleLogin}
                        />
                    )}

                    {/* ログイン結果画面を表示します */}
                    {view === "loginResult" && (
                        <views.LoginResultView
                            currentUser={currentUser}
                            loading={loading}
                            onList={function () { navigate("/list"); }}
                            onLogout={logout}
                        />
                    )}

                    {/* ユーザー一覧画面を表示します */}
                    {view === "list" && (
                        <views.ListView
                            users={users}
                            message={message}
                            loading={loading}
                            onReload={loadUsers}
                            onLogout={logout}
                        />
                    )}
                </section>
            </main>
        );
    }

    // main.jsx から使えるようにAppを公開します
    window.React01App = App;
})();
