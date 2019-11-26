package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.config.Order;

import com.philips.beans.OrderBean;
import com.philips.beans.OrderDetailBean;
import com.philips.beans.OrderInvoiceBean;
import com.philips.connection.GetConnection;;

/**
 * @author naveenkumar
 * @see Order and order detals
 */
public class OrderDAO {

    static Logger logger = GetConnection.getLogger(OrderDAO.class);

    // naveen
    // this function is called from MakeOrder3.jsp
    public ArrayList<ArrayList<Object>> getOrderDetailForStudent(int stId) {

        String sql = "select distinct un.unid, un.uname, au.auid, au.cost, au.cgst, au.sgst, au.igst,  "
                + "sm.smid, au.couriercost from uniform un, assignuniform au, STUDENTMESUREMENT sm,"
                + "studentmesurementdetails smd, student st, SCHOOL SC where sm.stid = ? "
                + "and  sm.auid = au.auid and sm.stid = st.stid and au.unid = un.unid AND ST.SCHID = "
                + "SC.SCHID and SMD.SMID=sm.smid and sm.stid=st.stid and SMD.value!=0.0 order by au.ordersorder";

        GetConnection gc = new GetConnection();

        ArrayList<ArrayList<Object>> myobj = new ArrayList<ArrayList<Object>>();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, stId);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ArrayList<Object> temp = new ArrayList<Object>();
                // unid
                temp.add(gc.rs1.getInt(1));
                // uname
                temp.add(gc.rs1.getString(2));
                // auid
                temp.add(gc.rs1.getInt(3));
                // cost
                temp.add(gc.rs1.getDouble(4));

                // added by naveen for tax (CGST, SGST, IGST) - 04OCT2017
                // cgst
                temp.add(gc.rs1.getDouble(5));

                // sgst
                temp.add(gc.rs1.getDouble(6));
                // igst
                temp.add(gc.rs1.getDouble(7));

                // added for tailor report naveen
                // smid
                temp.add(gc.rs1.getInt(8));

                // courier cost
                temp.add(gc.rs1.getDouble(9));
                myobj.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("In getOrderDetailForStudent :", e);
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

