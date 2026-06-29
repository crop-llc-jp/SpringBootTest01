react01 プログラム説明
======================

このプロジェクトは、Spring Boot と React を使用したログインプログラムです
もともとは Spring Boot + Thymeleaf で画面を表示していましたが、
現在は Thymeleaf を使わず、Spring Boot は API と静的ファイル配信、
React は画面表示を担当する構成になっています


1. 起動方法
-----------

プロジェクトフォルダへ移動します

  cd C:\Users\kutsu\Desktop\Plus\react01

Spring Boot を起動します

  .\mvnw.cmd spring-boot:run

ブラウザで次の URL を開きます

  http://localhost:8080/login

停止する場合は、起動中のターミナルで Ctrl + C を押します


2. 全体構成
-----------

Spring Boot 側:
  ・React 画面を表示するための index.html を配信します
  ・ログイン、ログアウト、ログイン状態確認、ユーザー一覧取得の API を提供します
  ・PostgreSQL の users テーブルからユーザー情報を取得します
  ・ログイン済みユーザーは HttpSession に保存します

React 側:
  ・ログイン画面、ログイン結果画面、一覧画面を表示します
  ・fetch を使って Spring Boot の /api/... にアクセスします
  ・画面遷移は React 側で URL を切り替えながら行います


3. 主なURL
----------

画面URL:
  /login
    ログイン画面を表示します

  /login-result
    ログイン成功後の結果画面を表示します

  /list
    ログイン後、ユーザー一覧画面を表示します

API URL:
  GET /api/me
    現在ログインしているユーザーがいるか確認します

  POST /api/login
    ユーザー名とパスワードでログインします

  GET /api/users
    ログイン済みの場合、ユーザー一覧を取得します

  POST /api/logout
    セッションを破棄してログアウトします


4. ファイルごとの内容
---------------------

pom.xml
  Maven の設定ファイルです
  Spring Boot、JDBC、Web MVC、PostgreSQL、Lombok、テスト用ライブラリなどの依存関係を管理しています
  Thymeleaf は使用しないため、spring-boot-starter-thymeleaf は削除されています

src/main/resources/application.properties
  Spring Boot の設定ファイルです
  アプリケーション名、PostgreSQL の接続先、ユーザー名、パスワードを設定しています

  現在の DB 設定:
    URL: jdbc:postgresql://localhost:5432/plusdb
    ユーザー名: plususer
    パスワード: plusPass


5. Spring Boot 側のファイル
---------------------------

src/main/java/jp/co/example/react01/React01Application.java
  Spring Boot アプリケーションの起動クラスです
  main メソッドから SpringApplication.run(...) を呼び出して、アプリケーションを起動します

src/main/java/jp/co/example/react01/ServletInitializer.java
  WAR ファイルとして外部 Tomcat などに配置する場合に使用される初期化クラスです
  通常の spring-boot:run で起動する場合も、残しておいて問題ありません

src/main/java/jp/co/example/react01/controller/SpaController.java
  React の画面を表示するためのコントローラです
  /, /login, /login-result, /list にアクセスされた場合、
  すべて /index.html に forward します
  これにより、実際の画面切り替えは React 側で行えます

src/main/java/jp/co/example/react01/controller/LogInController.java
  ログイン関連 API を担当する REST コントローラです
  @RestController と @RequestMapping("/api") が付いているため、
  返却内容は HTML ではなく JSON になります

  主な処理:
    GET /api/me
      セッションに loginUser が入っているか確認します
      ログイン済みなら authenticated: true とユーザー情報を返します
      未ログインなら authenticated: false を返します

    POST /api/login
      React から送られた userName と userPass を受け取ります
      UserService の login メソッドを呼び出し、DB に一致するユーザーがあるか確認します
      成功した場合は session.setAttribute("loginUser", user) でセッションに保存します
      失敗した場合は 401 とエラーメッセージを返します

    GET /api/users
      セッションに loginUser があるか確認します
      未ログインなら 401 を返します
      ログイン済みなら UserService の findAll メソッドで全ユーザーを取得して返します

    POST /api/logout
      session.invalidate() でセッションを破棄します
      ログアウト後は 204 No Content を返します

src/main/java/jp/co/example/react01/form/UserForm.java
  ログイン時に React から送られる JSON を受け取るためのフォームクラスです
  userName と userPass を持っています

