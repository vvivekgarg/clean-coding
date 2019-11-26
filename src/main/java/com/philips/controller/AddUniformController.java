package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.beans.LoginBean;
import com.philips.dao.UniformDAO;

public class AddUniformController extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (new UniformDAO().insertUniform(request.getParameter("uniformname").toUpperCase(),
                request.getParameter("shortcode").toUpperCase(), request.getParameter("sp"),
                request.getParameter("hsn"), ((LoginBean) request.getSession().getAttribute("LOGIN")).getUserId())) {

            request.getRequestDispatcher("adduniform.jsp?msg=Uniform Added Successfully").forward(request, response);
        } else {
            request.getRequestDispatcher("adduniform.jsp?msg=Sorry Uniform Not Added").include(request, response);
        }

    }

}
