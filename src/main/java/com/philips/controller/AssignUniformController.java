package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.beans.SchoolBean;
import com.philips.dao.SchoolDAO;

public class AssignUniformController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SchoolBean school = new SchoolDAO().getSchool(Integer.parseInt(request.getParameter("schoolid")));

        String cls = request.getParameter("class");
        String sex = request.getParameter("sex");

        request.setAttribute("school", school);
        request.setAttribute("class", cls);
        request.setAttribute("sex", sex);

        request.getRequestDispatcher("AssignUniform1.jsp?").forward(request, response);
    }

}
