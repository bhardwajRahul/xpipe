## SSH 구성

여기에서 연결에 전달해야 하는 모든 SSH 옵션을 지정할 수 있습니다.
`호스트이름`과 같이 연결을 성공적으로 설정하기 위해 필수적으로 필요한 옵션도 있지만,
다른 많은 옵션은 순전히 선택 사항입니다.

가능한 모든 옵션에 대한 개요를 보려면 [`man ssh_config`](https://linux.die.net/man/5/ssh_config)를 사용하거나 이 [가이드](https://www.ssh.com/academy/ssh/config)를 읽어보세요.
지원되는 옵션의 정확한 양은 순전히 설치된 SSH 클라이언트에 따라 다릅니다.

### 서식

여기의 내용은 SSH 구성 파일의 호스트 섹션 하나에 해당합니다.
`Host` 키는 자동으로 정의되므로 명시적으로 정의할 필요가 없습니다.

다른 구성 호스트에 의존하는 프록시 점프 호스트와 같은 종속 연결과 같이 둘 이상의 호스트 섹션을 정의하려는 경우 여기에 여러 호스트 항목을 정의할 수도 있습니다. 그러면 XPipe가 첫 번째 호스트 항목을 시작합니다.

공백이나 들여쓰기를 사용하여 서식을 지정할 필요는 없지만 작동하는 데는 필요하지 않습니다.

공백이 포함된 값은 반드시 따옴표로 묶어야 하며, 그렇지 않으면 잘못 전달될 수 있습니다.

### 신원 파일

여기에 `아이덴티티파일` 옵션을 지정할 수도 있습니다.
이 옵션을 여기에 지정하면 아래에서 다르게 지정한 키 기반 인증 옵션은 무시됩니다.

XPipe git 볼트에서 관리되는 ID 파일을 참조하도록 선택하면 그렇게 할 수도 있습니다.
XPipe는 공유 ID 파일을 감지하여 git 볼트를 복제한 모든 시스템에서 파일 경로를 자동으로 조정합니다.
