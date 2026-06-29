// テスト対象のUserServiceImplと同じパッケージに、このテストクラスを配置します
package jp.co.example.react01.service;

// 期待値と実際値が同じインスタンスか確認するassertSameを読み込みます
import static org.junit.jupiter.api.Assertions.assertSame;
// UserDaoのモックを作成するmockを読み込みます
import static org.mockito.Mockito.mock;
// モックが呼ばれたことを確認するverifyを読み込みます
import static org.mockito.Mockito.verify;
// モックが返す値を設定するwhenを読み込みます
import static org.mockito.Mockito.when;

// 複数のクラス用パラメータを順番に渡すStreamを読み込みます
import java.util.stream.Stream;

// 通常のテストメソッドを示すTestを読み込みます
import org.junit.jupiter.api.Test;
// テストクラス全体をパラメータ化するParameterizedClassを読み込みます
import org.junit.jupiter.params.ParameterizedClass;
// 1回分のコンストラクタ引数をまとめるArgumentsを読み込みます
import org.junit.jupiter.params.provider.Arguments;
// クラス用データ供給メソッドを指定するMethodSourceを読み込みます
import org.junit.jupiter.params.provider.MethodSource;

// DBアクセス部分の代わりにモック化するUserDaoを読み込みます
import jp.co.example.react01.dao.UserDao;
// 期待するログイン結果を表すUserを読み込みます
import jp.co.example.react01.entity.User;

/**
 * JUnit 4 の Parameterized テストランナーに相当する例です
 * テストメソッドではなく、テストクラス全体をパラメータ化します
 */
// parameters()から受け取ったデータごとに、このテストクラスのインスタンスが作られます
@ParameterizedClass
// クラスへ渡すパラメータを作成するメソッドを指定します
@MethodSource("parameters")
class Pattern3ParameterizedClassTest {

    // クラスが受け取った1ケース分のテストデータを保持します
    // parameters()から渡されたユーザーIDを保持します
    private final Integer userId;
    // parameters()から渡されたユーザー名を保持します
    private final String userName;
    // parameters()から渡されたパスワードを保持します
    private final String userPass;

    // JUnitがparameters()の各Argumentsを、このコンストラクタへ順番に渡します
    Pattern3ParameterizedClassTest(Integer userId, String userName, String userPass) {
        // 受け取ったユーザーIDをフィールドへ保存します
        this.userId = userId;
        // 受け取ったユーザー名をフィールドへ保存します
        this.userName = userName;
        // 受け取ったパスワードをフィールドへ保存します
        this.userPass = userPass;
    }

    // このメソッドがJUnitのテストであることを示します
    @Test
    void クラスに渡されたパラメータでログイン結果を検証する() {
        // Arrange（準備）：Daoのモックとテスト対象のServiceを作成します
        UserDao userDao = mock(UserDao.class);
        // モックをコンストラクタからServiceへ渡します
        UserServiceImpl service = new UserServiceImpl(userDao);
        // クラスが保持する値から期待するUserを作成します
        User expected = new User(userId, userName, userPass);
        // 指定した認証情報でDaoが呼ばれた場合にexpectedを返すよう設定します
        when(userDao.login(userName, userPass)).thenReturn(expected);

        // Act（実行）：コンストラクタで受け取った値を使ってログインします
        User actual = service.login(userName, userPass);

        // Assert（検証）：期待するUserが返り、Daoが正しく呼ばれたことを確認します
        assertSame(expected, actual);
        // クラスが保持するユーザー名とパスワードがDaoへ渡されたことを確認します
        verify(userDao).login(userName, userPass);
    }

    // JUnit 4の@Parametersを付けたメソッドに相当するデータ供給メソッドです
    // この例では3件のデータがあるため、テストクラスも3回実行されます
    static Stream<Arguments> parameters() {
        // Stream.ofで3件のクラス用パラメータをまとめます
        return Stream.of(
                // 1個目のテストクラスへ渡す値です
                Arguments.of(1, "taro", "pass01"),
                // 2個目のテストクラスへ渡す値です
                Arguments.of(2, "hanako", "pass02"),
                // 3個目のテストクラスへ渡す値です
                Arguments.of(3, "jiro", "pass03"));
    }
}
