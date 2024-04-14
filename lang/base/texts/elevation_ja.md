## 標高

昇格のプロセスはオペレーティングシステムに依存する。

### LinuxとmacOS

すべての昇格コマンドは`sudo`で実行される。オプションの`sudo`パスワードは、必要に応じてXPipe経由で照会される。
パスワードが必要になるたびにパスワードを入力するか、現在のセッションのためにパスワードをキャッシュするかを制御するために、設定で昇格の動作を調整する機能がある。

### ウィンドウズ

Windowsでは、親プロセスが昇格していない場合、子プロセスを昇格させることはできない。
したがって、XPipeが管理者として実行されていない場合、ローカルで昇格を使用することはできない。
リモート接続の場合、接続するユーザーアカウントに管理者権限を与える必要がある。