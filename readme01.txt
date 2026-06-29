react02 プログラム説明 import版
================================

このファイルは、現在の react02 の修正内容に合わせた説明です

今回の React 側は、Vite や Create React App を使わずに、
ブラウザ上で Babel standalone を使って JSX を JavaScript に変換しています

また、以前の window.React01Api や window.React01Views のような
グローバル変数連携をやめて、import / export を使う形に変更しています


1. 現在の主な変更点
-------------------

変更前:
  ・api.js、router.js、各 JSX を window.React01... に登録して連携していた
  ・App.jsx は window.React01Api、window.React01Router、window.React01Views を参照していた
  ・react-dom-client.js という補助ファイルがあった
  ・load-jsx.js は import の依存関係を再帰的に調べる複雑な作りだった

変更後:
  ・api.js、router.js、各 JSX は export する
  ・App.jsx、main.jsx、各 JSX は import して使う
  ・React と ReactDOM は react.js からまとめて import する
  ・react-dom-client.js は削除した
  ・load-jsx.js は files 配列の順番どおりに読み込む単純な作りにした


2. 起動方法
-----------

プロジェクトフォルダへ移動します

  cd C:\Users\kutsu\Desktop\Plus\react02

Spring Boot を起動します

  .\mvnw.cmd spring-boot:run

ブラウザで次の URL を開きます

  http://localhost:8080/login


3. React 側の現在のファイル構成
-------------------------------

src/main/resources/static/index.html
  React、ReactDOM、Babel、load-jsx.js を読み込みます

src/main/resources/static/js/load-jsx.js
  JSX / JS ファイルを順番に読み込み、Babel で変換します
  import 先を Blob URL に置き換えて、ブラウザで ES Module として動かします

src/main/resources/static/js/react.js
  HTML で読み込んだ window.React と window.ReactDOM を、
  import できるように export します

src/main/resources/static/js/api.js
  Spring Boot の API を呼び出す関数を export します

src/main/resources/static/js/router.js
  URL 判定と画面遷移用の関数を export します

src/main/resources/static/js/App.jsx
  React アプリ全体の状態管理と画面切り替えを行います

src/main/resources/static/js/main.jsx
  React アプリを root 要素へ表示します

src/main/resources/static/js/views/LoginView.jsx
  ログイン画面を表示します

src/main/resources/static/js/views/LoginResultView.jsx
  ログイン成功後の画面を表示します

src/main/resources/static/js/views/ListView.jsx
  ユーザー一覧画面を表示します


4. index.html の説明
--------------------

プログラム:

  <div id="root"></div>
  <script crossorigin src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
  <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
  <script src="/js/load-jsx.js"></script>

説明:

  <div id="root"></div>
    React の画面を表示する場所です

  react.production.min.js
    React 本体を読み込みます
    読み込むと window.React が使えるようになります

  react-dom.production.min.js
    React を HTML に表示するための ReactDOM を読み込みます
    読み込むと window.ReactDOM が使えるようになります

  babel.min.js
    JSX をブラウザ上で JavaScript に変換するために読み込みます

  /js/load-jsx.js
    自作の読み込み処理です
    JSX ファイルを fetch で取得し、Babel で変換してから実行します

注意:
  index.html では api.js、router.js、App.jsx、main.jsx を直接読み込みません
  それらは load-jsx.js が順番に読み込みます


5. load-jsx.js の説明
---------------------

プログラム:

  const files = [
      "/js/react.js",
      "/js/api.js",
      "/js/router.js",
      "/js/views/LoginView.jsx",
      "/js/views/LoginResultView.jsx",
      "/js/views/ListView.jsx",
      "/js/App.jsx",
      "/js/main.jsx"
  ];

説明:

  files は、変換するファイルの順番です
  import される側を先に書き、import する側を後に書きます

  react.js
    React と ReactDOM を export するため、最初に読み込みます

  api.js
    App.jsx から import されるため、App.jsx より前に読み込みます

  router.js
    App.jsx から import されるため、App.jsx より前に読み込みます

  LoginView.jsx、LoginResultView.jsx、ListView.jsx
    App.jsx から import される画面部品です

  App.jsx
    main.jsx から import されるため、main.jsx より前に読み込みます

  main.jsx
    React アプリの起点になるため、最後に読み込みます


