package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.dao.OrderDAO;

public class ApproveOrderController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String oId = request.getParameter("oid");
        if (new OrderDAO().approveOrder(oId)) {
            request.getRequestDispatcher("PrintOrder.jsp?oid=" + oId + "&frmname=vorder").forward(request, response);
        } else {
            request.getRequestDispatcher(
                    "ApproveOrder1.jsp?msg=Sorry Order Not Approved / This order ia already Cancelled. <br />Pls Try Again.. ")
                    .forward(request, response);
        }
    }

}
