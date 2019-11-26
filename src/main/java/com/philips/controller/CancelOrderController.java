package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.dao.OrderDAO;

/**
 * Servlet implementation class ApproveOrderController
 */
public class CancelOrderController extends HttpServlet {
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
        String oId = request.getParameter("oid");
        // reason added by Naveen on 27 Dec 2015

        String reason = request.getParameter("reason");
        if (new OrderDAO().cancelOrder(oId, reason)) {
            request.getRequestDispatcher("CancelOrder1.jsp?msg=Order Cancelled Successfully").forward(request,
                    response);
        } else {
            request.getRequestDispatcher("CancelOrder1.jsp?msg=Sorry Order Not Cancelled Pls Try Again.. ")
                    .forward(request, response);
        }
    }

}
