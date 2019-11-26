package com.philips.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.philips.beans.ProSrcBean;
import com.philips.beans.ProUnIDAuidBean;
import com.philips.beans.SchoolBean;
import com.philips.beans.StudentBean;
import com.philips.connection.GetConnection;

// This class is used for promoting a student from one class to other class 
// The parameters taken are SchoolID, Src Class(For Example: Nur), Sex(For Example : B or G)
public class PromotionDAO {
    static Logger logger = GetConnection.getLogger(HouseDAO.class);

    static {
        logger.info("Connection set to 700 in PromotionDao");
        GetConnection gc = new GetConnection();

        try {
            gc.ps2 = GetConnection.getMySQLConnection().prepareStatement("set global max_connections = 700");
            gc.ps2.executeUpdate();
            // System.out.println("connections set to 700>>>>>>>>>>>");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String acceptCurrentClass = null;
        String acceptNextClass = null;
        int schoolId = 0;
        PromotionDAO pd = new PromotionDAO();
        boolean flag = true;
        Scanner sc = new Scanner(System.in);

        System.out.println(
                "Enter Schoold Id For Processing (Please Contact Developers - ProBits Technologies Pvt Ltd.,))\n");

        for (SchoolBean temp : new SchoolDAO().getAllSchool()) {
            System.out.println(temp.getSchId() + " : " + temp.getSchName());
        }
        System.out.println("----------------------------------------");
        schoolId = sc.nextInt();

        while (flag) {

            System.out.println("Enter Your Choice, for ( " + (new SchoolDAO().getSchool(schoolId)).getSchName()
                    + ")\n1. Delete Old Order \n2. Delete Out Going Students \n3. Promote Student (Reverse Order)\n4.Exit");
            int choice = sc.nextInt();

            switch (choice) {
            case 1:
                System.out.println(
                        "CAUTION: Are you sure to delete all Orders/Order Details/Invoice/Resetting Inv Number/Resetting Order Numbering");
                System.out.println("Enter 1- Yes, 2- No");
                int opt = sc.nextInt();

                if (1 == opt) {
                    if (pd.resetDBOrderingSystem()) {
                        System.out.println("Details Successfully Updated To Next Year");
                    } else {
                        System.out.println("Please Try Again... ");
                    }
                } else {
                    System.out.println("No Changes Has Occurred In The System");
                }

                break;

            case 2:

                System.out.println("Enter Class Number to be Deleted : ");
                acceptCurrentClass = sc.next();
                System.out.println("Deleting of Old Students Started... ");

                pd.deleteOutGoingStudents(schoolId, acceptCurrentClass);
                System.out.println("All Old Students are delete for School ("
                        + (new SchoolDAO().getSchool(schoolId)).getSchName() + ")");

                break;
            case 3:
                System.out
                        .println(" this option to be executed many times in reverse order of the class \nfor example: "
                                + " 11 - 12, 10 - 11, 9  - 10, 8  - 9,  7  - 8, 6  - 7, 5  - 6, 4  - 5, 3  - 4, 2  - 3, 1  - 2, UKG- 1  , LKG- UKG");

                System.out.println("Enter the current Class to be promoted ");
                acceptCurrentClass = sc.next();

                System.out.println("Enter the next class to be promoted ");
                acceptNextClass = sc.next();

                // this list will contain all the unid & auid of current class
                List<ProUnIDAuidBean> currentClass = new ArrayList<ProUnIDAuidBean>();

                for (int temp : pd.getAuidForPromotedClass(schoolId, acceptCurrentClass, "B")) {
                    currentClass.add(new ProUnIDAuidBean(pd.getUniformIdFromAuid(temp), temp));
                }

                // this list will contain all the unid and auid of next class
                List<ProUnIDAuidBean> nextClasss = new ArrayList<ProUnIDAuidBean>();

                for (int temp : pd.getAuidForPromotedClass(schoolId, acceptNextClass, "B")) {
                    nextClasss.add(new ProUnIDAuidBean(pd.getUniformIdFromAuid(temp), temp));
                }

                System.out.println("Current Class\n" + currentClass);
                System.out.println("Next Class\n" + nextClasss);

                ArrayList<ProUnIDAuidBean> commonList = new ArrayList<ProUnIDAuidBean>(currentClass);
                commonList.retainAll(nextClasss);

                System.out.println("Common list is \n" + commonList);

                ArrayList<ProUnIDAuidBean> deletionList = new ArrayList<ProUnIDAuidBean>(currentClass);

                deletionList.removeAll(commonList);
                System.out.println("Uniforms to be deleted list is\n" + deletionList);

                ArrayList<ProUnIDAuidBean> newList = new ArrayList<ProUnIDAuidBean>(nextClasss);
                newList.removeAll(commonList);

                System.out.println("Uniforms newly to be added list is\n" + newList);

                System.out.println("______________________PROCESSING________________________");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            // this block will call commonUniform , deleteUniform, addUniform, and promot
            // the students from currentclass to nextclass
            {
                pd.updateCommonUniform(schoolId, acceptCurrentClass, acceptNextClass, nextClasss);
                pd.deleteExistingUniform(deletionList, schoolId, acceptCurrentClass);
                pd.addNewUniform(newList, schoolId, acceptCurrentClass, acceptNextClass);
                pd.promoteStudent(schoolId, acceptCurrentClass, acceptNextClass);
            }
                System.out.println("______________________END OF PROCESSING________________________");

                return;
            case 4:
                System.out.println("Your Promotion is exited \nTHANKS");
                return;

            default:
                break;
            }

        }

    }

    // in this method we will change the auid of the student as the uniforms are
    // common
    // by Naveen 18Nov2015
    /**
     * 
     * @param schoolId
     * @param currentClass
     * @param nextClass
     * @param nextClasss
     */
    private void updateCommonUniform(int schoolId, String currentClass, String nextClass,
            // using nextClass only to get next year's auid
            List<ProUnIDAuidBean> nextClasss) {

        Map<Integer, Integer> map = this.getMapForNextClassList(nextClasss);

        // this method does not accept sex, so take both B & G in loop
        for (String sex : "B:G".split(":")) {
            // get all the students
            for (StudentBean temp : new PromotionDAO().getStudentOfSchool(schoolId, currentClass, sex)) {
                // for each student get the smid, and loop through them
                GetConnection gc = null;
                String sql = "select sm.smid, sm.auid, au.unid from studentmesurement sm, assignuniform au where stid=? and sm.auid = au.auid;";
                try {
                    gc = new GetConnection();

                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                    gc.ps1.setInt(1, temp.getStudId());

                    gc.rs1 = gc.ps1.executeQuery();

                    // System.out.println("Smid is ");
                    while (gc.rs1.next()) {
                        // System.out.println(gc.rs1.getInt(1) +" for the student " + temp.getStudId()
                        // +", auid "+ gc.rs1.getInt(2) +", Next Year auid " +
                        // map.get(gc.rs1.getInt(3)));

                        GetConnection gc1 = null;
                        // to check only if the nextClasss map returns +ve value
                        try {
                            if ((map.get(gc.rs1.getInt(3)) > 0)) {

                                // updating smid in table studentmesurement
                                String sql1 = "update studentmesurement set auid =? where smid =?";

                                gc1 = new GetConnection();
                                gc1.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                                gc1.ps1.setInt(1, map.get(gc.rs1.getInt(3)));
                                gc1.ps1.setInt(2, gc.rs1.getInt(1));

                                gc1.ps1.executeUpdate();
                            }
                        } catch (NullPointerException npe) {
                        } finally {
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // if (gc.rs1 != null) gc.rs1.close();
                        if (gc.ps1 != null)
                            gc.ps1.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    GetConnection.closeConnection();

                }
            }
        }
    }

    // this method is required for changing the list to map, which is used in
    // updateCommonUniform
    /**
     * 
     * @param nextClasss
     * @return map
     */
    Map<Integer, Integer> getMapForNextClassList(List<ProUnIDAuidBean> nextClasss) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (ProUnIDAuidBean temp : nextClasss) {
            map.put(temp.getUnid(), temp.getAuid());
        }

        Set set = map.entrySet();
        Iterator itr = set.iterator();

        while (itr.hasNext()) {
            Map.Entry obj = (Entry) itr.next();
            // System.out.println(obj.getKey() +", value is " + obj.getValue());
        }
        return map;
    }

    /**
     * in this method first get each student list, get the list of smid by passing
     * auid (current year) and stid, first delete the entry from
     * studentmesurementdetails and then delete from studentmesurement
     */

    /**
     * 
     * @param deletionList
     * @param schoolId
     * @param currentClass
     */
    private void deleteExistingUniform(ArrayList<ProUnIDAuidBean> deletionList, int schoolId, String currentClass) {
        System.out.println("Deleting of old uniforms started");
        for (String sex : "B:G".split(":")) {
            // get all the students
            for (StudentBean temp : new PromotionDAO().getStudentOfSchool(schoolId, currentClass, sex)) {
                // System.out.println("Student processing is " + temp.getStudId());
                // for each student get the smid, and loop through them

                for (ProUnIDAuidBean pro : deletionList) {

                    String sqlDeleteStudentMesurementDetails = "delete from studentmesurementdetails where smid =?";
                    String sqlDeleteStudentMesurement = "delete from studentmesurement where auid=? and stid=?";
                    String sqlGetSMID = "select smid from studentmesurement where auid=? and stid=?";

                    GetConnection gc = new GetConnection();

                    // first get the smid

                    try {
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sqlGetSMID);
                        gc.ps1.setInt(1, pro.getAuid());
                        gc.ps1.setInt(2, temp.getStudId());

                        gc.rs1 = gc.ps1.executeQuery();

                        while (gc.rs1.next()) {
                            // System.out.println("\t\t inside deletion...");
                            // for each smid got delete from studentmesurementdetails first

                            try {
                                gc.ps2 = GetConnection.getMySQLConnection()
                                        .prepareStatement(sqlDeleteStudentMesurementDetails);

                                gc.ps2.setInt(1, gc.rs1.getInt(1));
                                gc.ps2.executeUpdate();
                            } catch (SQLException sqle) {
                            } // ducking
                            finally {
                                try {
                                    if (gc.ps2 != null)
                                        gc.ps2.close();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                GetConnection.closeConnection();

                            }

                            // for each smid got delete from studentmesurement
                            try {
                                gc.ps3 = GetConnection.getMySQLConnection()
                                        .prepareStatement(sqlDeleteStudentMesurement);

                                gc.ps3.setInt(1, pro.getAuid());
                                gc.ps3.setInt(2, temp.getStudId());

                                gc.ps3.executeUpdate();
                            } catch (SQLException sqle) {
                            } // ducking
                            finally {
                                try {
                                    if (gc.ps3 != null)
                                        gc.ps3.close();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                GetConnection.closeConnection();

                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (gc.ps1 != null)
                                gc.ps1.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        GetConnection.closeConnection();
                    }

                }

            }
        }
        System.out.println("Deleting of old uniforms ended");
    }

    private void addNewUniform(ArrayList<ProUnIDAuidBean> newList, int schoolId, String currentClass,
            String nextClass) {
        System.out.println("new list in addnewuniform " + newList);

        for (String sex : "B:G".split(":")) {
            // get all the students
            for (StudentBean temp : new PromotionDAO().getStudentOfSchool(schoolId, currentClass, sex)) {
                // unid, auid
                for (ProUnIDAuidBean pro : newList) {
                    String sqlInsertStudentMeasurement = "insert into studentmesurement (auid, stid, uid) values(?,?,?)";
                    String sqlInsertStudentMeasurementDetails = "insert into studentmesurementdetails (mid, smid, value) values (?,?,?)";
                    int noOfMeasurements = getNumberOfMeasurements(pro.getUnid());

                    GetConnection gc = new GetConnection();

                    try {
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sqlInsertStudentMeasurement,
                                Statement.RETURN_GENERATED_KEYS);

                        gc.ps1.setInt(1, pro.getAuid());
                        gc.ps1.setInt(2, temp.getStudId());
                        // change 3rd param with user who is logged in
                        gc.ps1.setInt(3, 2);

                        gc.ps1.executeUpdate();
                        gc.rs1 = gc.ps1.getGeneratedKeys();

                        if (null != gc.rs1 && gc.rs1.next()) {
                            long smid = gc.rs1.getLong(1);
                            gc.ps2 = GetConnection.getMySQLConnection()
                                    .prepareStatement(sqlInsertStudentMeasurementDetails);
                            for (int i = 0; i < noOfMeasurements; i++) {
                                gc.ps2.setInt(1, i);
                                gc.ps2.setLong(2, smid);
                                gc.ps2.setInt(3, 1);

                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (gc.rs1 != null)
                                gc.rs1.close();
                            if (gc.ps1 != null)
                                gc.ps1.close();
                            if (gc.ps2 != null)
                                gc.ps2.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        GetConnection.closeConnection();

                    }

                }

            }
        }
    }

    public void promoteStudent(int schoolId, String currentClass, String nextClass) {
        System.out.println("student promotion started");
        for (String sex : "B:G".split(":")) {
            // get all the students
            for (StudentBean temp : new PromotionDAO().getStudentOfSchool(schoolId, currentClass, sex)) {

                String sqlPromotStudent = "update student set class = ? where stid = ?";
                GetConnection gc = new GetConnection();
                try {
                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sqlPromotStudent);
                    gc.ps1.setString(1, nextClass);
                    gc.ps1.setInt(2, temp.getStudId());

                    gc.ps1.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (gc.ps1 != null)
                            gc.ps1.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    GetConnection.closeConnection();

                }

            }
        }

        System.out.println("student promotion end");
    }

    public int getNumberOfMeasurements(int unid) {
        String sql = "select count(*) from mesurementorder where unid = ?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, unid);

            gc.rs1 = gc.ps1.executeQuery();
            gc.rs1.next();

            return gc.rs1.getInt(1);
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

        return 0;
    }

    // business logic methods
    // ////////////////////////////////////////////////////////////////////////////
    // Get Student by SchoolId, Class, Sex
    public List<StudentBean> getStudentOfSchool(int schId, String cls, String sex) {

        // System.out.println("school got is " + schId);
        String sql = "select stid, name from student where  schid = ? and class = ? and sex=?";

        GetConnection gc = new GetConnection();
        List<StudentBean> list = new Vector<StudentBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);
            gc.ps1.setString(3, sex);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                StudentBean sb = new StudentBean();
                sb.setStudId(gc.rs1.getInt(1));
                sb.setStudName(gc.rs1.getString(2));

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

    // this method will give UNID, AUID, SMID and list of SMDID for each SMID
    public List<ProSrcBean> getStudentMesurementAssignUniform(int stid) {

        List<ProSrcBean> list = new ArrayList<ProSrcBean>();
        String sql1 = "select sm.SMID, sm.auid, au.unid from studentmesurement sm, assignuniform au where stid=? and sm.auid=au.auid";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.setInt(1, stid);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ProSrcBean temp = new ProSrcBean();
                temp.setSmid(gc.rs1.getInt(1));
                temp.setAuid(gc.rs1.getInt(2));
                temp.setUnid(gc.rs1.getInt(3));

                list.add(temp);
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

    // this method give uniformid for the given assignuniformid
    public Integer getUniformIdFromAuid(int auid) {
        String sql = "select unid from assignuniform where auid=?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, auid);

            gc.rs1 = gc.ps1.executeQuery();

            gc.rs1.next();

            return gc.rs1.getInt(1);

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

        return 0;

    }

    public List<Integer> getAuidForPromotedClass(int schId, String cls, String sex) {

        String sql = "select auid from assignuniform where sex=? AND CLASS=? AND SCHID=?";
        GetConnection gc = new GetConnection();

        List<Integer> auids = new ArrayList<Integer>();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, sex);
            gc.ps1.setString(2, cls);
            gc.ps1.setInt(3, schId);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                auids.add(gc.rs1.getInt(1));
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

        return auids;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // These method are for deleting the record of outgoing student including boy
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// and
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// girl
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean deleteOutGoingStudents(int schoolId, String currentClass) {

        for (String sex : "B:G".split(":")) {
            // get all the students
            for (StudentBean temp : new PromotionDAO().getStudentOfSchool(schoolId, currentClass, sex)) {

                String sqlUpdateStudentClass = "update student set status = 'DEACTIVE' where stid = ?";
                String sqlDeleteStudentMesurement = "delete from studentmesurement where stid = ?";
                String sqlDeleteStudentMesurementDetails = "delete from studentmesurementdetails where smid in (select smid from studentmesurement where stid = ?)";

                GetConnection gc = new GetConnection();
                // first delete from studentmesurementdetails

                try {
                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sqlDeleteStudentMesurementDetails);
                    gc.ps1.setInt(1, temp.getStudId());

                    gc.ps1.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (gc.ps1 != null)
                            gc.ps1.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    GetConnection.closeConnection();
                }

                // then delete from studentmesurement

                try {
                    gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sqlDeleteStudentMesurement);
                    gc.ps2.setInt(1, temp.getStudId());
                    gc.ps2.executeUpdate();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        if (gc.ps2 != null)
                            gc.ps2.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    GetConnection.closeConnection();
                }

                // update student to next class

                try {
                    gc.ps3 = GetConnection.getMySQLConnection().prepareStatement(sqlUpdateStudentClass);

                    gc.ps3.setInt(1, temp.getStudId());
                    gc.ps3.executeUpdate();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        if (gc.ps3 != null)
                            gc.ps3.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    GetConnection.closeConnection();
                }
            }
        }
        return false;
    }

    public boolean resetDBOrderingSystem() {

        String sqlDeleteAllOrderDetail = "delete from orderdetail";
        String sqlDeleteAllStudentOrder = "delete from studentorder";
        String sqlDeleteAllStudentInvoice = "delete from studentinvoice";
        String sqlResetInvNumber = "update studentinvnumber set seqno = 1, yr = yr +1";
        String sqlResetOrderNumber = "update studentordernumber set seqno = 1, yr = yr + 1";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sqlDeleteAllOrderDetail);

            gc.ps1.addBatch();

            gc.ps1.addBatch(sqlDeleteAllStudentOrder);
            gc.ps1.addBatch(sqlDeleteAllStudentInvoice);
            gc.ps1.addBatch(sqlResetInvNumber);
            gc.ps1.addBatch(sqlResetOrderNumber);

            return gc.ps1.executeBatch().length > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }
}
