# doma-eclipse-config

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-0.0.3-blue.svg)]()

Doma2プロジェクト用のEclipse IDEプラグインです。MavenベースのDoma2プロジェクトのクラスパス設定を自動化し、SQLファイルとスクリプトファイルがEclipse内で正しく認識されるようにビルドパスを調整します。

## 概要

### Doma2とは
[Doma2](https://doma.readthedocs.io/)は、Java用の軽量なO/Rマッピングフレームワークです。SQLファイルを外部に配置し、コンパイル時にアノテーションプロセッサを使用してDAOの実装クラスを自動生成することが特徴です。

### このプラグインの目的
Doma2プロジェクトでは、SQLファイル（`.sql`）やスクリプトファイル（`.script`）を`src/main/resources`に配置します。しかし、Eclipseのデフォルト設定では、これらのファイルがビルドパスから除外されることがあり、IDEでの開発体験が損なわれます。

本プラグインは、この問題を解決するために：
- ワークスペース内のDoma2 Mavenプロジェクトを自動検出
- `.classpath`ファイルを修正してSQLファイルとスクリプトファイルをビルドパスに含める
- 開発者が手動でクラスパス設定を変更する手間を削減

### 主な機能
- **自動プロジェクト検出**: `pom.xml`の依存関係または`.factorypath`ファイルを基にDoma2プロジェクトを識別
- **クラスパス自動修正**: `excluding="**"`を`including="**/*.script|**/*.sql"`に変更
- **クロスプラットフォーム対応**: Windows、Linux、macOSで動作
- **非破壊的処理**: 既存のプロジェクト設定を安全に更新

## 前提条件

このプラグインを使用するには、以下の環境が必要です：

### 必要なソフトウェア
- **Eclipse IDE**: Eclipse IDE for Java Developers（バージョン4.6以降推奨）
- **Java**: Java 8以降（JavaSE-1.8以上）
- **Maven**: Apache Maven 3.6以降
- **Doma2**: プロジェクトでDoma2フレームワークを使用

### 対応OS
- Windows 10/11
- Ubuntu/Debian Linux
- macOS

### Eclipse設定要件
- PDE（Plug-in Development Environment）がインストールされていること（開発時のみ）
- ワークスペースにMavenプロジェクトがインポートされていること

## ビルド方法

Eclipse の PDE(Plug-in Development Environment) を利用して以下の手順でビルドします。

### ステップ1: Eclipseでプロジェクトをインポート
1. Eclipse IDEを起動します
2. `File` > `Import...` を選択
3. `General` > `Existing Projects into Workspace` を選択して `Next`
4. `Select root directory` でこのプロジェクトのルートディレクトリを指定
5. プロジェクトが選択されていることを確認して `Finish`

### ステップ2: プラグインをエクスポート
1. プロジェクトを右クリックし `Export...` を選択
2. `Plug-in Development` > `Deployable plug-ins and fragments` を選択して `Next`
3. プラグインが選択されていることを確認
4. `Destination` タブで出力先ディレクトリを指定
5. `Finish` を押すと `plugins/io.nncdevel.doma-eclipse-config_0.0.3.jar` が生成されます

> **注意**: ビルドプロセスには数分かかる場合があります。Eclipseが応答しなくなったように見えても、処理が完了するまでお待ちください。

## インストール

## インストール

生成されたJARファイルをEclipseにインストールする方法は複数あります。

### 方法1: dropinsフォルダーを使用（推奨）
1. 生成された `io.nncdevel.doma-eclipse-config_0.0.3.jar` をコピー
2. Eclipseインストールディレクトリの `dropins` フォルダーに配置
   ```
   /path/to/eclipse/dropins/io.nncdevel.doma-eclipse-config_0.0.3.jar
   ```
3. Eclipseを再起動
4. メインツールバーにDomaアイコン ![](icons/doma.png) が表示されることを確認

### 方法2: Install New Softwareを使用
1. プラグインエクスポート時に「Update site archive」を選択してzipファイルを生成
2. Eclipse で `Help` > `Install New Software...` を選択
3. `Add...` > `Archive...` を選択してzipファイルを指定
4. インストールウィザードに従ってインストール
5. Eclipseを再起動

### インストール確認
インストールが成功すると：
- メインツールバーに小さなDomaアイコンが表示される
- `Help` > `About Eclipse IDE` > `Installation Details` でプラグインが確認できる

### トラブルシューティング
**アイコンが表示されない場合**:
1. `Window` > `Show View` > `Error Log` でエラーを確認
2. Eclipseを `-clean` オプションで起動
3. プラグインが正しいディレクトリに配置されているか確認

## 使い方

## 使い方

### 基本的な使用手順

1. **Doma2プロジェクトの準備**
   - MavenプロジェクトでDoma2の依存関係が設定されていること
   - プロジェクトがEclipseワークスペースにインポートされていること

2. **プラグインの実行**
   - Eclipseメインツールバーの Doma アイコン ![](icons/doma.png) をクリック
   - プラグインがワークスペース内のDoma2プロジェクトを自動検出
   - 該当プロジェクトの `.classpath` ファイルを自動更新

3. **結果の確認**
   - 処理完了後、変更されたプロジェクト名が表示されるダイアログが表示
   - プロジェクトをリフレッシュ（F5）して変更を反映

### Doma2プロジェクトの検出条件

プラグインは以下の方法でDoma2プロジェクトを検出します：

#### 方法1: pom.xml の依存関係をチェック
```xml
<dependency>
    <groupId>org.seasar.doma</groupId>
    <artifactId>doma-core</artifactId>
    <version>2.44.1</version>
</dependency>
```

#### 方法2: .factorypath ファイルをチェック
```xml
<?xml version="1.0" encoding="UTF-8"?>
<factorypath>
    <factorypathentry kind="VARJAR" id="M2_REPO/org/seasar/doma/doma-processor/2.44.1/doma-processor-2.44.1.jar"/>
</factorypath>
```

### クラスパス変更の詳細

プラグインは `.classpath` ファイルの以下の変更を行います：

**変更前:**
```xml
<classpathentry excluding="**" kind="src" output="target/classes" path="src/main/resources">
    <attributes>
        <attribute name="maven.pomderived" value="true"/>
    </attributes>
</classpathentry>
```

**変更後:**
```xml
<classpathentry including="**/*.script|**/*.sql" kind="src" output="target/classes" path="src/main/resources">
    <attributes>
        <attribute name="maven.pomderived" value="true"/>
    </attributes>
</classpathentry>
```

この変更により、SQLファイルとスクリプトファイルがEclipseのビルドパスに含まれ、IDE内でファイルの編集、シンタックスハイライト、オートコンプリートなどの機能が利用できるようになります。

### 実践例

#### サンプルプロジェクトでの動作確認

1. `examples/doma-sample-project/` をEclipseにインポート
2. プロジェクトにDoma2の依存関係が含まれていることを確認
3. ツールバーのDomaアイコンをクリック
4. 成功ダイアログで「doma-sample-project」が表示されることを確認
5. `.classpath`ファイルが上記のように変更されていることを確認

## Doma2特有の設定例

### 典型的なDoma2プロジェクト構造

```
my-doma-project/
├── pom.xml                          # Doma2の依存関係を含む
├── .classpath                       # プラグインが修正対象とするファイル
├── .factorypath                     # アノテーションプロセッサ設定（オプション）
└── src/
    └── main/
        ├── java/
        │   └── com/example/
        │       ├── entity/
        │       │   └── User.java    # @Entityアノテーション
        │       └── dao/
        │           └── UserDao.java # @Daoアノテーション
        └── resources/
            └── META-INF/
                └── com/example/dao/
                    ├── UserDao/
                    │   ├── selectAll.sql
                    │   ├── selectById.sql
                    │   └── insert.sql
                    └── UserDao.sql
```

### pom.xml設定例

```xml
<properties>
    <doma.version>2.44.1</doma.version>
</properties>

<dependencies>
    <!-- Doma2 Core -->
    <dependency>
        <groupId>org.seasar.doma</groupId>
        <artifactId>doma-core</artifactId>
        <version>${doma.version}</version>
    </dependency>
    
    <!-- Doma2 Processor -->
    <dependency>
        <groupId>org.seasar.doma</groupId>
        <artifactId>doma-processor</artifactId>
        <version>${doma.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.seasar.doma</groupId>
                        <artifactId>doma-processor</artifactId>
                        <version>${doma.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### エンティティクラスの例

```java
package com.example.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
    public String name;
    
    public String email;
}
```

### DAOインターフェースの例

```java
package com.example.dao;

