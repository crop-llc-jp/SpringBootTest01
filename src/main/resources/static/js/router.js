(function () {
    // React側で扱う画面URLです
    const allowedPaths = ["/", "/login", "/login-result", "/list"];

    // 未定義のURLはログイン画面へ戻します
    function normalizePath(value) {
        if (allowedPaths.indexOf(value) >= 0) {
            return value;
        }
        return "/login";
    }

    // URLから表示する画面名を決めます
    function getView(path) {
        if (path === "/list") {
            return "list";
        }
        if (path === "/login-result") {
            return "loginResult";
        }
        return "login";
    }

    // ブラウザのURLをReact側で変更します
    function pushPath(nextPath) {
        const normalized = normalizePath(nextPath);
        window.history.pushState({}, "", normalized);
        return normalized;
    }

    // App.jsx から使えるようにルーター関数を公開します
    window.React01Router = {
        getView: getView,
        normalizePath: normalizePath,
        pushPath: pushPath
    };
})();
