package com.philips.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.philips.beans.HouseBean;
import com.philips.beans.MesurementOrderBean;
import com.philips.beans.UniformBean;
import com.philips.connection.GetConnection;;

/**
 * 
 * @author naveenkumar
 *
 */
public class UniformDAO {

    static Logger logger = GetConnection.getLogger(UniformDAO.class);

    public boolean insertUniform(String uniformName, String shortCode, String sp, String hsn, int uId) {
        String sql = "insert into uniform (uname, short_code, uid,sp, hsn) values(?,?,?,?,?)";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, uniformName.toUpperCase());
            gc.ps1.setString(2, shortCode.toUpperCase());
            gc.ps1.setInt(3, uId);
            gc.ps1.setString(4, sp);
            gc.ps1.setString(5, hsn);
            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            logger.info("Uniform Added with  name " + uniformName);
            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public List<UniformBean> getAllUniform() {
        String sql = "SELECT unid, uname, short_code, uid, sp, hsn FROM uniform";
        // String sql = "select * from uniform";

        List<UniformBean> uniformList = new ArrayList<UniformBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                UniformBean temp = new UniformBean();
                temp.setUniformId(gc.rs1.getInt(1));
                temp.setuName(gc.rs1.getString(2));
                temp.setShortCode(gc.rs1.getString(3));
                temp.setSp(gc.rs1.getString(5));
                temp.setHsn(gc.rs1.getString(6));
                uniformList.add(temp);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
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

        return uniformList;
    }

    public List<UniformBean> getAssignedUniform(int schId, String cls, String sex) {
        String sql = "select u.unid, u.uname, u.short_code from uniform u, "
                + "ASSIGNUNIFORM AU where u.unid = au.unid and " + "au.schid = ? and class = ? and sex=? ";

        List<UniformBean> assignUniform = new ArrayList<UniformBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);
            gc.ps1.setString(3, sex);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                UniformBean temp = new UniformBean();

                temp.setUniformId(gc.rs1.getInt(1));
                temp.setuName(gc.rs1.getString(2));
                temp.setShortCode(gc.rs1.getString(3));

                assignUniform.add(temp);

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