プログラム:

  const importPathPattern = /(\bfrom\s*)(["'])(\.{1,2}\/[^"']+)\2/g;

説明:

  import 文の from "./xxx" の部分を探すための正規表現です
  例えば次のような文字列を見つけます

    from "./react.js"
    from "./App.jsx"
    from "../react.js"


プログラム:

  const moduleUrls = {};

説明:

  変換前のファイルパスと、変換後の Blob URL を対応させるための入れ物です

  例:

    "/js/react.js" -> "blob:http://localhost:8080/..."


プログラム:

  function resolveModulePath(fromFile, importPath) {
      return new URL(importPath, window.location.origin + fromFile).pathname;
  }

説明:

  import の相対パスを、/js/... のような絶対パスに変換します

  例:

    fromFile:   /js/App.jsx
    importPath: ./api.js
    結果:       /js/api.js


プログラム:

  function rewriteImportPaths(source, fromFile) {
      return source.replace(importPathPattern, function (match, prefix, quote, importPath) {
          const modulePath = resolveModulePath(fromFile, importPath);
          return prefix + quote + moduleUrls[modulePath] + quote;
      });
  }

説明:

  ファイル内の import 先を、変換済みの Blob URL に置き換えます

  変換前:

    import { React } from "./react.js";

  変換後のイメージ:

    import { React } from "blob:http://localhost:8080/...";

  ブラウザは .jsx を直接 import できないため、
  Babel で変換した結果を Blob URL として import させます


プログラム:

  function loadFile(file) {
      return fetch(file)
          .then(function (response) {
              if (!response.ok) {
                  throw new Error(file + " の読み込みに失敗しました");
              }
              return response.text();
          })
          .then(function (source) {
              const sourceWithBlobImports = rewriteImportPaths(source, file);
              const result = Babel.transform(sourceWithBlobImports, {
                  filename: file,
                  sourceType: "module",
                  presets: [["react", { runtime: "classic" }]]
              });
              const blob = new Blob([result.code], { type: "text/javascript" });

              moduleUrls[file] = URL.createObjectURL(blob);
          });
  }

説明:

  fetch(file)
    対象ファイルを読み込みます

  response.ok
    読み込みに成功したか確認します

  response.text()
    ファイル内容を文字列として取得します

  rewriteImportPaths(source, file)
    import 先を Blob URL に置き換えます

  Babel.transform(...)
    JSX を通常の JavaScript に変換します
    sourceType: "module" により import / export を含むファイルとして扱います
    runtime: "classic" により JSX は React.createElement 形式へ変換されます

  new Blob(...)
    変換後の JavaScript をブラウザ内の一時ファイルのように扱います

  URL.createObjectURL(blob)
    Blob を import できる URL に変換します


プログラム:

  files.reduce(function (promise, file) {
      return promise.then(function () {
          return loadFile(file);
      });
  }, Promise.resolve()).then(function () {
      const script = document.createElement("script");

      script.type = "module";
      script.src = moduleUrls["/js/main.jsx"];
      document.body.appendChild(script);
  }).catch(function (error) {
      const root = document.getElementById("root");
      root.innerHTML = '<main class="page"><section class="panel"><p class="error">画面の読み込みに失敗しました</p></section></main>';
      console.error(error);
  });

説明:

  files.reduce(...)
    files 配列の順番どおりに loadFile を実行します

  Promise.resolve()
    最初の Promise を作るために使います

  script.type = "module"
    main.jsx の変換後コードを ES Module として実行します

  script.src = moduleUrls["/js/main.jsx"]
    変換済み main.jsx の Blob URL を指定します

  catch(...)
    読み込みや変換に失敗した場合、画面にエラーメッセージを表示します


6. react.js の説明
------------------

プログラム:

  const React = window.React;
  const ReactDOM = window.ReactDOM;

説明:

  index.html で読み込んだ React と ReactDOM を取得します
  UMD 版の React は import ではなく、window.React と window.ReactDOM に入ります


プログラム:

  if (!React) {
      throw new Error("Reactの読み込みに失敗しました");
  }

  if (!ReactDOM) {
      throw new Error("ReactDOMの読み込みに失敗しました");
  }

説明:

  React または ReactDOM が読み込めていない場合、分かりやすいエラーを出します


プログラム:

  const { useEffect, useMemo, useState } = React;

説明:

  React の中から useEffect、useMemo、useState を取り出します
  App.jsx で import して使います


プログラム:

  export {
      React,
      ReactDOM,
      useEffect,
      useMemo,
      useState
  };

説明:

  他のファイルから次のように import できます

    import { React, ReactDOM } from "./react.js";
    import { React, useEffect, useMemo, useState } from "./react.js";

  react-dom-client.js は削除済みです
  ReactDOM も react.js から import します


7. api.js の説明
----------------

プログラム:

  export {
      fetchUsers,
      getCurrentUser,
      login,
      logout
  };

説明:

  Spring Boot の API を呼び出す関数を export します
  App.jsx では次のようにまとめて import しています

    import * as api from "./api.js";

主な関数:

  getCurrentUser()
    GET /api/me を呼び出して、現在ログイン中か確認します

  login(form)
    POST /api/login を呼び出します
    form には userName と userPass が入ります

  fetchUsers()
    GET /api/users を呼び出して、ユーザー一覧を取得します
    401 の場合は未ログインとして扱います

  logout()
    POST /api/logout を呼び出して、セッションを破棄します


8. router.js の説明
-------------------

プログラム:

  const allowedPaths = ["/", "/login", "/login-result", "/list"];

説明:

  React 側で許可する URL の一覧です


プログラム:

  function normalizePath(value) {
      if (allowedPaths.indexOf(value) >= 0) {
          return value;
      }
      return "/login";
  }

説明:

  value が allowedPaths に含まれていれば、その URL を返します
  含まれていない場合は /login を返します


プログラム:

  function getView(path) {
      if (path === "/list") {
          return "list";
      }
      if (path === "/login-result") {
          return "loginResult";
      }
      return "login";
  }

説明:

  URL から表示する画面名を決めます

  /list
    list 画面

  /login-result
    loginResult 画面

  それ以外
    login 画面


プログラム:

  function pushPath(nextPath) {
      const normalized = normalizePath(nextPath);
      window.history.pushState({}, "", normalized);
      return normalized;
  }

説明:

  ブラウザの URL を変更します
  ページ全体の再読み込みは行わず、React 側の画面だけを切り替えます


プログラム:

  export {
      getView,
      normalizePath,
      pushPath
  };

説明:

  App.jsx から次のように import して使います

    import * as router from "./router.js";


9. App.jsx の説明
-----------------

プログラム:

  import { React, useEffect, useMemo, useState } from "./react.js";
  import * as api from "./api.js";
  import * as router from "./router.js";
  import { LoginView } from "./views/LoginView.jsx";
  import { LoginResultView } from "./views/LoginResultView.jsx";
  import { ListView } from "./views/ListView.jsx";

説明:

  React、API、ルーター、各画面コンポーネントを import します
  以前のように window.React01Api や window.React01Views は使いません


プログラム:

  const [path, setPath] = useState(router.normalizePath(window.location.pathname));
  const [sessionChecked, setSessionChecked] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ userName: "", userPass: "" });
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

