package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.philips.beans.UniformBean;
import com.philips.connection.GetConnection;
import com.philips.utilities.Utilities;

public class TailorDAO {

    static Logger logger = GetConnection.getLogger(TailorDAO.class);

    // by naveen
    // the user can send multiple schools or single schools
    public int prepareTailorReport(int uniformId, ArrayList<String> classes, ArrayList<String> sex,
            ArrayList<String> schoolIds, String startDate, String endDate) {

        logger.info("start of prepare tailor report");
        int count = 0;

        if (createTableAndStoreValues(uniformId, classes, sex, schoolIds)) {
            try {
                GetConnection gc = new GetConnection();
                gc.stmt = GetConnection.getMySQLConnection().createStatement();

                String pSchools = "";
                for (String temp : schoolIds) {
                    pSchools = pSchools + "'" + temp + "',";
                }

                pSchools = pSchools.substring(0, pSchools.length() - 1);

                String pClasses = "";
                for (String temp : classes) {
                    pClasses = pClasses + "'" + temp + "',";

                }
                pClasses = pClasses.substring(0, pClasses.length() - 1);

                String pSex = "";
                // assuming that boy and girl is given
                if (sex.size() > 1) {
                    pSex = "'B', 'G'";
                } else {
                    pSex = "'" + sex.get(0) + "'";
                }

                if (getNumberOfUniforms(uniformId) <= 3) {

                    // Naveen 5th Feb 2015
                    String temp = "";
                    boolean isColor = checkColorForUniform(uniformId, classes, sex, schoolIds);
                    if (isColor) {
                        temp = "create table tmp_report1 as  select smd.value,mo.name,smd.smid, od.qty, IFNULL(SM.COLOR, 'WHITE') AS COLOR ";
                    } else {
                        temp = "create table tmp_report1 as select smd.value,mo.name,smd.smid, od.qty ";
                    }

                    String sql = "";
                    boolean isDateNotGiven = startDate.equals("") || endDate.equals("");
                    if (isDateNotGiven) {
                        sql = temp
                                + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                                + "studentorder so, ASSIGNUNIFORM AU  where OD.QTY > 0 and  SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                                + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid and  smd.mid=mo.mid and"
                                + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                                + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex
                                + ")  and unid='" + uniformId + "' AND AU.AUID = AUID ))  order by mo.morder";
                    } else {
                        sql = temp
                                + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                                + "studentorder so, ASSIGNUNIFORM AU  where OD.QTY > 0 and  so.odate between '"
                                + startDate + "' and '" + endDate
                                + "' and SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                                + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid and  smd.mid=mo.mid and"
                                + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                                + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex
                                + ")  and unid='" + uniformId + "' AND AU.AUID = AUID ))  order by mo.morder";
                    }

                    // end Naveen 5th Feb 2015
                    // logger.info(sql);

                    logger.info("sql in tailorDAO  is " + sql);

                    count = gc.stmt.executeUpdate(sql);
                    logger.info("No of recors inserted in tmp_report1 is " + count);

                    // here calling the method to set all values in tmp_report1
                    // with max number of
                    // fields with 0.1 as the value
                    // 9th feb 2015

                    // by 09Feb 2015 - Naveen
                    setEqualUniforms(uniformId);

                    // will call the procedure if atleast one uniform is
                    // there...
                    if (count > 0) {
                        gc.cs1 = GetConnection.getMySQLConnection().prepareCall(
                                "CALL PR_CREATE_INSERT_TAILOR_RPT(" + (isColor == true ? "'Y'" : "'N'") + ")");
                        gc.cs1.executeUpdate();
                        // added newly
                        gc.cs2 = GetConnection.getMySQLConnection().prepareCall("CALL PR_UPDATE_TAILOR_REPORT1_QTY()");
                        gc.cs2.executeUpdate();

                    }

                } else {

                    String temp = "";
                    boolean isColor = checkColorForUniform(uniformId, classes, sex, schoolIds);
                    if (isColor) {
                        temp = "create table tmp_report_ord as  select smd.value,mo.name, od.qty, sm.smid,  SO.OID,  IFNULL(SM.COLOR, 'WHITE') AS COLOR ";
                    } else {
                        temp = "create table tmp_report_ord as select smd.value,mo.name, od.qty, sm.smid,  SO.OID  ";
                    }

                    String sql = temp
                            + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                            + "studentorder so, ASSIGNUNIFORM AU  where SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                            + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid and  smd.mid=mo.mid and"
                            + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                            + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex + ")  and unid='"
                            + uniformId + "' AND AU.AUID = AUID )) " + "order by mo.morder";

                    logger.info("sql for no of uniforms > 3" + sql);

                    count = gc.stmt.executeUpdate(sql);
                    logger.info("No of recors inserted in tmp_report1 is " + count);

                    // will call the procedure if atleast one uniform is
                    // there...
                    // by 09Feb 2015 - Naveen
                    setEqualUniforms(uniformId);
                    // System.out.println("After Set uniform called.. ");

                    if (count > 0) {
                        gc.cs1 = GetConnection.getMySQLConnection().prepareCall(
                                "CALL PR_CREATE_INSERT_TAILOR_RPT_ORDERID(" + (isColor == true ? "'Y'" : "'N'") + ")");
                        gc.cs1.executeUpdate();
                        // added newly
                        gc.cs2 = GetConnection.getMySQLConnection()
                                .prepareCall("CALL PR_UPDATE_TAILOR_REPORT1_QTY_ORD()");
                        gc.cs2.executeUpdate();

                    }
                }
            } catch (SQLException e) {
                logger.error(e);

                e.printStackTrace();
            } finally {
                GetConnection.closeConnection();
            }
        }

        logger.info("end of prepare tailore report");
        return count;
    }