import java.util.List;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import com.example.entity.User;

@Dao
public interface UserDao {
    
    @Select
    List<User> selectAll();
    
    @Select
    User selectById(Integer id);
    
    @Insert
    int insert(User user);
}
```

### SQLファイルの例

`src/main/resources/META-INF/com/example/dao/UserDao/selectAll.sql`:
```sql
SELECT 
    id,
    name,
    email
FROM 
    users
ORDER BY 
    id
```

`src/main/resources/META-INF/com/example/dao/UserDao/selectById.sql`:
```sql
SELECT 
    id,
    name,
    email
FROM 
    users
WHERE 
    id = /* id */1
```

### プラグイン適用前後の比較

#### 適用前の問題
- SQLファイルがEclipseのPackage Explorerで見つからない
- SQLファイルの編集時にシンタックスハイライトが効かない
- ファイル検索でSQLファイルが検索対象から除外される

#### 適用後の改善
- SQLファイルがプロジェクトツリーで正常に表示される
- SQLエディタでシンタックスハイライトとフォーマットが利用可能
- Ctrl+Shift+R でSQLファイルの検索が可能
- リソースファイルとして正しく認識される

## トラブルシューティング

### よくある問題と解決方法

#### 1. プラグインアイコンが表示されない
**症状**: ツールバーにDomaアイコンが表示されない

**解決方法**:
- Eclipseを完全に再起動
- `Help` > `About Eclipse IDE` > `Installation Details` でプラグインが有効になっているか確認
- Eclipseを `-clean` オプションで起動: `eclipse -clean`
- Error Logを確認: `Window` > `Show View` > `Error Log`

#### 2. "No Doma projects found" と表示される
**症状**: プラグインを実行しても対象プロジェクトが見つからない

**解決方法**:
- プロジェクトの `pom.xml` に `org.seasar.doma` の依存関係があることを確認
- プロジェクトがMavenプロジェクトとして認識されているか確認
- プロジェクトをリフレッシュ（F5）
- `.factorypath` ファイルが存在する場合、内容を確認

#### 3. クラスパスが変更されない
**症状**: プラグイン実行後も `.classpath` ファイルが変更されない

**解決方法**:
- `.classpath` ファイルが読み取り専用になっていないか確認
- プロジェクトが適切なMaven構造を持っているか確認
- `src/main/resources` ディレクトリが存在することを確認

#### 4. Eclipse が応答しなくなる
**症状**: プラグイン実行中にEclipseがフリーズする

**解決方法**:
- プラグイン処理は数秒から数分かかる場合があるため、しばらく待つ
- 多数のプロジェクトがある場合、処理時間が長くなる
- メモリ不足の場合は、Eclipse起動時の `-Xmx` パラメータを増加

#### 5. Windows でのパス区切り文字の問題
**症状**: Windowsで `.factorypath` の検出が正しく動作しない

**解決方法**:
- プラグインはOS固有のパス区切り文字を自動検出するため、通常は問題なし
- `.factorypath` ファイルの内容を確認し、パスが正しい形式であることを確認

### ログとデバッグ情報

#### Error Logの確認方法
1. `Window` > `Show View` > `Other...`
2. `General` > `Error Log` を選択
3. プラグイン関連のエラーメッセージを確認

#### 詳細なログ出力
プラグインは以下の情報をコンソールに出力します：
- 検出されたMavenプロジェクトの数
- Doma2プロジェクトとして判定されたプロジェクト名
- クラスパス変更の成功/失敗

### サポートとコミュニティ

問題が解決しない場合：
1. [GitHub Issues](https://github.com/nncdevel-io/doma-eclipse-config/issues) で既存の問題を検索
2. 新しいIssueを作成して問題を報告
3. 以下の情報を含めて報告してください：
   - Eclipse のバージョン
   - Java のバージョン
   - OS とバージョン
   - プロジェクト構造のサンプル
   - Error Log の内容

## テストとサンプルコード

品質向上のため、単体テストとサンプルコードを追加しました：

### テスト実行
```bash
# Eclipse内でJUnitテストを実行
右クリック → "Run As" → "JUnit Test"