src/main/java/jp/co/example/react01/entity/User.java
  users テーブルの 1 レコードを表すエンティティクラスです
  userId、userName、userPass を持っています

src/main/java/jp/co/example/react01/service/UserService.java
  ユーザー操作のサービスインターフェースです
  findAll と login を定義しています

src/main/java/jp/co/example/react01/service/UserServiceImpl.java
  UserService の実装クラスです
  Controller から呼ばれ、DAO に処理を渡します
  現在は主に UserDao の findAll と login を呼び出しています

src/main/java/jp/co/example/react01/dao/UserDao.java
  DB 操作用 DAO のインターフェースです
  findAll と login を定義しています

src/main/java/jp/co/example/react01/dao/UserDaoImpl.java
  UserDao の実装クラスです
  NamedParameterJdbcTemplate を使って PostgreSQL に SQL を実行します

  実行している SQL:
    SELECT * FROM users ORDER BY user_id
      ユーザー一覧を取得します

    SELECT * FROM users WHERE user_name = :userName AND user_pass = :userPass
      ログイン認証用に、ユーザー名とパスワードが一致するユーザーを検索します

src/test/java/jp/co/example/react01/React01ApplicationTests.java
  Spring Boot のテストクラスです
  contextLoads により、Spring のアプリケーションコンテキストが起動できるか確認します


6. React 側のファイル
---------------------

src/main/resources/static/index.html
  React アプリの入口になる HTML ファイルです
  Spring Boot から静的ファイルとして配信されます

  読み込んでいるもの:
    React
    ReactDOM
    @babel/standalone
    styles.css
    api.js
    router.js
    load-jsx.js

  このプロジェクトではビルドツールを追加せずに JSX を使うため、
  load-jsx.js が .jsx ファイルを順番に読み込み
  @babel/standalone で JavaScript に変換してから実行します

src/main/resources/static/styles.css
  React 画面の見た目を定義する CSS ファイルです
  ページ全体、パネル、フォーム、ボタン、テーブル、エラーメッセージ
  などのスタイルを設定しています

src/main/resources/static/js/api.js
  Spring Boot API との通信処理をまとめたファイルです
  React の画面や App.jsx は、直接 fetch を書く代わりに
  このファイルの関数を呼び出します

  主な関数:
    getCurrentUser()
      GET /api/me を呼び出します

    login(form)
      POST /api/login を呼び出します
      form には userName と userPass が入ります

    fetchUsers()
      GET /api/users を呼び出します

    logout()
      POST /api/logout を呼び出します

src/main/resources/static/js/router.js
  画面遷移と URL 判定をまとめたファイルです

  主な関数:
    normalizePath(value)
      許可している URL か確認し、不正な URL の場合は /login にします

    getView(path)
      URL から表示する画面名を決めます
      /login は login、/login-result は loginResult、/list は list になります

    pushPath(nextPath)
      window.history.pushState を使ってブラウザの URL を変更します

src/main/resources/static/js/load-jsx.js
  JSX ファイルを読み込んで実行するためのファイルです
  index.html から読み込まれた後、
  LoginView.jsx、LoginResultView.jsx、ListView.jsx、App.jsx、main.jsx
  の順番で取得します
  取得した JSX は 
  @babel/standalone の 
    Babel.transform で React.createElement 形式に変換し、
    script タグとして画面に追加して実行します
    この処理により、JSX を書いたままでも、
    ブラウザ上では通常の JavaScript として実行できます

src/main/resources/static/js/App.jsx
  React アプリ全体の中心になるファイルです
  ログイン状態、入力フォーム、ユーザー一覧、
  読み込み状態、エラーメッセージなどを
   useState で管理します

  主な役割:
    ・起動時に /api/me でログイン状態を確認します
    ・未ログインで /list や /login-result に
    　アクセスした場合、/login に戻します
    ・ログイン処理を api.login に依頼します
    ・一覧取得を api.fetchUsers に依頼します
    ・ログアウト処理を api.logout に依頼します
    ・現在の URL に応じて
    　 LoginView、LoginResultView、ListView を切り替えます

src/main/resources/static/js/main.jsx
  React アプリを起動するファイルです
  index.html の <div id="root"></div> に App.jsx の
  React コンポーネントを表示します

