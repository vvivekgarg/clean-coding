package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.dao.UniformDAO;

/**
 * Servlet implementation class AssignHouseController
 */
public class AssignHouseController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
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

        // this servlet is used to update House details into assigned mesurement

        int schId = Integer.parseInt(request.getParameter("schoolid"));
        String cls = request.getParameter("class");
        String sex = request.getParameter("sex").toUpperCase();
        int uniformId = Integer.parseInt(request.getParameter("uniformid"));
        int houseId = Integer.parseInt(request.getParameter("hid"));

        // System.out.println(schId+cls+sex+uniformId+houseId);
        if (new UniformDAO().updateUniformHouse(schId, cls, sex, uniformId, houseId)) {
            request.getRequestDispatcher("index.jsp?msg=House Colors Updated Successfully... ").forward(request,
                    response);
        } else {
            request.getRequestDispatcher("index.jsp?msg=Sorry Details Not updated... ... ").forward(request, response);
        }

    }

}
