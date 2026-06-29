// テスト対象のUserServiceImplと同じパッケージに、このテストクラスを配置します
package jp.co.example.react01.service;

// 期待値と実際値が同じインスタンスか確認するassertSameを読み込みます
import static org.junit.jupiter.api.Assertions.assertSame;
// UserDaoのモックを作成するmockを読み込みます
import static org.mockito.Mockito.mock;
// UserDao.loginの呼び出しを確認するverifyを読み込みます
import static org.mockito.Mockito.verify;
// モックが返す値を設定するwhenを読み込みます
import static org.mockito.Mockito.when;

// 複数のFixtureを順番に渡すStreamを読み込みます
import java.util.stream.Stream;

// Fixtureごとにテストを繰り返すParameterizedTestを読み込みます
import org.junit.jupiter.params.ParameterizedTest;
// Fixtureを作るメソッドを指定するMethodSourceを読み込みます
import org.junit.jupiter.params.provider.MethodSource;

// DBアクセス部分の代わりにモック化するUserDaoを読み込みます
import jp.co.example.react01.dao.UserDao;
// 期待するログイン結果を表すUserを読み込みます
import jp.co.example.react01.entity.User;

/**
 * 入力値と期待値をFixtureオブジェクトにまとめる例です
 */
class Pattern2FixtureTest {

    // Fixtureを1件ずつ受け取り、Fixtureの件数だけテストを繰り返します
    // {0}にはLoginFixture.toString()の戻り値が表示されます
    @ParameterizedTest(name = "[{index}] {0}")
    // loginFixtures()からLoginFixtureオブジェクトを受け取ります
    @MethodSource("loginFixtures")
    // fixtureにはloginFixtures()が返すLoginFixtureが1件ずつ渡されます
    void Fixtureごとにログイン結果を検証する(LoginFixture fixture) {
        // Arrange（準備）：Daoをモックにすることで、DBに依存しない単体テストにします
        UserDao userDao = mock(UserDao.class);
        // モックを使用するUserServiceImplを作成します
        UserServiceImpl service = new UserServiceImpl(userDao);

        // Fixtureに入っている値から、期待するUserオブジェクトを作成します
        User expected = fixture.expectedUser();
        // Fixtureの認証情報でDaoが呼ばれた場合にexpectedを返すよう設定します
        when(userDao.login(fixture.userName(), fixture.userPass())).thenReturn(expected);

        // Act（実行）：Fixtureのユーザー名とパスワードでログインします
        User actual = service.login(fixture.userName(), fixture.userPass());

        // Assert（検証）：期待するUserが返り、Daoが正しい引数で呼ばれたことを確認します
        assertSame(expected, actual);
        // Fixtureのユーザー名とパスワードがDaoへ渡されたことを確認します
        verify(userDao).login(fixture.userName(), fixture.userPass());
    }

    // テストの入力値と期待値をLoginFixtureとしてまとめて供給します
    // ケースを増やす場合は、ここにLoginFixtureを追加します
    static Stream<LoginFixture> loginFixtures() {
        // Stream.ofで3件のFixtureを1つのStreamにまとめます
        return Stream.of(
                // 1回目に使用するFixtureです
                new LoginFixture(1, "taro", "pass01"),
                // 2回目に使用するFixtureです
                new LoginFixture(2, "hanako", "pass02"),
                // 3回目に使用するFixtureです
                new LoginFixture(3, "jiro", "pass03"));
    }

    // recordを使うと、テストデータを保持するだけのFixtureを簡潔に定義できます
    // userId、userName、userPassのアクセサーも自動生成されます
    private record LoginFixture(Integer userId, String userName, String userPass) {

        // Fixtureに保存した値から、検証に使用するUserを作成します
        User expectedUser() {
            // recordの3項目をUserのコンストラクタへ渡します
            return new User(userId, userName, userPass);
        }

        // パラメータ化テストの実行結果を読みやすくするための表示名です
        // Object.toString()をこのFixture専用の表示へ上書きします
        @Override
        public String toString() {
            // 例として「taro のFixture」という文字列を返します
            return userName + " のFixture";
        }
    }
}
