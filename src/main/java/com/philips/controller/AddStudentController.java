package com.philips.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.philips.beans.LoginBean;
import com.philips.beans.StudentBean;
import com.philips.dao.SchoolDAO;
import com.philips.dao.StudentDAO;

/**
 * Servlet implementation class AddStudentController
 */
public class AddStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddStudentController() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        String schid = request.getParameter("schoolid");
        String cls = request.getParameter("class");
        // System.out.println(schid);
        String schname = new SchoolDAO().getSchool(Integer.parseInt(schid)).getSchName();
        // System.out.println(schname);
        StudentBean studb = new StudentBean();
        studb.setStudName(request.getParameter("sname"));
        studb.setStudSex(request.getParameter("gen"));
        studb.setStudUSN(request.getParameter("usn"));
        studb.setStudClass(request.getParameter("class"));
        studb.setStudSection(request.getParameter("sec"));
        studb.setStudSchoolId(Integer.parseInt(request.getParameter("schoolid")));
        studb.setStudParent(request.getParameter("pname"));
        studb.setStudParentMob(request.getParameter("mob"));
        studb.setStudParentEmail(request.getParameter("email"));
        if (new StudentDAO().insertStudent(studb,
                ((LoginBean) request.getSession().getAttribute("LOGIN")).getUserId())) {
            if (request.getParameter("formname").equalsIgnoreCase("order")) {
                getServletContext()
                        .getRequestDispatcher(
                                "/MakeOrder2.jsp?msg=Student Added Successfully&schid=" + schid + "&class=" + cls)
                        .forward(request, response);
            } else if (request.getParameter("formname").equalsIgnoreCase("student")) {
                getServletContext().getRequestDispatcher("/student.jsp?msg=Student Added Successfully&schid=" + schid
                        + "&class1=" + cls + "&schname=" + schname).forward(request, response);
            }
            if (request.getParameter("formname").equalsIgnoreCase("mesurement")) {
                getServletContext().getRequestDispatcher(
                        "/TakeMesurementForm2.jsp?msg=Student Added Successfully&schoolid=" + schid + "&class=" + cls)
                        .forward(request, response);
            }
        }

        else {
            getServletContext().getRequestDispatcher("/student.jsp?msg=Sorry Student Not Added.. Try Again")
                    .include(request, response);

        }

    }

}
