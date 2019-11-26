package com.philips.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.beans.LoginBean;
import com.philips.beans.MesurementOrderBean;
import com.philips.dao.UniformDAO;

/**
 * Servlet implementation class AssignMesurementController
 */
public class AssignMesurementController extends HttpServlet {
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

        try {
            int count = Integer.parseInt(request.getParameter("totalcount"));
            // 3-PANT (PT) request will be in this format
            String uni = request.getParameter("uniformid");
            String un[] = uni.split("~~");
            int uniformId = 0;
            if (uni != null) {
                uniformId = Integer.parseInt(un[2]);
            }
            ArrayList<MesurementOrderBean> myList = new ArrayList<MesurementOrderBean>();

            // user id taken from session
            int userId = ((LoginBean) request.getSession().getAttribute("LOGIN")).getUserId();

            // System.out.println(request.getParameter("uniformid"));
            for (int i = 0; i < count; i++) {

                MesurementOrderBean temp = new MesurementOrderBean(request.getParameter("uniform" + i),
                        Integer.parseInt(request.getParameter("order" + i)), userId, uniformId);

                myList.add(temp);
                // INSERT INTO MESUREMENTORDER WITH UNIFORM ID
                // System.out.println("Uniform " + request.getParameter("uniform" +i));
                // System.out.println("Uniform " + request.getParameter("order" +i));
            }

            if (new UniformDAO().addMesurement(myList)) {
                request.getRequestDispatcher("index.jsp?msg=Mesurements Saved... ").forward(request, response);
            } else {
                request.getRequestDispatcher("index.jsp?msg=SORRY INSERT NOT DONE... ").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("index.jsp?msg=SORRY NOT INSERTED, BECAUSE OF WRONG VALUES... ")
                    .forward(request, response);
        }

    }

}