説明:

  path
    現在の URL を保持します

  sessionChecked
    ログイン状態の確認が終わったかを保持します

  currentUser
    ログイン中のユーザー情報を保持します

  users
    一覧画面に表示するユーザー一覧を保持します

  form
    ログインフォームの入力値を保持します

  message
    エラーメッセージを保持します

  loading
    API 通信中かどうかを保持します


プログラム:

  const view = useMemo(function () {
      return router.getView(path);
  }, [path]);

説明:

  path が変わったときに、表示する画面名を計算します
  router.getView(path) により login、loginResult、list のどれかになります


プログラム:

  api.getCurrentUser()
      .then(function (data) {
          setCurrentUser(data.authenticated ? data.user : null);
      })
      .finally(function () {
          setSessionChecked(true);
      });

説明:

  初回表示時に /api/me を呼び出し、ログイン済みか確認します
  確認が終わったら sessionChecked を true にします


プログラム:

  if (!currentUser && view !== "login") {
      navigate("/login");
      return;
  }

説明:

  未ログインの状態で /list や /login-result を開いた場合、
  ログイン画面へ戻します


プログラム:

  function handleLogin(event) {
      debugger;
      event.preventDefault();
      setLoading(true);
      setMessage("");

      api.login(form)
          .then(function (result) {
              if (!result.ok) {
                  setMessage(result.body.message || "ログインに失敗しました");
                  return;
              }

              setCurrentUser(result.body.user);
              setForm({ userName: "", userPass: "" });
              navigate("/login-result");
          })
          .catch(function () {
              setMessage("通信に失敗しました");
          })
          .finally(function () {
              setLoading(false);
          });
  }

説明:

  ログインボタンを押したときの処理です
  debugger は、開発者ツールを開いているときに処理を一時停止するための行です
  api.login(form) で Spring Boot の /api/login を呼び出します
  成功した場合は currentUser を更新し、/login-result へ移動します


プログラム:

  function loadUsers() {
      setLoading(true);
      setMessage("");

      api.fetchUsers()
          .then(function (result) {
              if (result.status === 401) {
                  setCurrentUser(null);
                  navigate("/login");
                  return;
              }

              setUsers(Array.isArray(result.body) ? result.body : []);
          })
          .catch(function () {
              setMessage("一覧の取得に失敗しました");
          })
          .finally(function () {
              setLoading(false);
          });
  }

説明:

  ユーザー一覧を取得する処理です
  401 が返った場合は未ログインとして扱い、ログイン画面へ戻します


プログラム:

  function logout() {
      setLoading(true);

      api.logout()
          .finally(function () {
              setCurrentUser(null);
              setUsers([]);
              setLoading(false);
              navigate("/login");
          });
  }