    // by naveen
    // the user can send multiple schools or single schools
    public int prepareTailorReportOrderNumber(int uniformId, ArrayList<String> classes, ArrayList<String> sex,
            ArrayList<String> schoolIds, String startDate, String endDate, int startOrderNumber, int endOrderNumber) {

        logger.info("start of prepare tailor report with order number");
        int count = 0;

        if (createTableAndStoreValues(uniformId, classes, sex, schoolIds)) {
            try {
                GetConnection gc = new GetConnection();
                gc.stmt = GetConnection.getMySQLConnection().createStatement();

                String pSchools = "";
                for (String temp : schoolIds) {
                    pSchools = pSchools + "'" + temp + "',";
                }

                pSchools = pSchools.substring(0, pSchools.length() - 1);

                String pClasses = "";
                for (String temp : classes) {
                    pClasses = pClasses + "'" + temp + "',";

                }
                pClasses = pClasses.substring(0, pClasses.length() - 1);

                String pSex = "";
                // assuming that boy and girl is given
                if (sex.size() > 1) {
                    pSex = "'B', 'G'";
                } else {
                    pSex = "'" + sex.get(0) + "'";
                }

                if (getNumberOfUniforms(uniformId) <= 3) {

                    // Naveen 5th Feb 2015
                    String temp = "";
                    boolean isColor = checkColorForUniform(uniformId, classes, sex, schoolIds);
                    if (isColor) {
                        temp = "create table tmp_report1 as  select smd.value,mo.name,smd.smid, od.qty, IFNULL(SM.COLOR, 'WHITE') AS COLOR ";
                    } else {
                        temp = "create table tmp_report1 as select smd.value,mo.name,smd.smid, od.qty ";
                    }

                    String sql = "";
                    boolean isDateNotGiven = startDate.equals("") || endDate.equals("");
                    if (isDateNotGiven) {
                        sql = temp
                                + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                                + "studentorder so, ASSIGNUNIFORM AU  where OD.QTY > 0 and  SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                                + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid and so.oidn between "
                                + startOrderNumber + " and " + endOrderNumber + "   and  smd.mid=mo.mid and"
                                + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                                + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex
                                + ")  and unid='" + uniformId + "' AND AU.AUID = AUID ))  order by mo.morder";
                    } else {
                        sql = temp
                                + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                                + "studentorder so, ASSIGNUNIFORM AU  where OD.QTY > 0 and  so.odate between '"
                                + startDate + "' and '" + endDate
                                + "' and SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                                + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid    and so.oidn between "
                                + startOrderNumber + " and " + endOrderNumber + " and  smd.mid=mo.mid and"
                                + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                                + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex
                                + ")  and unid='" + uniformId + "' AND AU.AUID = AUID ))  order by mo.morder";
                    }

                    // end Naveen 5th Feb 2015
                    // logger.info(sql);

                    logger.info("sql in tailorDAO  is " + sql);

                    count = gc.stmt.executeUpdate(sql);
                    logger.info("No of recors inserted in tmp_report1 is " + count);

                    // here calling the method to set all values in tmp_report1
                    // with max number of
                    // fields with 0.1 as the value
                    // 9th feb 2015

                    // by 09Feb 2015 - Naveen
                    setEqualUniforms(uniformId);

                    // will call the procedure if atleast one uniform is
                    // there...
                    if (count > 0) {
                        gc.cs1 = GetConnection.getMySQLConnection().prepareCall(
                                "CALL PR_CREATE_INSERT_TAILOR_RPT(" + (isColor == true ? "'Y'" : "'N'") + ")");
                        gc.cs1.executeUpdate();
                        // added newly
                        gc.cs2 = GetConnection.getMySQLConnection().prepareCall("CALL PR_UPDATE_TAILOR_REPORT1_QTY()");
                        gc.cs2.executeUpdate();

                    }

                } else {

                    String temp = "";
                    boolean isColor = checkColorForUniform(uniformId, classes, sex, schoolIds);
                    if (isColor) {
                        temp = "create table tmp_report_ord as  select smd.value,mo.name, od.qty, sm.smid,  SO.OID,  IFNULL(SM.COLOR, 'WHITE') AS COLOR ";
                    } else {
                        temp = "create table tmp_report_ord as select smd.value,mo.name, od.qty, sm.smid,  SO.OID  ";
                    }

                    String sql = temp
                            + "from studentmesurementdetails smd, STUDENTMESUREMENT SM, orderdetail od, mesurementorder mo, "
                            + "studentorder so, ASSIGNUNIFORM AU  where SM.SMID = SMD.SMID AND  SM.AUID = AU.AUID AND  OD.SMID = SM.SMID "
                            + "AND  so.oid = od.oid and  so.cancelled='N' and  SO.PAID='Y' AND  od.unid = mo.unid and  smd.mid=mo.mid and"
                            + " smd.smid in(select smid from  studentmesurement where auid in (select auid from assignuniform  where schid "
                            + "in(" + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex + ")  and unid='"
                            + uniformId + "' AND AU.AUID = AUID )) " + "order by mo.morder";

                    logger.info("sql for no of uniforms > 3" + sql);

                    count = gc.stmt.executeUpdate(sql);
                    logger.info("No of recors inserted in tmp_report1 is " + count);

                    // will call the procedure if atleast one uniform is
                    // there...
                    // by 09Feb 2015 - Naveen
                    setEqualUniforms(uniformId);
                    // System.out.println("After Set uniform called.. ");

                    if (count > 0) {
                        gc.cs1 = GetConnection.getMySQLConnection().prepareCall(
                                "CALL PR_CREATE_INSERT_TAILOR_RPT_ORDERID(" + (isColor == true ? "'Y'" : "'N'") + ")");
                        gc.cs1.executeUpdate();
                        // added newly
                        gc.cs2 = GetConnection.getMySQLConnection()
                                .prepareCall("CALL PR_UPDATE_TAILOR_REPORT1_QTY_ORD()");
                        gc.cs2.executeUpdate();

                    }
                }
            } catch (SQLException e) {
                logger.error(e);

                e.printStackTrace();
            } finally {
                GetConnection.closeConnection();
            }
        }

        logger.info("end of prepare tailore report");
        return count;
    }

