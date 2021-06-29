# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080 으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 



### 요구사항 1 - http://localhost:8080/index.html 로 접속시 응답
1. 우선 마크다운에 대해서 알게 됨
2. java.nio.files.Files
   * 기본 java.io인 File외에 static 메서드로 복잡한 처리를 하기 위한 클래스
   * run()에서 readAllByte를 통해 해당 url에 있는 모든 byte를 읽어온다.
   * readAllBytes(new File("./webapp" + url).toPath()) 또는 readAllBytes(Paths.get("./webapp" + url))로 경로를 지정할 수 있다.
   * File의 toPath()를 통해 **Path** 객체 얻을 수 있다.
   * Paths의 get()을 통해 **Path** 객체 얻을 수 있다.

3. 브라우저가 서버 프로그램에 보내는 get 요청을, handler의 'run' 내에서 처리한다. 
   1. 내부에서 url을 추출해서 byte[] body를 만든다. html의 모든 내용이 여기로 들어간다.
   2. DataOutputStream 생성 후, OutputStream을 생성자의 인자로 전달한다.(참조자는 dos)
   3. response200Header(dos, body.length)를 실행한다. -> dos에 HTTP Response를 입력한다.(dos.writeByte(" ") 이렇게 하면 body.length를 response에서 확인 가능하다.
   4. dos.writeBytes("\r\n")를 통해 header와 body를 구분한다. body는 다음 메서드에서 작성된다.
   5. responseBody(dos, body)를 실행한다. -> dos.write(body, 0, body.length)를 통해 body의 내용을 dos로 전달한다. 가운데 0은 offset이다.
   6. 결과로, 브라우저에서 헤더, 바디를 분리시켜서 알아서 html을 만들어준다.

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
