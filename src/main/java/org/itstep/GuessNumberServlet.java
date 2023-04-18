package org.itstep;


import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class GuessNumberServlet extends HttpServlet {
    public static String TEMPLATE;
    public static Random random = new Random();
    public static int START = 0;
    public static int FINISH = 100;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // 1. Создаем TEMPLATE
        ServletContext servletContext = config.getServletContext();
        try (InputStream in = servletContext.getResourceAsStream("/WEB-INF/template/home.html");
             BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = rdr.readLine()) != null) {
                stringBuilder.append(line);
            }
            TEMPLATE = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newGame = req.getParameter("new");
        if (newGame != null && !newGame.isBlank()) {
            HttpSession session = req.getSession();
            // Вариант 1 - Обнуление параметров
//            session.setAttribute("count", 0);
//            session.setAttribute("number", random.nextInt(START, FINISH + 1));
//            session.setAttribute("line", "");

            // Вариант 2 - Окончание сессии
            session.invalidate();
        }


        HttpSession session = req.getSession();
        if (session.isNew()) {
            session.setAttribute("count", 0);
            session.setAttribute("number", random.nextInt(START, FINISH + 1));
            session.setAttribute("line", "");
        }

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        PrintWriter writer = resp.getWriter();
        writer.printf(TEMPLATE, START, FINISH, session.getAttribute("line"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String numString = req.getParameter("num");
        if (numString != null && !numString.isBlank()) {
            HttpSession session = req.getSession();
            int num = Integer.parseInt(numString);
            System.out.println("num = " + num);
            session.setAttribute("current", num);

            int number = (int) session.getAttribute("number");
            int current = (int) session.getAttribute("current");
            System.out.println("number = " + number);
            System.out.println("current = " + current);
            if (!session.isNew()) {
                if (session.getAttribute("count") instanceof Integer n) {
                    session.setAttribute("count", ++n);
                }
            }
            String line = "";
            if ((int) session.getAttribute("number") > (int) session.getAttribute("current")) {
                line = "<p>Guess number is bigger " + session.getAttribute("current") + "</p>";
            } else if ((int) session.getAttribute("number") < (int) session.getAttribute("current")) {
                line = "<p>Guess number is less " + session.getAttribute("current") + "</p>";
            } else {
                line = "<p class='grad'>Number " + session.getAttribute("current") + " guessed in " +
                       session.getAttribute("count") + " attempts</p>" +
                       "<a class='but-done' role='button' href='?new=1'>New game?</a>";
            }
            System.out.println("line = " + line);
            session.setAttribute("line", session.getAttribute("line") + line);
            System.out.println("session.getAttribute(line) = " + session.getAttribute("line"));
        }
        //        doGet(req, resp);
        resp.sendRedirect("/Lesson073Task01/");
    }

}