説明:

  ログアウト処理です
  Spring Boot 側のセッションを破棄し、
  React 側の currentUser と users も空にします


プログラム:

  {view === "login" && (
      <LoginView ... />
  )}

  {view === "loginResult" && (
      <LoginResultView ... />
  )}

  {view === "list" && (
      <ListView ... />
  )}

説明:

  現在の URL から求めた view に応じて、
  ログイン画面、ログイン結果画面、一覧画面を切り替えます


プログラム:

  export {
      App
  };

説明:

  main.jsx から App を import できるようにしています


10. main.jsx の説明
-------------------

プログラム:

  import { React, ReactDOM } from "./react.js";
  import { App } from "./App.jsx";

説明:

  React と ReactDOM は react.js から import します
  App は App.jsx から import します


プログラム:

  const root = document.getElementById("root");

説明:

  index.html の <div id="root"></div> を取得します


プログラム:

  ReactDOM.createRoot(root).render(<App />);

説明:

  root の中に App コンポーネントを表示します
  ここから React アプリが動き始めます


11. LoginView.jsx の説明
------------------------

プログラム:

  import { React } from "../react.js";

説明:

  JSX 内で React.Fragment を使うため、react.js から React を import します


プログラム:

  function LoginView(props) {
      return (
          <React.Fragment>
              ...
          </React.Fragment>
      );
  }

説明:

  ログイン画面を表示するコンポーネントです
  props.form に入力値、props.message にエラーメッセージ、
  props.loading に通信中状態が入ります
  入力変更処理とログイン処理は App.jsx から props で受け取ります


プログラム:

  export {
      LoginView
  };

説明:

  App.jsx から LoginView を import できるようにしています


12. LoginResultView.jsx の説明
------------------------------

プログラム:

  import { React } from "../react.js";

説明:

  JSX 内で React.Fragment を使うため、react.js から React を import します


プログラム:

  const userName = props.currentUser ? props.currentUser.userName : "";

説明:

  ログイン中ユーザーがいる場合はユーザー名を取り出します
  ユーザー情報がない場合は空文字にします


プログラム:

  <p className="success">{userName}さんがログインしました</p>

説明:

  ログイン成功メッセージを表示します


プログラム:

  export {
      LoginResultView
  };

説明:

  App.jsx から LoginResultView を import できるようにしています


13. ListView.jsx の説明
-----------------------

プログラム:

  import { React } from "../react.js";

説明:

  JSX 内で React.Fragment を使うため、react.js から React を import します


プログラム:

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

説明:

  users が空の場合は「データがありません」と表示します
  users にデータがある場合は、map で 1 件ずつ table の行に変換します


プログラム:

  export {
      ListView
  };

説明:

  App.jsx から ListView を import できるようにしています


14. 現在の読み込みと実行の流れ
-------------------------------

  ブラウザで /login を開く
    ↓
  Spring Boot が index.html を返す
    ↓
  index.html が React、ReactDOM、Babel、load-jsx.js を読み込む
    ↓
  load-jsx.js が files 配列の順番で JS / JSX を読み込む
    ↓
  各ファイルの import 先を Blob URL に置き換える
    ↓
  Babel.transform で JSX を JavaScript に変換する
    ↓
  main.jsx の変換後コードを module script として実行する
    ↓
  main.jsx が App.jsx を import する
    ↓
  App.jsx が api.js、router.js、各 View を import する
    ↓
  ReactDOM.createRoot(root).render(<App />) で画面を表示する


15. import の関係
-----------------

main.jsx
  import { React, ReactDOM } from "./react.js";
  import { App } from "./App.jsx";

App.jsx
  import { React, useEffect, useMemo, useState } from "./react.js";
  import * as api from "./api.js";
  import * as router from "./router.js";
  import { LoginView } from "./views/LoginView.jsx";
  import { LoginResultView } from "./views/LoginResultView.jsx";
  import { ListView } from "./views/ListView.jsx";

LoginView.jsx
  import { React } from "../react.js";

LoginResultView.jsx
  import { React } from "../react.js";

ListView.jsx
  import { React } from "../react.js";


16. 注意点
----------

この構成は、学習用に Vite や Create React App を使わず、
ブラウザ上で JSX を変換して import / export を使えるようにしたものです

通常のブラウザは .jsx ファイルをそのまま import できません
そのため load-jsx.js が Babel で変換し、
変換後の JavaScript を Blob URL として import できるようにしています

新しい JSX ファイルを追加した場合:

  1. 対象ファイルで export する
  2. 使用する側で import する
  3. load-jsx.js の files 配列に、import される側を先に追加する

react-dom-client.js は現在使用していません
ReactDOM は react.js から import してください

