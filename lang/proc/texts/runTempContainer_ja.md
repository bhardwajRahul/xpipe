## 一時的なコンテナ

指定したイメージを使って一時的なコンテナを実行し、停止すると自動的に削除される。コンテナイメージに実行するコマンドが指定されていなくても、コンテナは実行され続ける。

これは、特定のコンテナ・イメージを使って特定の環境を素早くセットアップしたい場合に便利である。その後、XPipeで通常通りコンテナに入り、操作を実行し、不要になったらコンテナを停止することができる。コンテナは自動的に削除される。