(function () {
    // JSXは依存順に読み込みます
    const files = [
        "/js/views/LoginView.jsx",
        "/js/views/LoginResultView.jsx",
        "/js/views/ListView.jsx",
        "/js/App.jsx",
        "/js/main.jsx"
    ];

    // JSXを取得し、Babelで通常のJavaScriptへ変換して実行します
    function loadFile(file) {
        return fetch(file)
            .then(function (response) {
                if (!response.ok) {
                    throw new Error(file + " の読み込みに失敗しました");
                }
                return response.text();
            })
            .then(function (source) {
                const result = Babel.transform(source, {
                    filename: file,
                    presets: [["react", { runtime: "classic" }]]
                });
                const script = document.createElement("script");
                script.text = result.code;
                document.body.appendChild(script);
            });
    }

    // すべてのJSXを順番に読み込みます
    files.reduce(function (promise, file) {
        return promise.then(function () {
            return loadFile(file);
        });
    }, Promise.resolve()).catch(function (error) {
        const root = document.getElementById("root");
        root.innerHTML = '<main class="page"><section class="panel"><p class="error">画面の読み込みに失敗しました</p></section></main>';
        console.error(error);
    });
})();
