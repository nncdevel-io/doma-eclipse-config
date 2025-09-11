# doma-eclipse-config サンプルコードとテスト

このディレクトリには、`doma-eclipse-config` プラグインの動作確認用サンプルコードとテストケースが含まれています。

## サンプルプロジェクト

### `examples/doma-sample-project/`

Doma2を使用した典型的なJavaプロジェクトのサンプルです。このプロジェクトは以下を含みます：

- **pom.xml**: Doma依存関係を含むMavenプロジェクト設定
- **User.java**: Domaエンティティクラス
- **UserDao.java**: DomaのDAOインターフェース
- **SQLファイル**: `src/main/resources/META-INF/` 下のSQLファイル
- **.classpath**: プラグインが修正対象とするクラスパス設定ファイル

### プラグインの動作確認手順

1. サンプルプロジェクトをEclipseにインポート
2. doma-eclipse-configプラグインを実行（ツールバーのDomaアイコンクリック）
3. `.classpath`ファイルが以下のように変更されることを確認：

**変更前:**
```xml
<classpathentry excluding="**" kind="src" output="target/classes" path="src/main/resources">
```

**変更後:**
```xml
<classpathentry including="**/*.script|**/*.sql" kind="src" output="target/classes" path="src/main/resources">
```

## テストケース

### `test/io/nncdevel/domaeclipseconfig/handlers/`

#### ConfigHandlerTest.java
完全な単体テストスイート（Mockitoを使用）:
- Maven プロジェクト検出のテスト
- Doma プロジェクト検出のテスト（POMファイル経由）
- Doma プロジェクト検出のテスト（.factorypath 経由）
- OS固有のパス生成テスト
- クラスパス修正ロジックのテスト

#### DomaConfigUtilsTest.java
Eclipse依存関係なしの軽量テスト（CI環境で実行可能）:
- OS固有のパス生成テスト
- Doma依存関係検出のテスト
- .factorypath検出のテスト
- 文字列置換ロジックのテスト
- クラスパスファイル処理のテスト

### テストデータ

#### `test/resources/sample-projects/`

テスト用のサンプルプロジェクト構造:

- **doma-maven-project/**: Doma依存関係を含むpom.xmlを持つプロジェクト
- **non-doma-project/**: Doma依存関係を含まない通常のMavenプロジェクト
- **doma-factorypath-project/**: .factorypath経由でDomaを検出するプロジェクト

## テスト実行方法

### JUnit実行（Eclipse内）
1. テストクラスを右クリック
2. "Run As" → "JUnit Test" を選択

### Maven実行（コマンドライン）
```bash
mvn test
```

## 品質向上のポイント

### テストカバレッジ
- ✅ プロジェクト検出ロジック
- ✅ OS固有の処理
- ✅ ファイル操作
- ✅ エラーハンドリング
- ✅ 境界値テスト

### テストされる主要メソッド
- `isMavenProject()`: Maven プロジェクト判定
- `isDomaProject()`: Doma プロジェクト判定
- `isDomaProjectByPom()`: POM経由のDoma検出
- `isDomaProjectByFactoryPath()`: .factorypath経由のDoma検出
- `getKeyInFactoryPathEntry()`: OS固有パス生成
- 文字列置換ロジック

### エラーケースの検証
- null値処理
- 空ディレクトリ処理
- 存在しないファイルの処理
- I/O例外の処理

## 継続的品質向上

### 新機能追加時のテスト作成ガイドライン
1. 新しいpublicメソッドには対応するテストメソッドを作成
2. エラーケースとハッピーパスの両方をテスト
3. OS固有の処理がある場合は各OS向けのテストを追加
4. ファイル操作がある場合は一時ファイルを使用してクリーンアップを確実に実行

### 推奨テスト命名規則
- `test[メソッド名]_[条件]_[期待結果]()`
- 例: `testIsDomaProject_WithDomaDependency_ReturnsTrue()`