    // this method is used for droping the table and create a table and storing
    // the values based on what we have got
    public boolean createTableAndStoreValues(int uniformId, ArrayList<String> classes, ArrayList<String> sex,
            ArrayList<String> schoolIds) {

        logger.info("start of create table and store values");
        GetConnection gc = new GetConnection();

        String sql = "drop table IF EXISTS tmp_report1";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.addBatch();

            sql = "drop table IF EXISTS tailor_report1";
            gc.ps1.addBatch(sql);
            sql = "drop table IF EXISTS TMP_REPORT_ORD";
            gc.ps1.addBatch(sql);

            sql = "drop table IF EXISTS TAILOR_REPORT_ORD";
            gc.ps1.addBatch(sql);

            int count = gc.ps1.executeBatch().length;
            return count > 0;

        } catch (SQLException e) {
            logger.error(e);

            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();
        }

        logger.info("end  of create table and store values");
        return false;
    }

    /**
     * Naveen - 5th Feb 2015
     */
    public ArrayList<ArrayList<String>> generateTailorReport(int uniformId, ArrayList<String> classes,
            ArrayList<String> sex, ArrayList<String> schoolIds, String startDate, String endDate) {

        logger.info("start of generate tailor report");

        ArrayList<ArrayList<String>> myList = new ArrayList<ArrayList<String>>();

        if (prepareTailorReport(uniformId, classes, sex, schoolIds, startDate, endDate) > 0) {

            // not used because checking only with number of uniforms
            // boolean hasColor = checkColorForUniform(uniformId, classes, sex,
            // schoolIds);
            int noOfUniforms = getNumberOfUniforms(uniformId);

            GetConnection gc = new GetConnection();
            // checking if the uniform has color
            if (checkColorForUniform(uniformId, classes, sex, schoolIds)) {
                logger.info("Uniform has Color");
                try {
                    String sql = "select * from tailor_report1";
                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                    gc.rs1 = gc.ps1.executeQuery();
                    gc.rsmd = gc.rs1.getMetaData();

                    int columnCount = gc.rsmd.getColumnCount();

                    // // naveen start 4th feb 2015 ////
                    String sql1 = "";
                    // 4 because smid is also included
                    if (columnCount == 5) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3) + ",  sum(qty) from tailor_report1 group by "
                                + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3);

                    } else if (columnCount == 4) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2)
                                + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                + gc.rsmd.getColumnName(2);
                    } else if (columnCount == 6) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3) + ", " + gc.rsmd.getColumnName(4)
                                + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                + gc.rsmd.getColumnName(2) + ", " + gc.rsmd.getColumnName(3) + ", "
                                + gc.rsmd.getColumnName(4);

                    } else {
                        return null;
                    }

                    logger.info("sql1@tailordao with color \n " + sql1);

                    gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                    gc.rs2 = gc.ps2.executeQuery();

                    while (gc.rs2.next()) {
                        ArrayList<String> tempList = new ArrayList<String>();

                        if (columnCount == 5) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                            tempList.add(gc.rs2.getString(4));
                        } else if (columnCount == 4) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                        } else if (columnCount == 6) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                            tempList.add(gc.rs2.getString(4));
                            tempList.add(gc.rs2.getString(5));
                        }

                        myList.add(tempList);
                    }

                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }

                return myList;
            } else {
                // else case if uniform does not have color
                logger.info("Uniform Does not have color");
                try {
                    if (noOfUniforms <= 3) {

                        // System.out.println("no of uniforms is <=3");

                        String sql = "select * from tailor_report1";
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                        gc.rs1 = gc.ps1.executeQuery();
                        gc.rsmd = gc.rs1.getMetaData();

                        int columnCount = gc.rsmd.getColumnCount();

                        // // naveen start 2nd feb ////

                        String sql1 = "";
                        // 4 because smid is also included
                        if (columnCount == 4) {
                            // for two measurment uniform
                            // System.out.println("col count = 4");
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2)
                                    + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                    + gc.rsmd.getColumnName(2);
                        } else if (columnCount == 3) {
                            // System.out.println("col count = 3");
                            // for one measurement uniform
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ",  sum(qty) from tailor_report1 group by "
                                    + gc.rsmd.getColumnName(1);
                        } else if (columnCount == 5) {
                            // System.out.println("col colunt = 5");
                            // for three measurement uniform
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                    + gc.rsmd.getColumnName(3) + ",  sum(qty) from tailor_report1 group by "
                                    + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                    + gc.rsmd.getColumnName(3);
                        } else {
                            // System.out.println("else return null no color");
                            return null;
                        }

                        logger.info("sql 1@tailordao " + sql1);

                        gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                        gc.rs2 = gc.ps2.executeQuery();

                        while (gc.rs2.next()) {

                            ArrayList<String> tempList = new ArrayList<String>();

                            if (columnCount == 4) {
                                // for two measurements
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                                tempList.add(gc.rs2.getString(3));
                            } else if (columnCount == 3) {
                                // for one measurement
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                            } else if (columnCount == 5) {
                                // for three measurement
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                                tempList.add(gc.rs2.getString(3));
                                tempList.add(gc.rs2.getString(4));
                            }

                            myList.add(tempList);
                        }

                        // System.out.println("mylist is " + myList);

                        // // naveen end 2nd feb ////

                        // commented by naveen
                        /*
                         * while(gc.rs1.next()){ ArrayList<String> tempList = new ArrayList<String>();
                         * for (int i = 1; i < columnCount; i++) {
                         * 
                         * tempList.add(gc.rs1.getString(i)); }
                         * 
                         * System.out.println(tempList); myList.add(tempList); }
                         */
                        return myList;
                    } else if (noOfUniforms > 3) {
                        String sql = "select * from tailor_report_ord";
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                        gc.rs1 = gc.ps1.executeQuery();
                        gc.rsmd = gc.rs1.getMetaData();

                        int columnCount = gc.rsmd.getColumnCount();

                        while (gc.rs1.next()) {
                            ArrayList<String> tempList = new ArrayList<String>();
                            for (int i = 1; i < columnCount; i++) {

                                tempList.add(gc.rs1.getString(i));
                            }
                            myList.add(tempList);
                        }
                        logger.info("----------" + myList);
                        // System.out.println("my list from generatedtailor report is "
                        // + myList);
                        return myList;
                    }
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                } finally {
                    GetConnection.closeConnection();

                }
            }
        }

        logger.info("end  of generate tailor report");
        return null;

    }

    /**
     * Naveen - 5th Feb 2015, edited with startOrderNumber and endOrderNumber on 04
     * Oct 2017
     */
    public ArrayList<ArrayList<String>> generateTailorReportOrderNumber(int uniformId, ArrayList<String> classes,
            ArrayList<String> sex, ArrayList<String> schoolIds, String startDate, String endDate, int startOrderNumber,
            int endOrderNumber) {

        logger.info("start of generate tailor report");

        ArrayList<ArrayList<String>> myList = new ArrayList<ArrayList<String>>();

        if (prepareTailorReportOrderNumber(uniformId, classes, sex, schoolIds, startDate, endDate, startOrderNumber,
                endOrderNumber) > 0) {

            // not used because checking only with number of uniforms
            // boolean hasColor = checkColorForUniform(uniformId, classes, sex,
            // schoolIds);
            int noOfUniforms = getNumberOfUniforms(uniformId);

            GetConnection gc = new GetConnection();
            // checking if the uniform has color
            if (checkColorForUniform(uniformId, classes, sex, schoolIds)) {
                logger.info("Uniform has Color");
                try {
                    String sql = "select * from tailor_report1";
                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                    gc.rs1 = gc.ps1.executeQuery();
                    gc.rsmd = gc.rs1.getMetaData();

                    int columnCount = gc.rsmd.getColumnCount();

                    // // naveen start 4th feb 2015 ////
                    String sql1 = "";
                    // 4 because smid is also included
                    if (columnCount == 5) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3) + ",  sum(qty) from tailor_report1 group by "
                                + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3);

                    } else if (columnCount == 4) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2)
                                + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                + gc.rsmd.getColumnName(2);
                    } else if (columnCount == 6) {
                        sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                + gc.rsmd.getColumnName(3) + ", " + gc.rsmd.getColumnName(4)
                                + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                + gc.rsmd.getColumnName(2) + ", " + gc.rsmd.getColumnName(3) + ", "
                                + gc.rsmd.getColumnName(4);

                    } else {
                        return null;
                    }

                    logger.info("sql1@tailordao with color \n " + sql1);

                    gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                    gc.rs2 = gc.ps2.executeQuery();

                    while (gc.rs2.next()) {
                        ArrayList<String> tempList = new ArrayList<String>();

                        if (columnCount == 5) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                            tempList.add(gc.rs2.getString(4));
                        } else if (columnCount == 4) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                        } else if (columnCount == 6) {
                            tempList.add(gc.rs2.getString(1));
                            tempList.add(gc.rs2.getString(2));
                            tempList.add(gc.rs2.getString(3));
                            tempList.add(gc.rs2.getString(4));
                            tempList.add(gc.rs2.getString(5));
                        }

                        myList.add(tempList);
                    }

                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }

                return myList;
            } else {
                // else case if uniform does not have color
                logger.info("Uniform Does not have color");
                try {
                    if (noOfUniforms <= 3) {

                        // System.out.println("no of uniforms is <=3");

                        String sql = "select * from tailor_report1";
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                        gc.rs1 = gc.ps1.executeQuery();
                        gc.rsmd = gc.rs1.getMetaData();

                        int columnCount = gc.rsmd.getColumnCount();

                        // // naveen start 2nd feb ////

                        String sql1 = "";
                        // 4 because smid is also included
                        if (columnCount == 4) {
                            // for two measurment uniform
                            // System.out.println("col count = 4");
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2)
                                    + ",  sum(qty) from tailor_report1 group by " + gc.rsmd.getColumnName(1) + ", "
                                    + gc.rsmd.getColumnName(2);
                        } else if (columnCount == 3) {
                            // System.out.println("col count = 3");
                            // for one measurement uniform
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ",  sum(qty) from tailor_report1 group by "
                                    + gc.rsmd.getColumnName(1);
                        } else if (columnCount == 5) {
                            // System.out.println("col colunt = 5");
                            // for three measurement uniform
                            sql1 = "select " + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                    + gc.rsmd.getColumnName(3) + ",  sum(qty) from tailor_report1 group by "
                                    + gc.rsmd.getColumnName(1) + ", " + gc.rsmd.getColumnName(2) + ", "
                                    + gc.rsmd.getColumnName(3);
                        } else {
                            // System.out.println("else return null no color");
                            return null;
                        }

                        logger.info("sql 1@tailordao " + sql1);

                        gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                        gc.rs2 = gc.ps2.executeQuery();

                        while (gc.rs2.next()) {

                            ArrayList<String> tempList = new ArrayList<String>();

                            if (columnCount == 4) {
                                // for two measurements
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                                tempList.add(gc.rs2.getString(3));
                            } else if (columnCount == 3) {
                                // for one measurement
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                            } else if (columnCount == 5) {
                                // for three measurement
                                tempList.add(gc.rs2.getString(1));
                                tempList.add(gc.rs2.getString(2));
                                tempList.add(gc.rs2.getString(3));
                                tempList.add(gc.rs2.getString(4));
                            }

                            myList.add(tempList);
                        }

                        // System.out.println("mylist is " + myList);

                        // // naveen end 2nd feb ////

                        // commented by naveen
                        /*
                         * while(gc.rs1.next()){ ArrayList<String> tempList = new ArrayList<String>();
                         * for (int i = 1; i < columnCount; i++) {
                         * 
                         * tempList.add(gc.rs1.getString(i)); }
                         * 
                         * System.out.println(tempList); myList.add(tempList); }
                         */
                        return myList;
                    } else if (noOfUniforms > 3) {
                        String sql = "select * from tailor_report_ord";
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

                        gc.rs1 = gc.ps1.executeQuery();
                        gc.rsmd = gc.rs1.getMetaData();

                        int columnCount = gc.rsmd.getColumnCount();

                        while (gc.rs1.next()) {
                            ArrayList<String> tempList = new ArrayList<String>();
                            for (int i = 1; i < columnCount; i++) {

                                tempList.add(gc.rs1.getString(i));
                            }
                            myList.add(tempList);
                        }
                        logger.info("----------" + myList);
                        // System.out.println("my list from generatedtailor report is "
                        // + myList);
                        return myList;
                    }
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                } finally {
                    GetConnection.closeConnection();

                }
            }
        }

        logger.info("end  of generate tailor report");
        return null;

    }

    public boolean checkColorForUniform(int uniformId, ArrayList<String> classes, ArrayList<String> sex,
            ArrayList<String> schoolIds) {
        logger.info("start of Check color for uniform");
        try {
            GetConnection gc = new GetConnection();
            gc.stmt = GetConnection.getMySQLConnection().createStatement();

            String pSchools = "";
            for (String temp : schoolIds) {
                pSchools = pSchools + "'" + temp + "',";
            }

            pSchools = pSchools.substring(0, pSchools.length() - 1);

            String pClasses = "";
            for (String temp : classes) {
                pClasses = pClasses + "'" + temp + "',";

            }
            pClasses = pClasses.substring(0, pClasses.length() - 1);

            String pSex = "";
            // assuming that boy and girl is given
            if (sex.size() > 1) {
                pSex = "'B', 'G'";
            } else {
                pSex = "'" + sex.get(0) + "'";
            }

            // query checking for houseid
            /*
             * String sql =
             * "select MAX(AU.HID) from studentmesurementdetails smd, orderdetail od," +
             * " mesurementorder mo, studentorder so, ASSIGNUNIFORM AU  where so.oid = od.oid "
             * + "and od.unid = mo.unid and  smd.mid=mo.mid " +
             * "and smd.smid in(select smid from studentmesurement where auid in (select auid "
             * + "from assignuniform where schid in("+pSchools+") and class in("+
             * pClasses+") and sex in("+pSex+")  " +
             * "and unid='"+uniformId+"' AND AU.AUID = AUID ))";
             */

            String sql = "select count(distinct color) from studentmesurement where smid in("
                    + "select smid from orderdetail where auid in (select auid from assignuniform" + " where schid in("
                    + pSchools + ") and class in(" + pClasses + ") and sex in(" + pSex + ")  and unid='" + uniformId
                    + "'))";

            // System.out.println("sql is ::: " + sql);

            logger.debug(sql.toString().replace('\'', ' '));

            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                return gc.rs1.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.error(e);

            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();

        }

        logger.info("end of Check color for uniform");

        return false;
    }

    public int getNumberOfUniforms(int uniformId) {

        logger.info("start of get number of uniforms");

        // the below simple code will work, so commented this code, only with
        // uniform to get number of
        // measurements for the uniform

        // 9th Feb 2015 - Naveen

        /*
         * String pSchools = ""; for (String temp : schoolIds) { pSchools = pSchools +
         * "'" + temp + "',"; }
         * 
         * pSchools = pSchools.substring(0, pSchools.length() - 1);
         * 
         * String pClasses = ""; for (String temp : classes) { pClasses = pClasses + "'"
         * + temp + "',";
         * 
         * } pClasses = pClasses.substring(0, pClasses.length() - 1);
         * 
         * String pSex = ""; // assuming that boy and girl is given if (sex.size() > 1)
         * { pSex = "'B', 'G'"; } else { pSex = "'" + sex.get(0) + "'"; }
         * 
         * String sql = "select  count(distinct(mo.name))" +
         * " from studentmesurementdetails smd, STUDENTMESUREMENT SM," +
         * " orderdetail od, mesurementorder mo, studentorder so, ASSIGNUNIFORM AU  where"
         * + " SM.SMID = SMD.SMID AND SM.AUID = AU.AUID AND so.oid = od.oid and" +
         * " od.unid = mo.unid and smd.mid=mo.mid and smd.smid in(select smid from" +
         * " studentmesurement where auid in (select auid from assignuniform " +
         * "where schid in(" + pSchools + ") and class in(" + pClasses + ") and sex in("
         * + pSex + ")  and unid='" + uniformId + "' AND AU.AUID = AUID ))";
         */

        String sql = "select count(name) from mesurementorder where unid = ?";

        try {
            GetConnection gc = new GetConnection();
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, uniformId);

            gc.rs1 = gc.ps1.executeQuery();

            gc.rs1.next();
            return gc.rs1.getInt(1);

        } catch (SQLException e) {
            logger.error(e);

            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();
        }

        logger.info("end of count number of uniforms");

        return 0;
    }

    /**
     * 
     * @param args
     * @see This method does not return any thing, but it will insert max number of
     *      counts of the uniform for ex: if skirt generally length & waist is taken
     *      and hip is left as it is, for few Children's we will take hip, if you
     *      don't take hip, in the tmp_report1 table only length & waist this method
     *      will insert 0.1 for those who has not got that values
     */

    public void setEqualUniforms(int uniformId) {

        GetConnection gc = new GetConnection();
        String sql = "select distinct smid from tmp_report1";

        int noOfUniforms = getNumberOfUniforms(uniformId);
        // System.out.println("no of uniforms " + noOfUniforms);

        List<String> uniformNames = getMeasurementOrdName(uniformId);
        // System.out.println("uniform names are " + uniformNames);

        // checking if we really need to update the table with logic as
        // (count(distinct smid) * noofuniforms) == (noofuniroms in tmp_report1)
        // if the method returns true it means that records have no problem
        // if not insert into tmp_report1

        if (!checkToInsertIntoTmp_report1(uniformId)) {
            try {
                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                gc.rs1 = gc.ps1.executeQuery();

                while (gc.rs1.next()) {
                    String sql1 = "select count(name) from tmp_report1 where smid = ?";
                    GetConnection gc1 = new GetConnection();

                    gc1.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
                    gc1.ps1.setInt(1, gc.rs1.getInt(1));
                    gc1.rs1 = gc1.ps1.executeQuery();

                    gc1.rs1.next();

                    // System.out.println("no of uniforms " + gc1.rs1.getInt(1)
                    // +" for smid is " + gc.rs1.getInt(1));

                    if (gc1.rs1.getInt(1) != noOfUniforms) {

                        for (String uniName : uniformNames) {
                            // checking to insert a record or not, if record is
                            // present then
                            // leave, if not insert with the values got as temp
                            // from uniformNames
                            String insertSQL = "select count(*) from tmp_report1 where smid = ? and name = ?";
                            gc1.ps3 = GetConnection.getMySQLConnection().prepareStatement(insertSQL);
                            gc1.ps3.setInt(1, gc.rs1.getInt(1));
                            gc1.ps3.setString(2, uniName);

                            gc1.rs3 = gc1.ps3.executeQuery();
                            gc1.rs3.next();

                            // System.out.println(gc1.rs3.getInt(1)
                            // +" no of uniforms for smid " +gc.rs1.getInt(1)
                            // +", ,," + uniName );
                            GetConnection gc2 = null;
                            if (gc1.rs3.getInt(1) == 0) {
                                try {
                                    // System.out.println("in insert... with values "
                                    // + uniName +", " + gc.rs1.getInt(1));

                                    String storeTmpRptSQL = "insert into tmp_report1 values(?,?,?,?)";

                                    gc2 = new GetConnection();
                                    gc2.ps1 = GetConnection.getMySQLConnection().prepareStatement(storeTmpRptSQL);
                                    gc2.ps1.setDouble(1, 0.1);
                                    gc2.ps1.setString(2, uniName);
                                    gc2.ps1.setInt(3, gc.rs1.getInt(1));
                                    gc2.ps1.setInt(4, getQtyUsingSMID(gc.rs1.getInt(1)));

                                    // System.out.println("qty for update is " +
                                    // getQtyUsingSMID(gc.rs1.getInt(1)));
                                    gc2.ps1.executeUpdate();
                                } catch (SQLException sqle) {
                                    logger.info(sqle);
                                } finally {
                                    // if(gc2.rs1 != null) gc.rs1.close();
                                    // if(gc2.ps1 !=null) gc.ps1.close();
                                    GetConnection.getMySQLConnection().close();
                                }
                            }
                        }
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
                    GetConnection.getMySQLConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        } else {
            logger.info("tmp_report1 table has noofuniforms * count ( distinct name) == count(name)");
        }
    }

    public int getQtyUsingSMID(int smid) {
        // System.out.println("uni smid @getqtyusingsmidnname " + smid);

        String sql = "select qty from tmp_report1 where smid = ?";
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, smid);

            gc.rs1 = gc.ps1.executeQuery();
            gc.rs1.next();

            return gc.rs1.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null) {
                    gc.rs1.close();
                }
                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                GetConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return 0;
    }

    // this method will check should there be insertion or not in the table

    public boolean checkToInsertIntoTmp_report1(int uniformId) {
        int noOfUniforms = getNumberOfUniforms(uniformId);

        String sql = "select count(distinct smid) from tmp_report1";
        String sql1 = "select count(smid) from tmp_report1";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql1);

            gc.rs1 = gc.ps1.executeQuery();

            gc.rs2 = gc.ps2.executeQuery();

            gc.rs1.next();
            gc.rs2.next();

            // System.out.println("distinct count * no of uniforms "+
            // gc.rs1.getInt(1)*noOfUniforms);
            // System.out.println("no of total count in tmp_report1 " +
            // gc.rs2.getInt(1));
            return (gc.rs1.getInt(1) * noOfUniforms) == gc.rs2.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if (gc.rs1 != null) {
                    gc.rs1.close();
                }
                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                if (gc.ps2 != null) {
                    gc.ps2.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    // this method will give names from mesuremnetorder table, given uniformId

    public List<String> getMeasurementOrdName(int uniformId) {
        List<String> myList = new ArrayList<String>();
        String sql = "select name from mesurementorder where unid =?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, uniformId);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                myList.add(gc.rs1.getString(1));
            }

            return myList;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null) {
                    gc.rs1.close();
                }
                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public static void main1(String[] args) {

        /*
         * ArrayList<String> classes = new ArrayList<String>(); classes.add("4");
         * 
         * ArrayList<String> sex = new ArrayList<String>(); sex.add("G"); //
         * sex.add("G");
         * 
         * ArrayList<String> schoolIds = new ArrayList<String>(); schoolIds.add("2");
         */
        // System.out.println(new TailorDAO().getNumberOfUniforms(1, classes,
        // sex, schoolIds));

        // System.out.println(new TailorDAO().generateTailorReport(8,
        // classes,sex, schoolIds,"",""));

        // System.out.println(new TailorDAO().checkColorForUniform(1, classes,
        // sex, schoolIds));
        // System.out.println(new TailorDAO().checkColorForUniform(10, classes,
        // sex, schoolIds));
        // System.out.println(new TailorDAO().checkToInsertIntoTmp_report1(2));

        // new TailorDAO().setEqualUniforms(8);

        ArrayList<ArrayList<Object>> report = new TailorDAO().getTailorReportBeforeOrder(2, "NUR", 2, "B");
        System.out.println(report);
        //
        System.out.println("created..done  ");
        // Iterator itr = map.entrySet().iterator();
        //
        // while(itr.hasNext()){
        // Entry<Double,Double> temp = (Entry<Double, Double>) itr.next();
        //
        // System.out.println(temp.getKey() +"\t " + temp.getValue());
        // }
        //

    }

    // This report is to get the tailor report after measurement is taken and
    // before order, this is new requirement
    // Naveen - 27-Dec-2015

    public ArrayList<ArrayList<Object>> getTailorReportBeforeOrder(int schoolId, String cls, int uniformId,
            String sex) {
        logger.info("Schoold id got is " + schoolId + ", Class " + cls + ", Uniform Id " + uniformId
                + ", for getTrailorReportBeforeOrder");

        String sql1 = "DROP TABLE IF EXISTS before_order_temp";
        String sql6 = "DROP TABLE IF EXISTS GENERATE_REPORT_BEFORE_ORDER";
        String sql2 = "create table before_order_temp as select sm.smid, smd.smdid,  sm.stid, mo.name,"
                + " smd.value, un.uname from studentmesurementdetails smd, mesurementorder mo, "
                + "studentmesurement sm, assignuniform au, uniform un where smd.smid in "
                + "(select smid from studentmesurement where stid in (select stid from student "
                + "where schid=? and class=?)) and  smd.mid = mo.mid  and sm.smid = smd.smid and "
                + "sm.auid = au.auid and au.unid = un.unid and un.unid =? and au.sex=? order by smd.smid";
        // this query will help to generate group_concat query to know how many
        // measurements are there
        // for the uniform
        String sql3 = "select distinct name from before_order_temp";

        GetConnection gc = null;
        try {
            gc = new GetConnection();

            // drop existing tables
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.executeUpdate();
            logger.info("Statement Execute-> " + sql1);
            gc.ps6 = GetConnection.getMySQLConnection().prepareStatement(sql6);
            gc.ps6.executeUpdate();

            gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sql2);
            gc.ps2.setInt(1, schoolId);
            gc.ps2.setString(2, cls);
            gc.ps2.setInt(3, uniformId);
            gc.ps2.setString(4, sex);

            gc.ps2.executeUpdate();

            gc.ps3 = GetConnection.getMySQLConnection().prepareStatement(sql3);
            gc.rs1 = gc.ps3.executeQuery();

            ArrayList<String> measurementNames = new ArrayList<String>();
            while (gc.rs1.next()) {
                measurementNames.add(gc.rs1.getString(1));
            }
            System.out.println(measurementNames);

            // Naveen - 6-jan-2016
            // This is only for reference dont delete
            // SELECT
            // stid,
            // GROUP_CONCAT(if(name='LENTH',value,NULL)) AS 'LENTH',
            // GROUP_CONCAT(if(name='WIDTH',value,NULL)) AS 'WIDTH'
            // FROM deletetable group by stid;

            String sql4 = "create table GENERATE_REPORT_BEFORE_ORDER as SELECT ";
            for (String temp : measurementNames) {
                sql4 += "GROUP_CONCAT(if(name='" + temp + "',value,NULL)) AS '" + temp + "',";
            }
            sql4 = sql4.substring(0, sql4.length() - 1);

            sql4 += " FROM before_order_temp group by stid";

            logger.info(Utilities.removeSingleQuotes("Query generated for Before Order is " + sql4));

            gc.ps4 = GetConnection.getMySQLConnection().prepareStatement(sql4);
            gc.ps4.executeUpdate();

            String attributes = "";
            for (String temp : measurementNames) {
                attributes += temp + ",";
            }
            attributes = attributes.substring(0, attributes.length() - 1);

            String sql5 = "select count(*)," + attributes + " from GENERATE_REPORT_BEFORE_ORDER " + " group by "
                    + attributes + " order by count(*)";

            // String sql5 ="select count(*)," + attributes + " from
            // GENERATE_REPORT_BEFORE_ORDER where " +
            // measurementNames.get(0) +" > 0.5 group by "+ attributes +" order by
            // count(*)";

            logger.info(Utilities.removeSingleQuotes("Query generated for  count of each measurement " + sql5));

            gc.ps5 = GetConnection.getMySQLConnection().prepareStatement(sql5);
            gc.rs2 = gc.ps5.executeQuery();
            gc.rsmd = gc.rs2.getMetaData();

            ArrayList<ArrayList<Object>> report = new ArrayList<ArrayList<Object>>();

            while (gc.rs2.next()) {
                ArrayList<Object> list = new ArrayList<Object>();
                list.add(gc.rs2.getInt(1));

                // System.out.print(gc.rs2.getInt(1) +" ");
                for (String temp : measurementNames) {

                    // System.out.println("temp : "+temp +", "+gc.rs2.getString(temp) + "\t");
                    list.add(gc.rs2.getDouble(temp));
                }

                report.add(list);
            }

            return report;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.ps2 != null) {
                    gc.ps2.close();
                }
                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 
     * @param schoolId
     * @param clas
     * @param sex
     * @see this method does not have the GUI for end user this was create only for
     */
    public void deleteExtraMeasurement(int schoolId, String cls, String sex) {

        logger.info("Processing " + schoolId + ", Class " + cls + ", sex " + sex);

        GetConnection gc = new GetConnection();
        try {
            List<UniformBean> uniformList = new UniformDAO().getAssignedUniform(schoolId, cls, sex);

            for (UniformBean uniform : uniformList) {
                // this query will run n number of times for each uniform fetched for schoo,
                // class, sex

                String createViewSql = "create or replace  view  view_before_order as select sm.smid, smd.smdid,  sm.stid, mo.name, "
                        + "smd.value, un.uname from studentmesurementdetails smd, mesurementorder mo,"
                        + " studentmesurement sm, assignuniform au, uniform un where smd.smid in "
                        + "(select smid from studentmesurement where stid in (select stid from student "
                        + "where schid=? and class=?)) and  smd.mid = mo.mid  and sm.smid = smd.smid and "
                        + "sm.auid = au.auid and au.unid = un.unid and un.unid =? and au.sex=?  order by smd.smid";

                String numberOfMeasurementSql = "select count(*)  from mesurementorder where unid = ?";
                // the parameter here is numberOfMeasurementSql
                String studentsWrongUniformSql = "select stid, count(stid) from view_before_order group by stid having count(stid) >  ?";

                String studentMesurementDetailsDeleteSql = "delete  from studentmesurementdetails where SMID in "
                        + "(select smid from studentmesurement where stid = ?)";

                String studentMesurementDeleteSql = "delete from studentmesurement where stid = ?";

                String updateStudentMeasurementSql = "update student set MEASUREMENTDATE = null where stid = ?";

                logger.info("processing for -> Uniform ID:  " + uniform.getUniformId() + ", " + uniform.getuName());

                // Step1 -> Create the view

                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(createViewSql);
                gc.ps1.setInt(1, schoolId);
                gc.ps1.setString(2, cls);
                gc.ps1.setInt(3, uniform.getUniformId());
                gc.ps1.setString(4, sex);

                gc.ps1.executeUpdate();

                gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(numberOfMeasurementSql);
                gc.ps2.setInt(1, uniform.getUniformId());
                gc.rs1 = gc.ps2.executeQuery();
                gc.rs1.next();

                int numberOfMeasurements = gc.rs1.getInt(1);

                // the students who has got wrong uniforms
                gc.ps3 = GetConnection.getMySQLConnection().prepareStatement(studentsWrongUniformSql);
                gc.ps3.setInt(1, numberOfMeasurements);

                gc.rs2 = gc.ps3.executeQuery();

                while (gc.rs2.next()) {
                    int tempStudentId = gc.rs2.getInt(1);
                    logger.info("Student Id " + tempStudentId + ", count " + gc.rs2.getInt(2));

                    // deleting student measurement details
                    gc.ps4 = GetConnection.getMySQLConnection().prepareStatement(studentMesurementDetailsDeleteSql);
                    gc.ps4.setInt(1, tempStudentId);
                    gc.ps4.executeUpdate();

                    // delete student measurement
                    gc.ps5 = GetConnection.getMySQLConnection().prepareStatement(studentMesurementDeleteSql);
                    gc.ps5.setInt(1, tempStudentId);
                    gc.ps5.executeUpdate();

                    // update student mesauremdate to null;

                    gc.ps6 = GetConnection.getMySQLConnection().prepareStatement(updateStudentMeasurementSql);
                    gc.ps6.setInt(1, tempStudentId);
                    gc.ps6.executeUpdate();

                }

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error("View did not create", e);
        } finally {
            try {
                if (gc.ps2 != null) {
                    gc.ps2.close();
                }
                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                if (gc.ps3 != null) {
                    gc.ps3.close();
                }
                if (gc.ps4 != null) {
                    gc.ps4.close();
                }
                if (gc.ps5 != null) {
                    gc.ps5.close();
                }
                if (gc.ps6 != null) {
                    gc.ps6.close();
                }
                GetConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void main(int schoolId) {

        String cls = "NUR:LKG:UKG:1:2:3:4:5:6:7:8:9:10:11:12";
        String sex = "B:G";

        for (String tempClass : cls.split(":")) {
            for (String tempSex : sex.split(":")) {
                new TailorDAO().deleteExtraMeasurement(schoolId, tempClass, tempSex);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
