package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

// 추상 메서드를 포함하지 않는 클래스에도 키워드 abstract를 통해 추상클래스로 지정 가능.
// 이렇게 하면 인스턴스 생성 불가
public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();

        if(method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
