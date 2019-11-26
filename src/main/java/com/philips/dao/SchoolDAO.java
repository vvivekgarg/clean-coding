package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.philips.beans.SchoolBean;
import com.philips.connection.GetConnection;

/**
 * 
 * 
 * @author naveenkumar
 *
 */
public class SchoolDAO {
    static Logger logger = GetConnection.getLogger(SchoolDAO.class);

    public List<SchoolBean> getAllSchool() {
        String sql = "select schid,sname,shortcode,street,city,state,pin,inperson,phno,mobile,email,vat,vatpercent,comments,uid from school where schid > 1";

        List<SchoolBean> schoolList = new ArrayList<SchoolBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                SchoolBean temp = new SchoolBean();
                temp.setSchId(gc.rs1.getInt(1));
                temp.setSchName(gc.rs1.getString(2));
                temp.setShortCode(gc.rs1.getString(3));
                temp.setStreet(gc.rs1.getString(4));
                temp.setCity(gc.rs1.getString(5));
                temp.setState(gc.rs1.getString(6));
                temp.setPin(gc.rs1.getString(7));
                temp.setInPerson(gc.rs1.getString(8));
                temp.setSchPhone(gc.rs1.getString(9));
                temp.setInPersonMobile(gc.rs1.getString(10));
                temp.setInPersonEmail(gc.rs1.getString(11));
                temp.setVat(gc.rs1.getString(12));
                temp.setVatPercent(gc.rs1.getString(13));
                temp.setComments(gc.rs1.getString(14));
                temp.setUid(gc.rs1.getInt(15));

                schoolList.add(temp);
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

        return schoolList;

    }

    /**
     * 
     * @param schId
     * @return
     * @see added lastorderconfirmdate, and also altered SchoolBean, the same is
     *      used in ShowMeasurement.jsp, as the message to be shown to the parent to
     *      say them when is the last date for the change in measurement
     */
    public SchoolBean getSchool(int schId) {
        String sql = "select schid, sname, shortcode, lastorderconfirmdate, DATE_FORMAT(lastorderconfirmdate, '%d %M %Y') from school where schid =?";

        GetConnection gc = new GetConnection();
        SchoolBean sb = null;

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {

                sb = new SchoolBean();
                sb.setSchId(gc.rs1.getInt(1));
                sb.setSchName(gc.rs1.getString(2));
                sb.setStreet(gc.rs1.getString(3));
                sb.setLastorderconfirmdate(gc.rs1.getString(4));
                sb.setLastOrderConfirmDateInDDMMYYYY(gc.rs1.getString(5));
                // put other
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

    public static void main(String[] args) {
        System.out.println(new SchoolDAO().getSchool(2).getLastOrderConfirmDateInDDMMYYYY());
    }

    public boolean insertSchool(SchoolBean sb, int uid) {
        String sql = "insert into school (sname, street,city, state, pin,inperson,phno,mobile,email,vat,vatpercent,comments,uid,shortcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, sb.getSchName().toUpperCase());
            gc.ps1.setString(2, sb.getStreet().toUpperCase());
            gc.ps1.setString(3, sb.getCity().toUpperCase());
            gc.ps1.setString(4, sb.getState().toUpperCase());
            gc.ps1.setString(5, sb.getPin());
            gc.ps1.setString(6, sb.getInPerson().toUpperCase());
            gc.ps1.setString(7, sb.getSchPhone());
            gc.ps1.setString(8, sb.getInPersonMobile());
            gc.ps1.setString(9, sb.getInPersonEmail());
            gc.ps1.setString(10, sb.getVat().toUpperCase());
            gc.ps1.setString(11, sb.getVatPercent());
            gc.ps1.setString(12, sb.getComments());
            gc.ps1.setInt(13, uid);
            gc.ps1.setString(14, sb.getShortCode().toUpperCase());

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

    // this method is not used after GST - commented on 29 DEC 2017
    // naveen
    // used in MakeOrder2.jsp
    public double getSchoolVat(int schId) {
        String sql = "select vatpercent from school where schid=?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);

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

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return 0;
    }

    /*
     * varish This method is used to get school id using shortcoder
     */
    public int getSchoolId(String scode) {
        String sql = "select schid from school where shortcode=?";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, scode);
            gc.rs1 = gc.ps1.executeQuery();
            if (gc.rs1.next()) {
                return gc.rs1.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        return 0;
    }

    /*
     * varish This method is used to insert values to studentordernumber table used
     * to generate order number
     */

    public boolean insertStudentOrderNumber(SchoolBean sb) {
        int schid = getSchoolId(sb.getShortCode());
        String sql = "insert into studentordernumber (schsname,yr,seqno,schid) values (?,?,?,?)";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, sb.getShortCode());
            gc.ps1.setInt(2, Calendar.getInstance().get(Calendar.YEAR));
            gc.ps1.setInt(3, 1);
            gc.ps1.setInt(4, schid);
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
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

    /*
     * varish This method is used to insert values to studentinvnumber table used to
     * generate invoice number
     */

    public boolean insertStudentInvNumber(SchoolBean sb) {
        int schid = getSchoolId(sb.getShortCode());
        String sql = "insert into studentinvnumber (schsname,yr,seqno,schid) values (?,?,?,?)";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, sb.getShortCode());
            gc.ps1.setInt(2, Calendar.getInstance().get(Calendar.YEAR));
            gc.ps1.setInt(3, 1);
            gc.ps1.setInt(4, schid);
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
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

}
