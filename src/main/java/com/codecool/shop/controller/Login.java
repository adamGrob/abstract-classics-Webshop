package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.controller.Validate;
import com.codecool.shop.dao.UserDao;
import com.codecool.shop.dao.implementation.UserDaoJdbc;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/login"})
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {response.setContentType("text/html;charset=UTF-8");
        UserDao userDataStore = UserDaoJdbc.getInstance();

        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        User user = userDataStore.findByName(username);
        if (user == null) {
            response.sendRedirect("/login");
        } else {
            int userId = user.getId();

            if(Validate.checkUser(username, pass)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username );
                session.setAttribute("userId", userId);
                //setting session to expiry in 30 mins
                session.setMaxInactiveInterval(30*60);
                Cookie loginCookie = new Cookie("user",username);
                //setting cookie to expiry in 30 mins
                loginCookie.setMaxAge(30*60);
                response.addCookie(loginCookie);
                response.sendRedirect("/");

            } else {
                response.sendRedirect("/login");
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        WebContext context = new WebContext(request, response, request.getServletContext());
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(request.getServletContext());
        engine.process("product/login.html",context, response.getWriter());

    }
}
