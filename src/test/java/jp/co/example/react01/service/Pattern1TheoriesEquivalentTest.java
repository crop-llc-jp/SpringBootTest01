// テスト対象のUserServiceImplと同じパッケージに、このテストクラスを配置します
package jp.co.example.react01.service;

// 期待値と実際値が同じインスタンスか確認するassertSameを読み込みます
import static org.junit.jupiter.api.Assertions.assertSame;
// DBへ接続しない代替オブジェクトを作るmockを読み込みます
import static org.mockito.Mockito.mock;
// モックのメソッドが呼ばれたことを確認するverifyを読み込みます
import static org.mockito.Mockito.verify;
// モックが返す値を設定するwhenを読み込みます
import static org.mockito.Mockito.when;

// 複数のテストデータを順番に渡すStreamを読み込みます
import java.util.stream.Stream;

// 1つのテストメソッドを複数のデータで実行するアノテーションです
import org.junit.jupiter.params.ParameterizedTest;
// 1回分の複数の引数をまとめるArgumentsを読み込みます
import org.junit.jupiter.params.provider.Arguments;
// データ供給メソッドを指定するMethodSourceを読み込みます
import org.junit.jupiter.params.provider.MethodSource;

// モックにするUserDaoインターフェースを読み込みます
import jp.co.example.react01.dao.UserDao;
// ログイン結果として使用するUserクラスを読み込みます
import jp.co.example.react01.entity.User;

/**
 * JUnit 4 の Theories + DataPoint/DataPoints に相当する例です
 */
class Pattern1TheoriesEquivalentTest {

    // @ParameterizedTestを付けると、供給されたテストデータの件数だけ
    // このメソッドが繰り返し実行されます
    // name属性は、VS Codeのテスト結果に表示する各ケースの名前です
    @ParameterizedTest(name = "[{index}] {1} でログインする")
    // loginDataPoints()が、JUnit 4の@DataPoint/@DataPointsに相当します
    @MethodSource("loginDataPoints")
    // userId、userName、userPassには、Argumentsの各値が同じ順番で渡されます
    void DataPointごとにログイン結果を検証する(
            Integer userId, String userName, String userPass) {
        // Arrange（準備）：実際のDBへ接続しないようにUserDaoのモックを作成します
        UserDao userDao = mock(UserDao.class);
        // 作成したモックをコンストラクタからServiceへ渡します
        UserServiceImpl service = new UserServiceImpl(userDao);
        // このテストで返されることを期待するUserを作成します
        User expected = new User(userId, userName, userPass);

        // 指定したユーザー名とパスワードが渡されたら、期待するユーザーを返すよう設定します
        when(userDao.login(userName, userPass)).thenReturn(expected);

        // Act（実行）：テスト対象のloginメソッドを呼び出します
        User actual = service.login(userName, userPass);

        // Assert（検証）：Daoが返したユーザーがそのまま返されることを確認します
        assertSame(expected, actual);

        // UserDao.loginが指定した値で1回呼び出されたことを確認します
        verify(userDao).login(userName, userPass);
    }

    // 1つのArgumentsが1回分のテストデータです
    // 戻り値の順番は、テストメソッドの引数の順番と一致させます
    static Stream<Arguments> loginDataPoints() {
        // Stream.ofで3件のArgumentsを1つのStreamにまとめます
        return Stream.of(
                // 1回目に使用するユーザーID、ユーザー名、パスワードです
                Arguments.of(1, "taro", "pass01"),
                // 2回目に使用するユーザーID、ユーザー名、パスワードです
                Arguments.of(2, "hanako", "pass02"),
                // 3回目に使用するユーザーID、ユーザー名、パスワードです
                Arguments.of(3, "jiro", "pass03"));
    }
}