src/main/resources/static/js/views/LoginView.jsx
  ログイン画面の表示だけを担当する React コンポーネントです
  ユーザー名、パスワードの入力欄、ログインボタン、エラーメッセージを表示します
  入力変更やログイン実行の処理は App.jsx から props で受け取ります

src/main/resources/static/js/views/LoginResultView.jsx
  ログイン成功後の結果画面を表示する React コンポーネントです
  「○○さんがログインしました」というメッセージ、一覧へボタン、
  ログアウトボタンを表示します

src/main/resources/static/js/views/ListView.jsx
  ユーザー一覧画面を表示する React コンポーネントです
  ユーザー一覧テーブル、再読み込みボタン、ログアウトボタンを表示します
  users 配列は App.jsx から props として受け取ります


7. プログラムの連携
-------------------

画面表示の連携:

  ブラウザ
    ↓
  http://localhost:8080/login にアクセス
    ↓
  Spring Boot の SpaController
    ↓
  src/main/resources/static/index.html を返す
    ↓
  index.html が
  React、Babel、CSS、api.js、
  router.js、load-jsx.js を読み込む
    ↓
  load-jsx.js が JSX ファイルを順番に読み込み、Babel で変換して実行する
    ↓
  main.jsx が App.jsx を root に表示する
    ↓
  App.jsx が現在の URL を見て LoginView.jsx を表示する


ログイン処理の連携:

  ユーザーが LoginView.jsx のフォームに入力
    ↓
  ログインボタンを押す
    ↓
  App.jsx の handleLogin が実行される
    ↓
  api.js の login(form) を呼び出す
    ↓
  POST /api/login に JSON を送信する
    ↓
  LogInController の login メソッドが実行される
    ↓
  UserServiceImpl.login を呼び出す
    ↓
  UserDaoImpl.login を呼び出す
    ↓
  PostgreSQL の users テーブルを検索する
    ↓
  ユーザーが見つかった場合、LogInController が HttpSession に loginUser を保存する
    ↓
  React にログインユーザー情報を JSON で返す
    ↓
  App.jsx が currentUser を更新し、/login-result に遷移する
    ↓
  LoginResultView.jsx がログイン成功メッセージを表示する


一覧表示の連携:

  LoginResultView.jsx で「一覧へ」ボタンを押す
    ↓
  App.jsx が /list に遷移する
    ↓
  App.jsx が api.js の fetchUsers() を呼び出す
    ↓
  GET /api/users を実行する
    ↓
  LogInController の list メソッドがセッションを確認する
    ↓
  ログイン済みなら UserServiceImpl.findAll を呼び出す
    ↓
  UserDaoImpl.findAll が users テーブルから全件取得する
    ↓
  React にユーザー一覧を JSON で返す
    ↓
  App.jsx が users を更新する
    ↓
  ListView.jsx がテーブルに一覧を表示する


ログアウト処理の連携:

  ログアウトボタンを押す
    ↓
  App.jsx の logout が実行される
    ↓
  api.js の logout() を呼び出す
    ↓
  POST /api/logout を実行する
    ↓
  LogInController の logout メソッドが session.invalidate() を実行する
    ↓
  React 側で currentUser と users を空にする
    ↓
  /login に遷移する
    ↓
  LoginView.jsx が表示される


8. セッションの考え方
---------------------

このプログラムでは、ログイン成功時に 
Spring Boot 側の HttpSession に loginUser を保存しています

  session.setAttribute("loginUser", user)

ログイン状態の確認や一覧取得では、この loginUser がセッションに存在するかを見ています

  loginUser がある:
    ログイン済み

  loginUser がない:
    未ログイン

ログアウト時は session.invalidate() によりセッション全体を破棄します


9. 注意点
---------

このプロジェクトでは JSX を使うために @babel/standalone を index.html で読み込んでいます
load-jsx.js が JSX を classic runtime で変換するため、
react/jsx-runtime の import が生成されず、通常の script として実行できます
そのため npm install や npm start は不要で、Spring Boot の起動だけで画面を確認できます

ただし、本格的な開発や公開用アプリでは、
Vite などのビルドツールを使って JSX を事前に JavaScript へ変換する構成が一般的です

また、現在のログイン処理は学習用のシンプルな実装です
パスワードは平文で照合しています
実際のシステムでは、パスワードをハッシュ化し、
Spring Security などを使って認証を実装することが推奨されます

