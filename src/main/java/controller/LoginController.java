package controller;

import http.HttpSession;
import model.User;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;

public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                //비밀번호가 맞다면, request에서 session을 받아서
                HttpSession session = request.getSession();
                session.setAttributes("user", user);
//                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/login_failed.html");
            }
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
