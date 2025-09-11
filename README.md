# doma-eclipse-config
Doma2 を Eclipse のビルドパス設定を行うツールです。

## ビルド方法

Eclipse の PDE(Plug-in Development Environment) を利用して以下の手順でビルドします。

1. Eclipse でこのプロジェクトをインポートします。 `File` > `Import...` > `Existing Projects into Workspace` を選択してください。
2. プロジェクトを右クリックし `Export...` を選択します。
3. `Plug-in Development` > `Deployable plug-ins and fragments` を選択して `Next` を押します。
4. 出力先ディレクトリを指定し `Finish` を押すと `io.nncdevel.doma-eclipse-config_*.jar` が生成されます。

## インストール

生成された JAR ファイルを Eclipse の `dropins` フォルダーへコピーし、Eclipse を再起動するとプラグインが有効になります。  
あるいは、上記エクスポート手順で更新サイト形式を出力し、`Install New Software...` からインストールすることも可能です。

## 使い方

プラグインを導入すると、Eclipse のメインツールバーに Doma アイコン ![](icons/doma.png) が表示されます。  
このアイコンをクリックすると、ワークスペース内の Maven Doma プロジェクトを検索して `.classpath` を自動で更新します。  
処理が完了すると、変更されたプロジェクト名がダイアログに表示されます。

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