# 軽量テストの実行（Eclipse依存なし）
DomaConfigUtilsTest.java を実行
```

### サンプルプロジェクト
`examples/doma-sample-project/` にDoma2を使用した完全なサンプルプロジェクトがあります。
このプロジェクトでプラグインの動作確認ができます。

詳細な情報は [TESTING.md](TESTING.md) を参照してください。

## コード品質とCI

このプロジェクトは以下の品質向上施策を導入しています：

### 継続的インテグレーション
- GitHub Actions による自動ビルドとテスト
- Eclipse依存のない軽量テストの自動実行
- 静的解析ツールによるコード品質チェック

### 静的解析ツール
- **Checkstyle**: コーディング規約の自動チェック
- **PMD**: コード品質とベストプラクティスの検証
- 設定ファイル: `.github/checkstyle-config.xml`, `.github/pmd-ruleset.xml`

### コードリファクタリング
- 重複コードの削除とユーティリティクラスの抽出
- 例外処理の改善とログ出力の統一
- メソッドの可視性とJavadocの改善

## コントリビューション（貢献）

このプロジェクトへの貢献を歓迎します！以下のガイドラインに従ってご参加ください。

### 開発環境のセットアップ

1. **必要なツール**
   - Eclipse IDE for Eclipse Committers（PDE含む）
   - Java 8以上
   - Git

2. **プロジェクトのクローン**
   ```bash
   git clone https://github.com/nncdevel-io/doma-eclipse-config.git
   cd doma-eclipse-config
   ```

3. **Eclipse での開発**
   - Eclipse でプロジェクトをインポート
   - PDE が有効になっていることを確認
   - "Run As" > "Eclipse Application" でデバッグ用Eclipse を起動可能

### 貢献の方法

#### バグ報告
1. [GitHub Issues](https://github.com/nncdevel-io/doma-eclipse-config/issues) で既存の報告を検索
2. 新しいIssue を作成し、以下を含める：
   - 再現手順
   - 期待される動作
   - 実際の動作
   - 環境情報（Eclipse バージョン、OS など）
   - エラーメッセージやスクリーンショット

#### 機能提案
1. Issue を作成して機能提案を説明
2. 実装前にメンテナーと議論
3. 必要に応じて設計ドキュメントを作成

#### コード貢献
1. **Fork & Branch**
   ```bash
   # プロジェクトを fork
   git checkout -b feature/your-feature-name
   ```

2. **コーディング規約**
   - Java の標準的なコーディング規約に従う
   - メソッドには適切なJavadoc を記述
   - テストケースを含める（可能な場合）

3. **テスト**
   - 変更内容をテスト
   - 既存のテストが通ることを確認
   - 新機能には対応するテストを追加

4. **Pull Request**
   - 明確なタイトルと説明
   - 関連するIssue をリンク
   - 変更内容のスクリーンショット（UI変更の場合）

### テストガイドライン

#### 単体テスト
- `test/` ディレクトリにテストクラスを配置
- Eclipse 依存のないロジックは `DomaConfigUtilsTest` で検証
- Eclipse API を使う部分は `ConfigHandlerTest` で検証

#### 動作テスト
1. サンプルプロジェクトでの動作確認
2. 複数のOS での検証（Windows、Linux、macOS）
3. 異なるDoma バージョンでのテスト

### リリースプロセス

1. **バージョン管理**
   - セマンティックバージョニングを使用（MAJOR.MINOR.PATCH）
   - `META-INF/MANIFEST.MF` でバージョン更新

2. **リリース手順**
   - テストの実行とパス確認
   - ドキュメントの更新
   - GitHub Release の作成
   - プラグインJAR の添付

### コミュニティガイドライン

- **行動規範**: すべての参加者に敬意を払う
- **コミュニケーション**: 日本語または英語で議論
- **ライセンス**: MIT ライセンスの下で貢献

### 技術的な詳細

#### プロジェクト構造
```
src/io/nncdevel/domaeclipseconfig/
├── Activator.java           # プラグインライフサイクル
└── handlers/
    ├── ConfigHandler.java   # メインロジック（Eclipse依存）
    └── DomaConfigUtils.java # ユーティリティ（Eclipse非依存）
