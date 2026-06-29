(function () {
    // APIレスポンスを共通の形に整えます
    function parseJsonResponse(response) {
        return response.json().then(function (body) {
            return {
                ok: response.ok,
                status: response.status,
                body: body
            };
        });
    }

    // 現在のログイン状態を確認します
    function getCurrentUser() {
        return fetch("/api/me", { credentials: "same-origin" })
            .then(function (response) {
                return response.json();
            });
    }

    // ログインAPIへフォーム内容を送信します
    function login(form) {
        return fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify(form)
        }).then(parseJsonResponse);
    }

    // ユーザー一覧を取得します
    function fetchUsers() {
        return fetch("/api/users", { credentials: "same-origin" })
            .then(function (response) {
                if (response.status === 401) {
                    return {
                        ok: false,
                        status: response.status,
                        body: []
                    };
                }

                return parseJsonResponse(response);
            });
    }

    // ログアウトAPIを呼び出します
    function logout() {
        return fetch("/api/logout", {
            method: "POST",
            credentials: "same-origin"
        });
    }

    // App.jsx から使えるようにAPI関数を公開します
    window.React01Api = {
        fetchUsers: fetchUsers,
        getCurrentUser: getCurrentUser,
        login: login,
        logout: logout
    };
})();
