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

// Providerから複数のデータを返すStreamを読み込みます
import java.util.stream.Stream;

// Providerへ現在のテスト実行情報を渡すExtensionContextを読み込みます
import org.junit.jupiter.api.extension.ExtensionContext;
// 1つのメソッドを複数データで実行するParameterizedTestを読み込みます
import org.junit.jupiter.params.ParameterizedTest;
// 1回分の複数の引数をまとめるArgumentsを読み込みます
import org.junit.jupiter.params.provider.Arguments;
// 独自データProviderのインターフェースを読み込みます
import org.junit.jupiter.params.provider.ArgumentsProvider;
// 使用するProviderを指定するArgumentsSourceを読み込みます
import org.junit.jupiter.params.provider.ArgumentsSource;
// テストメソッドの引数定義を表すParameterDeclarationsを読み込みます
import org.junit.jupiter.params.support.ParameterDeclarations;

// DBアクセス部分の代わりにモック化するUserDaoを読み込みます
import jp.co.example.react01.dao.UserDao;
// 期待するログイン結果を表すUserを読み込みます
import jp.co.example.react01.entity.User;

/**
 * JUnit 4 の ParametersSuppliedBy に相当する例です
 */
class Pattern4ArgumentsSourceTest {

    // ArgumentsProviderが返したデータの件数だけテストを繰り返します
    @ParameterizedTest(name = "[{index}] {1} でログインする")
    // JUnit 4の@ParametersSuppliedByに相当するProviderクラスを指定します
    @ArgumentsSource(LoginArgumentsProvider.class)
    // Providerが返す3つの値が、次の3引数へ同じ順番で渡されます
    void Providerが供給した値でログイン結果を検証する(
            Integer userId, String userName, String userPass) {
        // Arrange（準備）：実際のDaoの代わりにMockitoのモックを使用します
        UserDao userDao = mock(UserDao.class);
        // 作成したモックをコンストラクタからServiceへ渡します
        UserServiceImpl service = new UserServiceImpl(userDao);
        // Providerから受け取った値で期待するUserを作成します
        User expected = new User(userId, userName, userPass);
        // 指定した認証情報でDaoが呼ばれた場合にexpectedを返すよう設定します
        when(userDao.login(userName, userPass)).thenReturn(expected);

        // Act（実行）：Providerから受け取った値でログインします
        User actual = service.login(userName, userPass);

        // Assert（検証）：戻り値とDaoの呼び出し内容を確認します
        assertSame(expected, actual);
        // Providerから受け取ったユーザー名とパスワードがDaoへ渡されたことを確認します
        verify(userDao).login(userName, userPass);
    }

    // 複数のテストクラスから再利用したい場合は、Providerを別ファイルに分離できます
    // JUnitが生成できるように、引数なしコンストラクタを持つstaticクラスにします
    // finalを付け、このProviderクラスが継承されないことを示します
    static final class LoginArgumentsProvider implements ArgumentsProvider {

        // JUnit 6で推奨されているArgumentsProviderの実装形式です
        // parametersにはテスト側の引数情報、contextには実行中テストの情報が渡されます
        // ArgumentsProviderのメソッドを実装していることを示します
        @Override
        // 複数のArgumentsをStreamとしてJUnitへ返します
        public Stream<? extends Arguments> provideArguments(
                ParameterDeclarations parameters, ExtensionContext context) {
            // 1つのArgumentsが、テストメソッド1回分の引数になります
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
}