```

#### 重要なクラス
- `ConfigHandler`: Eclipse ワークスペースとの統合
- `DomaConfigUtils`: テスト可能なコアロジック
- プラグイン設定: `plugin.xml`, `META-INF/MANIFEST.MF`

### 質問とサポート

- 開発に関する質問: GitHub Issues
- 一般的な使用法: README の トラブルシューティング セクション
- リアルタイム議論: 適切な場合Issue での議論

皆様のご協力をお待ちしています！

## ライセンス

このプロジェクトは MIT ライセンスの下で提供されています。詳細は [LICENSE](LICENSE) ファイルを参照してください。

### MIT License 概要
- ✅ 商用利用可能
- ✅ 修正可能
- ✅ 配布可能
- ✅ プライベート使用可能
- ❗ 免責事項: 作者は一切の責任を負いません

## バージョン情報

- **現在のバージョン**: 0.0.3
- **対応Eclipse**: 4.6以降
- **対応Java**: Java 8以降
- **対応Doma**: Doma2 2.44.1以降

### 変更履歴

#### v0.0.3
- ユーティリティクラス `DomaConfigUtils` の追加
- Eclipse非依存のテストケースを追加
- エラーハンドリングとログ出力の改善
- クロスプラットフォーム対応の強化

#### v0.0.2
- .factorypath ファイルによる検出機能を追加
- OS固有のパス区切り文字対応
- UI改善（アイコン更新、ダイアログメッセージ）

#### v0.0.1
- 初期リリース
- 基本的なpom.xml ベースのDoma プロジェクト検出
- .classpath ファイルの自動修正機能