        return assignUniform;

    }

    public void insertUniform(int schId, String cls, String sex, String[] uniformIds, int uid) {
        String sql = "insert into assignuniform (SCHID , CLASS , SEX , UNID, UID) values(?,?,?,?,?)";

        GetConnection gc = new GetConnection();

        for (int i = 0; i < uniformIds.length; i++) {

            try {
                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                gc.ps1.setInt(1, schId);
                gc.ps1.setString(2, cls.toUpperCase());
                gc.ps1.setString(3, sex.toUpperCase());
                gc.ps1.setString(4, uniformIds[i].toUpperCase());
                gc.ps1.setInt(5, 1);
                gc.ps1.executeUpdate();

            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } finally {

                try {
                    // if(gc.rs1 != null) gc.rs1.close();
                    if (gc.ps1 != null)
                        gc.ps1.close();
                    GetConnection.getMySQLConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 
     * @param schId
     * @param cls
     * @param sex
     * @return
     * @see tax added by naveen added ordersOrder by Naveen 19JAN2016 changed again
     *      on 26 JULY 2017 after GST taken off tax in sql query and got cgst, sgst,
     *      igst
     */
    public ArrayList<ArrayList<Object>> getAssignedPriceUniform(int schId, String cls, String sex) {

        String sql = "select au.auid, u.uname, au.cost, au.cgst, sgst, igst, au.ordersorder, au.couriercost from assignuniform au, "
                + "uniform u where au.schid = ? and class =? and sex =? and au.unid = u.unid";

        GetConnection gc = new GetConnection();

        ArrayList<ArrayList<Object>> myobj = new ArrayList<ArrayList<Object>>();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls.toUpperCase());
            gc.ps1.setString(3, sex.toUpperCase());

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ArrayList<Object> temp = new ArrayList<Object>();
                temp.add(gc.rs1.getInt(1)); // auid
                temp.add(gc.rs1.getString(2)); // uniform name
                temp.add(gc.rs1.getDouble(3)); // cost
                temp.add(gc.rs1.getDouble(4)); // cgst
                temp.add(gc.rs1.getDouble(5)); // sgst
                temp.add(gc.rs1.getDouble(6)); // igst
                temp.add(gc.rs1.getInt(7)); // ordersorder
                temp.add(gc.rs1.getDouble(8)); // courier cost
                myobj.add(temp);
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

        return myobj;
    }

    // this function will update the cost in the assignuniform table
    // accepting one more paramenter of type in ordersOrder for taking order while
    // giving purchase order

    public boolean insertPrice(double price, int auId, double cgst, double sgst, double igst, int ordersOrder,
            double courierCost) {

        // updated by naveen for tax - 06Nov2015
        // updated by Naveen for OrdersOrder - 19JAN2016
        // taken off tax and got cgst, sgst, igst in sql query
        String sql = "update assignuniform set cost=?, cgst=?, sgst=?, igst=?, ordersorder=?, couriercost=? where auid=?";
        logger.info("Courier Cost Got is " + courierCost);
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setDouble(1, price);
            gc.ps1.setDouble(2, cgst);
            gc.ps1.setDouble(3, sgst);
            gc.ps1.setDouble(4, igst);
            gc.ps1.setInt(5, ordersOrder);
            gc.ps1.setDouble(6, courierCost);
            gc.ps1.setInt(7, auId);

            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();
            try {
                gc.ps1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // either all mesurements to be stored or nothing to be stored..
    // so used execute batch...
    public boolean addMesurement(ArrayList<MesurementOrderBean> myList) {

        String sql = "insert into mesurementorder(name, morder, uid, unid) values(?,?,?,?)";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            for (MesurementOrderBean temp : myList) {
                gc.ps1.setString(1, temp.getName().toUpperCase());
                gc.ps1.setInt(2, temp.getmOrder());
                gc.ps1.setInt(3, temp.getuId());
                gc.ps1.setInt(4, temp.getUnId());

                gc.ps1.addBatch();
            }

            return gc.ps1.executeBatch().length > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    // this is used in AssignHouseForm.jsp
    public List<HouseBean> getAllHouses() {
        String sql = "select hid, color1, color2, color3, color4 from house";

        GetConnection gc = new GetConnection();
        List<HouseBean> myList = new ArrayList<HouseBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.rs1 = gc.ps1.executeQuery();
            while (gc.rs1.next()) {
                HouseBean hb = new HouseBean();
                hb.sethId(gc.rs1.getInt(1));
                hb.setColor1(gc.rs1.getString(2).toUpperCase());
                hb.setColor2(gc.rs1.getString(3).toUpperCase());
                hb.setColor3(gc.rs1.getString(4));
                hb.setColor4(gc.rs1.getString(5));

                myList.add(hb);

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
        return myList;

    }

    /**
     * 
     * @param schId
     * @param cls
     * @param sex
     * @param uniformId
     * @param houseId
     * @return boolean
     * 
     * @see method to update houseid in assignuniform table
     */
    public boolean updateUniformHouse(int schId, String cls, String sex, int uniformId, int houseId) {
        String sql = "update assignuniform set hid = ? where schid = ? and class = ? and  sex = ? and unid= ?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setInt(1, houseId);
            gc.ps1.setInt(2, schId);
            gc.ps1.setString(3, cls);
            gc.ps1.setString(4, sex);
            gc.ps1.setInt(5, uniformId);

            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    // auid, UNID, uname
    public TreeMap<Integer, ArrayList<MesurementOrderBean>> getMesurementOrder(List<List<Object>> uniList) {
        // uniform id, mesurementorder bean
        // System.out.println("varish inList"+uniList);
        TreeMap<Integer, ArrayList<MesurementOrderBean>> myMap = new TreeMap<Integer, ArrayList<MesurementOrderBean>>();

        String sql = "select mo.mid, mo.name, mo.morder, au.auid from mesurementorder mo,assignuniform au where au.auid = "
                + "? and au.unid=mo.unid order by morder";

        GetConnection gc = new GetConnection();

        for (List<Object> temp : uniList) {

            // System.out.println("number of uniforms is " + uniList.size());

            Object tt[] = temp.toArray();

            // System.out.println(" tt lenght is " + tt.length);
            for (int i = 0; i < tt.length; i++) {
                if (i == 0) {
                    // System.out.println("uniform id is " + tt[i].toString());

                    try {
                        gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                        gc.ps1.setInt(1, Integer.parseInt(tt[i].toString()));
                        gc.rs1 = gc.ps1.executeQuery();

                        ArrayList<MesurementOrderBean> mo = new ArrayList<MesurementOrderBean>();

                        while (gc.rs1.next()) {
                            // (mId, name, mOrder, uId, unId)
                            mo.add(new MesurementOrderBean(gc.rs1.getInt(1), gc.rs1.getString(2), gc.rs1.getInt(3),
                                    gc.rs1.getInt(4), Integer.parseInt(tt[i].toString())));

                            myMap.put(Integer.parseInt(tt[i].toString()), mo);
                            // System.out.println("yes="+Integer.parseInt(tt[i].toString()));
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
                }
            }
        }

        // System.out.println("no of uniforms matched is " + myMap.size());

        return myMap;
    }

    // this function is used in takemesurement3.jsp, for displaying uniform name
    public String getUniformName(int auId) {
        // System.out.println("vari="+unId);
        String sql = "select uname from uniform u,assignuniform au where au.auid = ? and au.unid=u.unid";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, auId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getString(1);

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
        return "No Uniform Name";
    }

    // this function is used in shottailorreport.jsp, for displaying uniform name
    public String getUniformByName(int unId) {
        // System.out.println("vari="+unId);
        String sql = "select uname from uniform where unid = ? ";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, unId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getString(1);

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

        return "No Uniform Name";

    }

    // this function is used in takemesurement3.jsp, for displaying uniform
    // shortcode
    public String getUniformShortCode(int auId) {
        String sql = "select short_code from uniform u,assignuniform au where au.auid = ? and au.unid=u.unid";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, auId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getString(1);

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

        return "No Uniform Name";

    }

    /**
     * @param stId
     * @param unId
     * @return
     * @see this method is used in TakeMesurement3.jsp for showing house, since you
     *      can get house number from combination of sex, class, school, uniformId
     */
    public int getAssignUniformId(String usn, int schid, String class1, int auId) {

        // have to extract the usn from KANCHAN-USN1
        // usn = usn.substring(usn.indexOf("~~~") + 1, usn.length());

        // System.out.println("usn is varish" + usn);
        // System.out.println("unid varish: " + auId);

        String sql = "SELECT au.unid FROM ASSIGNUNIFORM AU WHERE AU.CLASS = ? AND "
                + "AU.SEX = (SELECT SEX FROM STUDENT ss WHERE ss.usn = ? and ss.class=? and ss.schid=? ) and"
                + " au.schid = ? and au.auid = ? order by au.auid";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, class1);
            gc.ps1.setString(2, usn);
            gc.ps1.setString(3, class1);
            gc.ps1.setInt(4, schid);
            gc.ps1.setInt(5, schid);
            gc.ps1.setInt(6, auId);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getInt(1);

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
        // check for zero if auid is not found
        return 0;
    }

    /**
     * this function is used in TakeMesurementController.java for sending unid take
     * measurement order
     * 
     * @return map
     * @see : the map will contain uniformId as key and list mesurement ids which is
     *      used to do request.getparmeter of the parmeters passed.
     */

    public ArrayList<MesurementOrderBean> getMesurementOrderunId(int unid) {
        // System.out.println("arraylist="+unid);
        ArrayList<MesurementOrderBean> mesureOrder = new ArrayList<MesurementOrderBean>();

        String sql = "select mid, name, morder, unid from mesurementorder where unid = ? order by morder";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, unid);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                MesurementOrderBean temp = new MesurementOrderBean();
                temp.setmId(gc.rs1.getInt(1));
                temp.setName(gc.rs1.getString(2));
                temp.setmOrder(gc.rs1.getInt(3));
                temp.setUnId(gc.rs1.getInt(4));

                mesureOrder.add(temp);
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

        return mesureOrder;
    }

    public int getsmId(int auid, int stid) {

        String sql = "select smid from studentmesurement where auid = ? and stid =?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, auid);
            gc.ps1.setInt(2, stid);
            // gc.ps1.setInt(3, uid);
            // gc.ps1.setString(4, color);

            if ((gc.rs1 = gc.ps1.executeQuery()).next())
                return gc.rs1.getInt(1);

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
        return 0;
    }

    public boolean updateStudentMesurementColor(int smid, String color) {

        String sql = "update studentmesurement set color =? where smid =?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, color);
            gc.ps1.setInt(2, smid);

            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    // auids[i], stid, color, uid
    public int insertStudentMesurement(int auid, int stid, String color, int uid) {

        int smid = getsmId(auid, stid);
        // System.out.println("auid " + auid +", stid " + stid +", color " + color +",
        // smid " + smid);

        if (smid > 0) {
            // update color and student measurement record is found
            return updateStudentMesurementColor(smid, color) == true ? smid : 0;
        } else {
            String sql = "insert into studentmesurement (auid, stid, uid, color) values(?,?,?,?)";
            // auid=?, stid=?, uid =?,

            GetConnection gc = new GetConnection();
            try {
                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS);

                gc.ps1.setInt(1, auid);
                gc.ps1.setInt(2, stid);
                gc.ps1.setInt(3, uid);
                gc.ps1.setString(4, color);

                if (gc.ps1.executeUpdate() > 0) {
                    gc.rs1 = gc.ps1.getGeneratedKeys();
                    gc.rs1.next();
                    return gc.rs1.getInt(1);

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
            return 0;
        }
    }

    public int getsmdid(int mid, int smid) {

        String sql = "select smdid from studentmesurementdetails where mid = ? and smid =?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, mid);
            gc.ps1.setInt(2, smid);

            if ((gc.rs1 = gc.ps1.executeQuery()).next())
                return gc.rs1.getInt(1);

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
        return 0;
    }

    public boolean updateStuMesureDetailSize(int smdid, double value) {

        String sql = "update STUDENTMESUREMENTDETAILS set value =? where smdid =?";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setDouble(1, value);
            gc.ps1.setInt(2, smdid);

            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    // Integer.parseInt(req), mob.getmId(), smid
    public boolean insertStudentMesurementDetails(double value, int mid, int smid) {
        // System.out.println(value);
        if (value > 0.0) {
            int smdid = getsmdid(mid, smid);
            if (smdid > 0) {
                updateStuMesureDetailSize(smdid, value);
            } else {

                GetConnection gc = new GetConnection();
                String sql = "insert into STUDENTMESUREMENTDETAILS (mid, smid, value) values(?,?,?)";

                try {
                    gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                    gc.ps1.setInt(1, mid);
                    gc.ps1.setInt(2, smid);
                    gc.ps1.setDouble(3, value);

                    return gc.ps1.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {

                    try {
                        // if(gc.rs1 != null) gc.rs1.close();
                        if (gc.ps1 != null)
                            gc.ps1.close();
                        GetConnection.getMySQLConnection().close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

        return false;
    }

    public double getStuMesDetailValue(int stId, int auid, int mid) {

        String sql = "select smd.value from studentmesurementdetails smd, STUDENTMESUREMENT sm, MESUREMENTORDER mo "
                + "where sm.stid = ? and sm.auid = ? and smd.smid = sm.smid  "
                + "and smd.mid = mo.mid and mo.mid = ? order by smd.smdid";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setInt(1, stId);
            gc.ps1.setInt(2, auid);
            gc.ps1.setInt(3, mid);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getDouble(1);
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

        return 0;
    }

    public String getStudentMesurementColor(int auid, int stId) {
        String sql = "select color from STUDENTMESUREMENT where auid =? and stid =?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setInt(1, auid);
            gc.ps1.setInt(2, stId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next())
                return gc.rs1.getString(1);

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
        return "";

    }

    /**
     * @author naveenkumar
     * @since Oct 09 2016
     * @param auid
     * @see Called From DeleteAssignedUniformController in turn from
     *      DeleteUniformForm.jsp
     * 
     */
    public boolean deleteExistingUniform(int auid) {
        boolean flag = false;
        String sql1 = "delete from studentmesurementdetails where smid in (select smid from studentmesurement where auid = "
                + auid + ")";
        String sql2 = "delete from orderdetail where auid = " + auid;
        String sql3 = "delete from studentmesurement where auid = " + auid;
        String sql4 = "delete from assignuniform where auid = " + auid;
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql1);
            gc.ps1.addBatch();
            gc.ps1.addBatch(sql2);
            gc.ps1.addBatch(sql3);
            gc.ps1.addBatch(sql4);

            logger.info("Deletion request got for " + auid);
            return gc.ps1.executeBatch().length > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /*
    *//**
        * 
        * @param schoolId
        * @param clas
        * @param sex
        * @return
        * 
        *         This method is not used...
        *//*
           * 
           * public List<Integer> getAuid(int schoolId, String cls, String sex){ String
           * sql =
           * "select auid  from assignuniform where SCHID = ? and class=? and sex =?";
           * 
           * List<Integer> auidList = new ArrayList<Integer>();
           * 
           * GetConnection gc = new GetConnection(); try { gc.ps1 =
           * GetConnection.getMySQLConnection().prepareStatement(sql); gc.ps1.setInt(1,
           * schoolId); gc.ps1.setString(2, cls); gc.ps1.setString(3, sex);
           * 
           * gc.rs1 = gc.ps1.executeQuery();
           * 
           * while (gc.rs1.next()) {
           * 
           * auidList.add(gc.rs1.getInt(1)); }
           * 
           * } catch (SQLException e) { e.printStackTrace(); }finally{
           * 
           * try { if(gc.rs1 != null) gc.rs1.close(); if(gc.ps1 !=null) gc.ps1.close();
           * GetConnection.getMySQLConnection().close(); } catch (SQLException e) {
           * e.printStackTrace(); }
           * 
           * }
           * 
           * return auidList; }
           * 
           */

}
