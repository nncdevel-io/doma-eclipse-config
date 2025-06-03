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
