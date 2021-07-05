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
1. form.html 내의 form tag 내에 method='get'으로 지정되어 있는 상태임
2. Get 메서드를 통해 form을 전송할 경우, GET /user/create?userId=jh HTTP/1.1 와 같이 HTTP 요청의 첫 번째 라인에 모든 정보가 담아서 오게 된다. 따라서 substring 사용해서 분리한다.
3. parseQueryString method를 통해 쿼리스트링을 매개변수 & 값으로 분리시켜서 Map에 저장해준다.
4. GET을 이용하여 데이터를 전달하면, 사용자가 입력한 데이터가 브라우저 URL에 노출되기 때문에 보안 측면에서 좋지 않다. 또한 요청 라인의 길이에 제한이 있다.
5. 따라서 Post로 변경해서 구현해야 한다.

* html의 모든 <a> 태그 링크, CSS, 자바스크립트, 이미지 요청은 모두 GET 방식으로 요청을 보낸다. POST 방식은 <form>태그로 요청을 보낼 수 있는데, 이 태그가 지원하는 속성은 GET, POST 이다.
* GET은 서버에 존재하는 데이터를 조회하는 역할만 하는 것
  
### 요구사항 3 - post 방식으로 회원가입
1. getUrl을 통해 첫 번째 줄에서 url만 추출한다.
2. 두 번째 줄 부터는 header이다. HashMap을 통해 헤더 정보를 key & value로 저장한다.
3. 1에서 저장한 url이 /user/create로 시작될 경우, 본격적으로 post를 구현한다.
4. header에서 Content-length 정보를 get한다. 이를 바탕으로 IOUtils.readData를 통해 현재 body를 가리키고 있는 br로 부터 length만큼 queryString 정보를 requestBody(String)에 저장한다.
5. 이후에 사용하기 위해 Database에 User를 생성하여 저장해둔다.
* HTML은 기본적으 GET, POST 메소드만 지원한다.
* POST는 데이터의 상태 변경 작업을 담당한다.
  
### 요구사항 4 - redirect 방식으로 이동
* 위의 회원가입(Create)을 완료한 후, 다시 처음 화면(/index.html)로 가는게 목적이다.
* byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath()), responseBody(dos, body)
* 위의 코드를 통해, 회원가입 요청 완료 후 url에 /index.html을 직접 지정하여 처음 화면으로 돌아갈 수는 있다.
* 하지만, 이 과정에서 새로고침을 누르게 되면, 회원가입 요청이 재전송 됨을 알 수 있다. 서버가 index.html을 자체적으로 뿌려줬을 뿐, 실제로 브라우저에서 요청한 url은 아직 /user/create에
  머물러있기 때문이다. 즉 브라우저가 한 요청이 아니다.
* 따라서 redirect를 통해 브라우저에게 서버가 지정한 url로 다시 요청을 주라는 응답을 보내야 한다. 302 status code를 통해 가능하다.
 
1. response302Header 메서드르 만든 후, 브라우저에게 전달 할 response message를 생성한다.
2. HTTP/1.1 302와 함께 Location: /index.html을 브라우저에 전달하면, 알아서 Location url로 서버에 다시 요청을 준다.
3. 즉 요청과 응답이 각각 2번씩 발생한다.

### 요구사항 5 - cookie
* 앞서 저장한 사용자 데이터를 기반으로, 쿠키를 사용한 로그인 정보 유지에 대해서 구현한다.
1. 등록된 사용자가 아닐 경우, 302를 통해 index/html을 redirect 한다.
2. 비밀번호가 틀릴 경우, 302를 통해 /user/login_fail.html로 redirect 한다. 이 때 Set-Cookie에 logined=false를 setting 한다.
3. 비밀번호가 맞을 경우, 302르 통해 index/html을 redirect 한다. 이 때 Set-Cookie에 logined=true를 setting 한다.
* 쿠키 setting이 성공할 경우(true든 false든) 다시 요청이 올 때마다 브라우저는 해당 쿠키를 요청에 포함시킨다. -> 일단 모든 url 요청에 대해서 브라우저는 쿠키를 같이 전달한다.
* html, css, js에 모두 해당 쿠키가 false로 되어 있는 것을 확인할 수 있다.
* 크롬 개발자 도구를 통해 이 과정을 확인할 수 있다.

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