        return myobj;

    }

    // naveen
    // this method is not used after GST
    // disabled on 09Oct2017
    public boolean insertOrderDetail(int unId, int auId, int qty, double amount, String oId, int smId, double tax,
            double tax1) {

        String sql = "insert into orderdetail (UNID, QTY, AUID, OID, AMOUNT, SMID,TAX,TAXAMOUNT) values(?,?,?,?,?, ?,?,?)";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, unId);
            gc.ps1.setInt(2, qty);
            gc.ps1.setInt(3, auId);
            gc.ps1.setString(4, oId);
            gc.ps1.setDouble(5, amount);
            gc.ps1.setInt(6, smId);
            gc.ps1.setDouble(7, tax1);
            gc.ps1.setDouble(8, tax);
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("in insertOrderDetail ", e);
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

    // naveen
    public boolean insertOrderDetail(int unId, int auId, int qty, double amount, String oId, int smId, double cgsTax,
            double cgsTaxAmount, double sgsTax, double sgsTaxAmount, double igsTax, double igsTaxAmount,
            double courierCost) {

        // String sql = "insert into orderdetail (UNID, QTY, AUID, OID, AMOUNT,
        // SMID,TAX,TAXAMOUNT) values(?,?,?,?,?, ?,?,?)";
        String sql = "insert into orderdetail (UNID, QTY, AUID, OID, AMOUNT, SMID, cgstax, cgstaxamount, sgstax, sgstaxamount,igstax, igstaxamount, couriercost) "
                + "values(?,?,?,?,?, ?,?,?, ?, ?, ?, ?, ?)";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, unId);
            gc.ps1.setInt(2, qty);
            gc.ps1.setInt(3, auId);
            gc.ps1.setString(4, oId);
            gc.ps1.setDouble(5, amount);
            gc.ps1.setInt(6, smId);
            // newly added fields for GST
            gc.ps1.setDouble(7, cgsTax);
            gc.ps1.setDouble(8, cgsTaxAmount);

            gc.ps1.setDouble(9, sgsTax);
            gc.ps1.setDouble(10, sgsTaxAmount);

            gc.ps1.setDouble(11, igsTax);
            gc.ps1.setDouble(12, igsTaxAmount);
            gc.ps1.setDouble(13, courierCost);

            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("in insertOrderDetail ", e);
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

    // parameters needed
    // stid, total, modeofpayment, uid, ccvalue, (paid ='N', odate will be
    // system date), grandtotal, cancelled='N'
    public synchronized String insertStudentOrder(int studentId, double grandTotal, double total, String modeOfPay,
            String ccValue, int uId, double bankHandlingCharges, double totalCourierCost) {

        // System.out.println("in insertestudentorder @STID " + studentId);

        String sql = "insert into studentorder (oid, stid, total, modeofpayment,uid, ccvalue, grandtotal, "
                + "odate,refno, HANDLINGCHARGES, oidn, couriercost)" + " values (?,?,?,?,?,?,?,now(),?, ?, ?, ?)";

        String oId = null;
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            oId = getNextOrderStudent(studentId);

            // System.out.println("from insert student order method of order do
            // oid got is "
            // + oId);

            gc.ps1.setString(1, oId);
            gc.ps1.setInt(2, studentId);
            gc.ps1.setDouble(3, total);
            gc.ps1.setString(4, modeOfPay);
            gc.ps1.setInt(5, uId);
            gc.ps1.setString(6, ccValue);
            gc.ps1.setDouble(7, grandTotal);
            gc.ps1.setInt(8, incRefNo());

            // added by Naveen 06-Feb-2016
            // bankhandlingcharges
            gc.ps1.setDouble(9, bankHandlingCharges);

            // 2017/18/CHIN/1000 -> need only last part of the oid for universal report for
            // new requirement 03-oct-2017
            gc.ps1.setInt(10, Integer.parseInt(oId.substring(oId.lastIndexOf("/") + 1, oId.length())));

            gc.ps1.setDouble(11, totalCourierCost);

            // System.out.println(incRefNo());
            // incrementing the seq value by 1 when insertion happens in the DB
            // for StudentOrder Table
            if (gc.ps1.executeUpdate() > 0) {
                incSeqByOneStudenOrder(shoolShortCodeForStudent(studentId));
                return oId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug(" in insertStudentOrder ", e);
        } finally {
            try {
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            GetConnection.closeConnection();
        }
        return null;
    }

    // naveen
    // used in ApproveOrder.do
    public boolean approveOrder(String oId) {
        String sql = "update  studentorder set paid =? where oid =? and cancelled='N'";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setString(1, "Y");
            gc.ps1.setString(2, oId);

            return gc.ps1.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("in approveOrder ", e);
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
     * @param oId
     * @param reason
     * @see: this method is used in ApproveOrder2.jsp, when user enters the order
     *       number, if the amount is alread collected then the application should
     *       show an error message and send it to ApproveOrder1.jsp to accept other
     *       order number
     */

    public boolean checkApprovedOrder(String oId) {
        String sql = "select paid from studentorder where oid=?";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setString(1, oId);
            gc.rs1 = gc.ps1.executeQuery();
            if (gc.rs1.next()) {
                return gc.rs1.getString(1).equals("Y") ? true : false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("in approveOrder ", e);
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

    // varish
    // used in CancelOrder.do
    // added reason field by Naveen on 27-Dec-2015
    // packed='C' introduced as part of the new requirement 05-01-2018, when, this
    // status is used
    // while packing by defult 'N', when packed 'Y'
    public boolean cancelOrder(String oId, String reason) {

        String sql = "update  studentorder set cancelled =?, remarks =?, CANCELLEDDATE=now() where oid =?";

        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setString(1, "Y");
            gc.ps1.setString(2, reason);
            gc.ps1.setString(3, oId);

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

    /**
     * 
     * @param oId
     * @return
     * @see This is used in CancelOrder2.jsp to ensure that the user doesnot cancel
     *      the cancelled order
     */
    public boolean isOrderCancelled(String oId) {

        String sql = "select cancelled from studentorder where oid=?";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setString(1, oId);
            gc.rs1 = gc.ps1.executeQuery();
            if (gc.rs1.next()) {
                return gc.rs1.getString(1).equals("Y") ? true : false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("in approveOrder ", e);
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

    public List<OrderBean> getCancelledOrders() {

        String sql = "select oid, modeofpayment,odate, remarks, CANCELLEDDATE from studentorder where cancelled=?";
        GetConnection gc = new GetConnection();
        List<OrderBean> list = new ArrayList<OrderBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, "Y");
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                OrderBean order = new OrderBean();
                order.setoId(gc.rs1.getString(1));
                order.setModeOfPayment(gc.rs1.getString(2));
                order.setoDate(gc.rs1.getString(3));
                order.setRemarks(gc.rs1.getString(4));
                order.setCancelledDate(gc.rs1.getString(5));

                list.add(order);
            }

            return list;
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
        return null;
    }

    /**
     * 
     * @return
     * @see This is used in PrintOrder.jsp, to show either the order is cancelled,
     *      if cancelled, then with date
     */
    public OrderBean getCancelledOrder(String oId) {

        System.out.println("in getCancelledOrder :" + oId);
        String sql = "select oid, modeofpayment,odate, remarks, CANCELLEDDATE, cancelled from studentorder where oid=?";
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, oId);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                OrderBean order = new OrderBean();
                order.setoId(gc.rs1.getString(1));
                order.setModeOfPayment(gc.rs1.getString(2));
                order.setoDate(gc.rs1.getString(3));
                order.setRemarks(gc.rs1.getString(4));
                order.setCancelledDate(gc.rs1.getString(5));
                order.setCancelled(gc.rs1.getString(6));

                System.out.println("orderBean :" + order);
                return order;
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
        return null;
    }

    // This method will give the short code for the student given
    // made it as static as it is used only here
    // for order
    public static String shoolShortCodeForStudent(int studentId) {

        String sql = "SELECT SON.SCHSNAME FROM STUDENTORDERNUMBER SON, STUDENT ST, SCHOOL SC WHERE "
                + "SON.SCHID = SC.SCHID AND ST.SCHID = SC.SCHID AND ST.STID=?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, studentId);

            gc.rs1 = gc.ps1.executeQuery();
            if (gc.rs1.next()) {
                return gc.rs1.getString(1);
            } else {
                // System.out.println("No Short code... ");
                return null;
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

        return null;
    }

    // NAVEEN TO THE ORDER NO IN THE FORMAT 2015/16/TRI/01
    // this method will call another mehtod which returns the Short Code of
    // the school
    public String getNextOrderStudent(int studentId) {

        GetConnection gc = new GetConnection();
        try {

            if (shoolShortCodeForStudent(studentId) != null) {
                String sql = "SELECT SCHSNAME, YR, SEQNO, SCHID FROM studentordernumber where schsname =?";
                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                gc.ps1.setString(1, shoolShortCodeForStudent(studentId));
                gc.rs1 = gc.ps1.executeQuery();

                if (gc.rs1.next()) {
                    return gc.rs1.getString(2) + "/" + (gc.rs1.getInt(2) - 1999) + "/" + gc.rs1.getString(1) + "/"
                            + gc.rs1.getString(3);
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

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return null;
    }

    public boolean incSeqByOneStudenOrder(String schSCode) {
        String sql = "update studentordernumber set seqno = seqno + 1 where schsname = ?";

        // System.out.println("seq inc by 1... ");
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, schSCode);

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

    public boolean incSeqByOneStudentInvoice() {
        String sql = "update studentinvnumber set seqno = seqno +1 where id = 1";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

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

    public int getNextInvoiceStudent() {
        String sql = "SELECT SEQNO FROM studentinvnumber where id =1";
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
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

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }
        return -1;
    }

    public int insertInvoiceDetail(int studentId, String invDate, String oId) {

        System.out.println("IN insert invoice Detail studentid:" + studentId + ", inDate: " + invDate + ", oid" + oId);
        int invNo = getNextInvoiceStudent();
        if (invNo != -1) {
            String sql = "insert into studentinvoice (oid,date,INVOICENO) values (?,?,?)";
            GetConnection gc = new GetConnection();
            try {
                gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
                gc.ps1.setString(1, oId);
                gc.ps1.setString(2, invDate);
                gc.ps1.setInt(3, invNo);

                if (gc.ps1.executeUpdate() > 0) {
                    incSeqByOneStudentInvoice();
                    return invNo;
                }
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
        }
        return -1;

    }

    /**
     * 
     * @return
     * @see : create for bulk tax invoice called -> insertInvoiceDetail(int
     *      studentId, String invDate, String oId) for inserting multiple invoice ,
     *      this method will get those students who has paid and not cancelled
     */

    public List<OrderInvoiceBean> insertBulkInvoiceDetail(int schoolId, String studentClass, String invDate) {

        List<OrderInvoiceBean> listOfOrders = new ArrayList<OrderInvoiceBean>();

        try {
            String sql = "select oid, stid from  studentorder where stid in (select stid from student where class=? and schid=?)"
                    + " and paid='Y' AND CANCELLED='N'";
            GetConnection gc = new GetConnection();

            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, studentClass);
            gc.ps1.setInt(2, schoolId);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {

                OrderInvoiceBean orderInvoiceBean = new OrderInvoiceBean();

                orderInvoiceBean.setoId(gc.rs1.getString(1));
                orderInvoiceBean.setStudentId(gc.rs1.getInt(2));

                int invoiceNumberGenerated = this.insertInvoiceDetail(orderInvoiceBean.getStudentId(), invDate,
                        orderInvoiceBean.getoId());
                if (invoiceNumberGenerated == -1) {
                    listOfOrders.clear();

                    return listOfOrders;
                }
                orderInvoiceBean.setInvoiceNumber(invoiceNumberGenerated);
                listOfOrders.add(orderInvoiceBean);

                logger.info("Invoice Generated for " + orderInvoiceBean.getStudentId() + ", order id "
                        + orderInvoiceBean.getoId() + ", invoice number : " + invoiceNumberGenerated);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            // clearing this because in TaxInvoiceBulkController, while forwarding checking
            // for >0, so it should goto else clause
            // and show error in index.jsp

            listOfOrders.clear();

            return listOfOrders;
        }
        return listOfOrders;
    }

    public int incRefNo() {
        String sql = "SELECT max(refno)+1 FROM studentorder";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                return gc.rs1.getInt(1);
            }
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
        return 0;

    }

    public List<List<String>> getOrderDetailsWithStudentName(String name, int schid) {
        List<List<String>> myList = new ArrayList<List<String>>();

        String sql = "select st.name,  st.usn, st.sex, st.class, so.oid, so.cancelled, st.stid from student st, studentorder so where  st.stid = so.stid and  st.name like '%"
                + name + "%' and st.schid=" + schid + "";
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                List<String> temp = new ArrayList<String>();
                temp.add(gc.rs1.getString(1));
                temp.add(gc.rs1.getString(2));
                temp.add(gc.rs1.getString(3));
                temp.add(gc.rs1.getString(4));
                temp.add(gc.rs1.getString(5));
                temp.add(gc.rs1.getString(6).equalsIgnoreCase("N") ? "NO" : "YES");
                temp.add(gc.rs1.getString(7));
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

    // varish : to set invoice number

    public boolean updateInvoice(int no) {
        // TODO Auto-generated method stub
        GetConnection gc = new GetConnection();
        String sql = "update studentinvnumber set seqno=? where id=1";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, no);
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

    //
    // varish:
    // this method is used to set second date for invoice
    public boolean updateInvDate1(int sno, int eno, String date1) {
        // TODO Auto-generated method stub
        GetConnection gc = new GetConnection();
        String sql = "update studentinvoice set date1=? where INVOICENO between ? and ?";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, date1);
            gc.ps1.setInt(2, sno);
            gc.ps1.setInt(3, eno);

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

    /**
     * @since 06 Sep 2016
     * @see: this is the new requirement when system locks while taking order
     */
    public boolean incrementOrderNumber(int schId) {
        GetConnection gc = new GetConnection();
        String sql = "update studentordernumber set SEQNO = SEQNO +1 where SCHID =?";

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            boolean flag = gc.ps1.executeUpdate() > 0;
            logger.info("School Order id : " + new SchoolDAO().getSchool(schId).getSchName() + " incremented by 1");
            return flag;

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
        return false;
    }

    public static void main(String[] args) {
        new OrderDAO().incrementOrderNumber(2);
    }

    // this is used in showpackingorder.jsp, when clicked on the page
    // PackingReportDashBoard.jsp
    // this method is used to get number of items packed or not
    public List<OrderDetailBean> getOrderDetails(String oid) {
        String sql = "select odid, unid, qty, auid, packed from orderdetail where oid =?";

        GetConnection gc = new GetConnection();
        List<OrderDetailBean> listOrderDetail = new ArrayList<OrderDetailBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, oid);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                OrderDetailBean odb = new OrderDetailBean();

                odb.setOdid(gc.rs1.getInt(1));
                odb.setUnid(gc.rs1.getInt(2));
                odb.setQty(gc.rs1.getInt(3));
                odb.setAuid(gc.rs1.getInt(4));
                odb.setPacked(gc.rs1.getString(5));

                listOrderDetail.add(odb);

                logger.info(odb);

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
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

        return listOrderDetail;
    }

    public boolean setPackStatus(int odid, String status) {

        String sql = "update orderdetail set packed=? where odid=?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, status);
            gc.ps1.setInt(2, odid);
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

}// end
