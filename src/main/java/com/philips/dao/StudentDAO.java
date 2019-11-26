package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.philips.beans.StudentBean;
import com.philips.connection.GetConnection;;

//varish written to insert student details
public class StudentDAO {

    static Logger logger = GetConnection.getLogger(StudentDAO.class);

    public boolean insertStudent(StudentBean studb, int userId) {
        GetConnection gc = new GetConnection();
        String sql = "insert into student (usn,name,sex,class,section,parentname,mobile,email,schid,uid) values (?,?,?,?,?,?,?,?,?,?)";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, studb.getStudUSN().toUpperCase());
            gc.ps1.setString(2, studb.getStudName().toUpperCase());
            gc.ps1.setString(3, studb.getStudSex().toUpperCase());
            gc.ps1.setString(4, studb.getStudClass());
            gc.ps1.setString(5, studb.getStudSection().toUpperCase());
            gc.ps1.setString(6, studb.getStudParent().toUpperCase());
            gc.ps1.setString(7, studb.getStudParentMob());
            gc.ps1.setString(8, studb.getStudParentEmail());
            gc.ps1.setInt(9, studb.getStudSchoolId());
            gc.ps1.setInt(10, userId);
            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return false;
    }

    public boolean updateStudent(StudentBean studb, int userId) {
        GetConnection gc = new GetConnection();
        String sql = "update student set usn=?,name=?,sex=?,class=?,section=?,parentname=?,mobile=?,email=?,schid=?,uid=? where stid=?";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, studb.getStudUSN().toUpperCase());
            gc.ps1.setString(2, studb.getStudName().toUpperCase());
            gc.ps1.setString(3, studb.getStudSex().toUpperCase());
            gc.ps1.setString(4, studb.getStudClass());
            gc.ps1.setString(5, studb.getStudSection().toUpperCase());
            gc.ps1.setString(6, studb.getStudParent().toUpperCase());
            gc.ps1.setString(7, studb.getStudParentMob());
            gc.ps1.setString(8, studb.getStudParentEmail());
            gc.ps1.setInt(9, studb.getStudSchoolId());
            gc.ps1.setInt(10, userId);
            gc.ps1.setInt(11, studb.getStudId());
            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return false;
    }

    public boolean updateStudentParent(String parent, String mob, String email, int uid, int stid) {
        GetConnection gc = new GetConnection();
        String sql = "update student set parentname=?,mobile=?,email=?,uid=? where stid=?";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, parent);
            gc.ps1.setString(2, mob);
            gc.ps1.setString(3, email);
            gc.ps1.setInt(4, uid);
            gc.ps1.setInt(5, stid);
            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return false;
    }

    public List<StudentBean> getAllStudents() {
        String sql = "select STID, USN, NAME, SEX, CLASS, SECTION, PARENTNAME, MOBILE, EMAIL, "
                + "UID, SCHID, STATUS, MEASUREMENTDATE from STUDENT";

        List<StudentBean> studentList = new ArrayList<StudentBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean temp = new StudentBean();
                temp.setStudId(gc.rs1.getInt(1));
                temp.setStudUSN(gc.rs1.getString(2));
                temp.setStudName(gc.rs1.getString(3));
                temp.setStudSex(gc.rs1.getString(4));
                temp.setStudClass(gc.rs1.getString(5));
                temp.setStudSection(gc.rs1.getString(6));
                temp.setStudParent(gc.rs1.getString(7));
                temp.setStudParentMob(gc.rs1.getString(8));
                temp.setStudParentEmail(gc.rs1.getString(9));
                temp.setStudUID(gc.rs1.getInt(10));
                temp.setStudSchoolId(gc.rs1.getInt(11));
                temp.setStudStatus(gc.rs1.getString(12));

                studentList.add(temp);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return studentList;
    }

    public List<StudentBean> getStudents(int shcoolId, String studentName) {
        String sql = "select STID, USN, NAME, SEX, CLASS, SECTION, PARENTNAME, MOBILE, EMAIL, "
                + "UID, SCHID, STATUS, MEASUREMENTDATE from STUDENT where schid=? and name like '%" + studentName
                + "%'";

        List<StudentBean> studentList = new ArrayList<StudentBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, shcoolId);
            // gc.ps1.setString(2, studentName);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean temp = new StudentBean();
                temp.setStudId(gc.rs1.getInt(1));
                temp.setStudUSN(gc.rs1.getString(2));
                temp.setStudName(gc.rs1.getString(3));
                temp.setStudSex(gc.rs1.getString(4));
                temp.setStudClass(gc.rs1.getString(5));
                temp.setStudSection(gc.rs1.getString(6));
                temp.setStudParent(gc.rs1.getString(7));
                temp.setStudParentMob(gc.rs1.getString(8));
                temp.setStudParentEmail(gc.rs1.getString(9));
                temp.setStudUID(gc.rs1.getInt(10));
                temp.setStudSchoolId(gc.rs1.getInt(11));
                temp.setStudStatus(gc.rs1.getString(12));

                studentList.add(temp);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return studentList;

    }

    /**
     * 
     * @param sId
     * @return
     * 
     *         THIS QUERY will give the uniform(s) for the student selected mapping
     *         with assignuniform table
     */
    public List<List<Object>> getAssignedUniformsForStudent(String usn, int sp, String class1, int schid) {

        // usn = usn.substring(usn.indexOf("~~~")+1, usn.length());
        // System.out.println("usn got is " + usn);

        String sql = null;
        if (sp == 0) {
            sql = "SELECT distinct au.auid, U.UNID, u.uname  FROM ASSIGNUNIFORM AU, UNIFORM U, STUDENT st "
                    + "WHERE AU.CLASS = ? AND AU.SEX = "
                    + "(SELECT SEX FROM STUDENT ss WHERE ss.usn = ? and ss.class=? and ss.schid=?) "
                    + "and au.schid = ? and AU.UNID = U.UNID and u.sp=0";
        }
        if (sp == 1) {
            sql = "SELECT distinct au.auid, U.UNID, u.uname  FROM ASSIGNUNIFORM AU, UNIFORM U, STUDENT st "
                    + "WHERE AU.CLASS = ? AND AU.SEX = "
                    + "(SELECT SEX FROM STUDENT ss WHERE ss.usn = ? and ss.class=? and ss.schid=?) "
                    + "and au.schid = ? and AU.UNID = U.UNID order by au.auid";
        }

        List<List<Object>> myList = new Vector<List<Object>>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, class1);
            gc.ps1.setString(2, usn);
            gc.ps1.setString(3, class1);
            gc.ps1.setInt(4, schid);
            gc.ps1.setInt(5, schid);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ArrayList<Object> temp = new ArrayList<Object>();
                temp.add(gc.rs1.getString(1));
                temp.add(gc.rs1.getString(2));
                temp.add(gc.rs1.getString(3));

                myList.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return myList;

    }

    /**
     * varish
     * 
     * @param sId
     * @return
     * 
     *         THIS QUERY will give the uniform(s) for the student selected mapping
     *         with assignuniform table used to show student measurements while
     *         taking order
     */
    public List<List<Object>> getAssignedUniformsForStudent1(String usn, String class1, int schid) {

        // usn = usn.substring(usn.indexOf("~~~")+1, usn.length());
        // System.out.println("usn got is " + usn);

        String sql = "SELECT distinct au.auid, U.UNID, u.uname  FROM ASSIGNUNIFORM AU, UNIFORM U, STUDENT st, "
                + "studentmesurementdetails smd, studentmesurement sm WHERE "
                + "AU.CLASS = ? AND AU.SEX = (SELECT SEX FROM STUDENT ss WHERE ss.usn = ? and ss.class=? and ss.schid=?) "
                + "and au.schid = ? and "
                + "AU.UNID = U.UNID and smd.smid=sm.smid and sm.auid=au.auid and smd.value > 0.0";

        List<List<Object>> myList = new Vector<List<Object>>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, class1);
            gc.ps1.setString(2, usn);
            gc.ps1.setString(3, class1);
            gc.ps1.setInt(4, schid);
            gc.ps1.setInt(5, schid);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ArrayList<Object> temp = new ArrayList<Object>();
                temp.add(gc.rs1.getString(1));
                temp.add(gc.rs1.getString(2));
                temp.add(gc.rs1.getString(3));

                myList.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return myList;

    }

    // naveen
    public int getStudentIdFromUsn(String usn, String class1, int schoolid) {
        // usn = usn.substring(usn.indexOf("~~~")+1, usn.length());
        // System.out.println("usn="+usn);
        String sql = "select stid from student where usn = ? and class= ? and schid = ?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, usn);
            gc.ps1.setString(2, class1);
            gc.ps1.setInt(3, schoolid);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getInt(1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return 0;
    }

    public List<StudentBean> getStudentOfSchool(int schId, String cls) {

        // System.out.println("school got is " + schId);
        String sql = "select stid, name, parentname, sex, class, section, mobile, email, uid, schid, usn from student where  schid = ? and class = ? and status='ACTIVE'";

        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudName(gc.rs1.getString(2));
                sb.setStudParent(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));
                sb.setStudClass(gc.rs1.getString(5));
                sb.setStudSection(gc.rs1.getString(6));
                sb.setStudParentMob(gc.rs1.getString(7));
                sb.setStudParentEmail(gc.rs1.getString(8));
                sb.setStudUID(gc.rs1.getInt(9));
                sb.setStudSchoolId(gc.rs1.getInt(10));
                sb.setStudUSN(gc.rs1.getString(11));

                list.add(sb);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            GetConnection.closeConnection();
        }
        return list;
    }

    /**
     * 
     * @param schId
     * @param cls
     * @return List<StudentBean> This method will list all the students who's
     *         measurement was not taken, this will be checked if the
     *         MEASUREMENTDATE is null in the DB, if not null then the measurement
     *         is taken if not he/she was absent on particular day
     */
    public List<StudentBean> getStudentOfSchoolAbsent(int schId, String cls) {

        String sql = "select stid, name, parentname, sex, class, section, mobile, email, uid, "
                + "schid, usn from student where  schid = ? and class = ? and status='ACTIVE' and MEASUREMENTDATE is NULL";

        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudName(gc.rs1.getString(2));
                sb.setStudParent(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));
                sb.setStudClass(gc.rs1.getString(5));
                sb.setStudSection(gc.rs1.getString(6));
                sb.setStudParentMob(gc.rs1.getString(7));
                sb.setStudParentEmail(gc.rs1.getString(8));
                sb.setStudUID(gc.rs1.getInt(9));
                sb.setStudSchoolId(gc.rs1.getInt(10));
                sb.setStudUSN(gc.rs1.getString(11));

                list.add(sb);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return list;

    }

    /*
     * varish this method is used to get student details using usn used in
     * TakeMesurement3.jsp and TakeMesurement4.jsp
     */
    public StudentBean getStudent(String usn, String class1, int schid) {
        String sql = "select stid,name,sex,class,section,parentname,mobile,email, measurementdate from student "
                + " where usn=? and class=? and schid=?";

        GetConnection gc = new GetConnection();
        StudentBean sb = null;

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, usn);
            gc.ps1.setString(2, class1);
            gc.ps1.setInt(3, schid);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {

                sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudName(gc.rs1.getString(2));
                sb.setStudSex(gc.rs1.getString(3));
                sb.setStudClass(gc.rs1.getString(4));
                sb.setStudSection(gc.rs1.getString(5));
                sb.setStudParent(gc.rs1.getString(6));
                sb.setStudParentMob(gc.rs1.getString(7));
                sb.setStudParentEmail(gc.rs1.getString(8));
                sb.setMeasurementDate(gc.rs1.getString(9));
                return sb;
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return sb;
    }

    /**
     * 
     * @param usn
     * @param class1
     * @param schid
     * @return
     * @see : This method is used in ShowPackingOrder.jsp, for showing student
     *      details while packing, this was part of new requirement on 22 feb 2018
     */

    public StudentBean getStudentWithOrderId(String orderId) {
        String sql = "select  stid,name,sex,class,section,parentname,mobile,email, measurementdate "
                + " from student where stid = (select stid from studentorder where oid =?)";

        GetConnection gc = new GetConnection();
        StudentBean sb = null;

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, orderId);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {

                sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudName(gc.rs1.getString(2));
                sb.setStudSex(gc.rs1.getString(3));
                sb.setStudClass(gc.rs1.getString(4));
                sb.setStudSection(gc.rs1.getString(5));
                sb.setStudParent(gc.rs1.getString(6));
                sb.setStudParentMob(gc.rs1.getString(7));
                sb.setStudParentEmail(gc.rs1.getString(8));
                sb.setMeasurementDate(gc.rs1.getString(9));
                return sb;
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return sb;
    }

    public int getSchoolId(int studentId) {
        // TODO Auto-generated method stub
        String sql = "select schid from student where stid = ?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, studentId);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getInt(1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return 0;
    }

    /*
     * varish to list order placed students used in packing report
     */
    public List<StudentBean> getOrderPlacedStudentsOfSchool(int schId, String cls, String sdate, String edate) {

        String sql1 = "select st.stid,st.usn,st.name,st.sex from student st,studentorder so where st.stid=so.stid and "
                + "st.schid= ? and st.class= ? and so.cancelled='n' and so.paid='y' and so.odate between ? and ? order by so.refno";
        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);
            gc.ps1.setString(3, sdate);
            gc.ps1.setString(4, edate);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudUSN(gc.rs1.getString(2));
                sb.setStudName(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));

                list.add(sb);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return list;

    }

    /*
     * varish to list order placed students used in packing report, added by naveen
     * as part of the new requirement, this method shall take the sex as the
     * parameter 21 Dec 2017
     * 
     */
    public List<StudentBean> getOrderPlacedStudentsOfSchool(int schId, String cls, String sdate, String edate,
            String sex) {

        logger.info("packing report called for " + sex);

        String sql1 = "select st.stid,st.usn,st.name,st.sex from student st,studentorder so where st.stid=so.stid and "
                + "st.schid= ? and st.class= ? and so.cancelled='n' and so.paid='y'  and so.odate between ? and ?  and st.sex = ?  order by so.refno";
        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);
            gc.ps1.setString(3, sdate);
            gc.ps1.setString(4, edate);
            gc.ps1.setString(5, sex);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudUSN(gc.rs1.getString(2));
                sb.setStudName(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));

                list.add(sb);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return list;

    }

    /*
     * varish to list order placed students for selected uniform used in tailor
     * report
     */
    public List<StudentBean> getOrderPlacedStudentsOfSchoolUnid(int schId, int unid, String sdate, String edate) {
        String sql1 = "select st.stid,st.usn,st.name,st.sex,so.oid from student st,studentorder so, orderdetail od, "
                + "studentordernumber son where st.stid=so.stid and"
                + " st.schid= ? and od.unid=? and od.qty>0 and so.oid=od.oid and so.cancelled='n' and so.paid='y' and so.odate between"
                + " ? and ? and son.SCHID = st.SCHID order by refno;";

        logger.info("Called --> getOrderPlacedStudentsOfSchoolUnidWithOrderNo");
        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.setInt(1, schId);
            gc.ps1.setInt(2, unid);
            gc.ps1.setString(3, sdate);
            gc.ps1.setString(4, edate);

            // gc.ps1 = GetConnection.getMySQLConnection().prepareStatement("set global
            // max_connections = 700");

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudUSN(gc.rs1.getString(2));
                sb.setStudName(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));
                sb.setOrderId(gc.rs1.getString(5));
                list.add(sb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return list;
    }

    // added by Naveen 09-Feb-2016
    // to get non universal report with class...
    public List<StudentBean> getOrderPlacedStudentsOfSchoolUnid(int schId, int unid, String sdate, String edate,
            String cls) {
        /*
         * String sql1 =
         * "select st.stid,st.usn,st.name,st.sex from student st,studentorder so, orderdetail od where st.stid=so.stid "
         * +
         * "and st.schid= ? and od.unid=? and od.qty>0 and so.oid=od.oid and so.cancelled='n' and so.paid='y' and so.odate "
         * + "between ? and ? and st.class=? order by refno;";
         */

        String sql1 = "select st.stid, st.usn, st.name, st.sex, so.oid  from student st,studentorder so, orderdetail od, studentordernumber son "
                + "where st.stid=so.stid  and st.schid= ? and od.unid=? and od.qty>0 and so.oid=od.oid and so.cancelled='n'"
                + " and so.paid='y' and so.odate  between ?  and ? and st.class=? and son.SCHID = st.SCHID order by refno";

        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.setInt(1, schId);
            gc.ps1.setInt(2, unid);
            gc.ps1.setString(3, sdate);
            gc.ps1.setString(4, edate);
            gc.ps1.setString(5, cls);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudUSN(gc.rs1.getString(2));
                sb.setStudName(gc.rs1.getString(3));
                sb.setStudSex(gc.rs1.getString(4));
                sb.setOrderId(gc.rs1.getString(5));
                list.add(sb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return list;
    }

    /**
     * 
     * @param studentId
     * @return this method is part of new requirement after GST, to get student
     *         details when order id between range is passed, getting the list of
     *         all students who placed an order from method
     *         getOrderPlacedStudentsOfSchoolUnid, and filtering
     * @since 25 jul 2017
     */

    public List<StudentBean> getOrderPlacedStudentsOfSchoolUnidWithOrderNo(int schId, int unid, String sdate,
            String edate, int minOrderNo, int maxOrderNo) {

        System.out.println(schId + ", " + unid + ", " + sdate + ", " + edate + ", " + minOrderNo);
        List<StudentBean> list = this.getOrderPlacedStudentsOfSchoolUnid(schId, unid, sdate, edate);
        List<StudentBean> minMaxList = new ArrayList<StudentBean>();
        for (StudentBean temp : list) {
            // get only number part of the oid such that filters between minOrderNo and
            // maxOrderNo can be applied
            String orderId = temp.getOrderId().substring(temp.getOrderId().lastIndexOf("/") + 1,
                    temp.getOrderId().length());
            // since the data got in String format converting to int, and comparing between
            // min and max values
            int iOrderId = Integer.parseInt(orderId);

            if (iOrderId >= minOrderNo && iOrderId <= maxOrderNo) {
                System.out.println(temp.getStudId() + ", " + temp.getOrderId() + "   ,  " + orderId);
                minMaxList.add(temp);
            }
        }
        return minMaxList;
    }

    /**
     * 
     * @param studentId
     * @return this method is part of new requirement after GST, to get student
     *         details when order id between range is passed, getting the list of
     *         all students who placed an order from method
     *         getOrderPlacedStudentsOfSchoolUnid, and filtering
     * @since 25 jul 2017
     */

    public List<StudentBean> getOrderPlacedStudentsOfSchoolUnidWithOrderNo(int schId, int unid, String sdate,
            String edate, String cls, int minOrderNo, int maxOrderNo) {

        List<StudentBean> list = this.getOrderPlacedStudentsOfSchoolUnid(schId, unid, sdate, edate, cls);
        List<StudentBean> minMaxList = new ArrayList<StudentBean>();

        for (StudentBean temp : list) {
            // get only number part of the oid such that filters between minOrderNo and
            // maxOrderNo can be applied
            String orderId = temp.getOrderId().substring(temp.getOrderId().lastIndexOf("/") + 1,
                    temp.getOrderId().length());
            // since the data got in String format converting to int, and comparing between
            // min and max values
            int iOrderId = Integer.parseInt(orderId);

            if (iOrderId >= minOrderNo && iOrderId <= maxOrderNo) {
                System.out.println(temp.getStudId() + ", " + temp.getOrderId() + "   ,  " + orderId);
                minMaxList.add(temp);
            }
        }
        return minMaxList;
    }

    public static void main(String[] args) {
        new StudentDAO().getOrderPlacedStudentsOfSchoolUnidWithOrderNo(2, 1, "2017-01-07", "2017-03-07", 100, 200);
    }

    public boolean updateMeasurementDate(int studentId) {
        GetConnection gc = new GetConnection();
        try {
            String sql = "update student set MEASUREMENTDATE= now() where stid=?";

            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, studentId);

            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {

                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return false;
    }

    /**
     * 
     * @param studentId
     * @since Aug 15 2016 new Requirement
     * @see Deleting student shall be delete from Studentinvoice, OrderDetail,
     *      studentorder, studentmesurementdetails, studentmesuremnt, STUDENT
     * 
     */
    public boolean deleteStudent(String studentId) {
        GetConnection gc = new GetConnection();

        try {
            // delete invoice
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(
                    "delete from studentinvoice where oid in (select oid from studentorder where stid='" + studentId
                            + "')");
            gc.ps1.addBatch();

            gc.ps1.addBatch("delete from orderdetail where oid in (select oid from studentorder where stid='"
                    + studentId + "')");
            gc.ps1.addBatch("delete from studentorder where stid ='" + studentId + "'");
            gc.ps1.addBatch(
                    "delete from studentmesurementdetails where smid in (select smid from studentmesurement where stid = '"
                            + studentId + "')");
            gc.ps1.addBatch("delete from studentmesurement where stid = '" + studentId + "'");
            gc.ps1.addBatch("DELETE FROM STUDENT WHERE STID='" + studentId + "'");

            return gc.ps1.executeBatch().length > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    } // end of delete student

}// end of class
