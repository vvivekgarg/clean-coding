package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.philips.beans.OrderDetailBean;
import com.philips.connection.GetConnection;;

// this class is create only temporary purpose, in case when there were any error this 

public class ErrorManagementFiles {

    // this method was create on 02-02-2018 since the amount calculated was
    // amount+tax
    // and gross amount was supposed to be amount + tax and it happened amount + tax
    // + tax
    public void setProperPriceAndTax(String orderDate) {
        String sql = "select oid from studentorder where odate =? ";

        try {
            String sqlOrderDetail = "select odid, qty, auid from orderdetail where oid =?";

            GetConnection gc = new GetConnection();
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, orderDate);
            gc.rs1 = gc.ps1.executeQuery();

            ArrayList<String> orderNumbers = new ArrayList<String>();
            while (gc.rs1.next()) {
                orderNumbers.add(gc.rs1.getString(1));
            }
            System.out.println(orderNumbers);

            for (String tempOrderId : orderNumbers) {

                gc.ps2 = GetConnection.getMySQLConnection().prepareStatement(sqlOrderDetail);
                gc.ps2.setString(1, tempOrderId);
                gc.rs2 = gc.ps2.executeQuery();

                while (gc.rs2.next()) {

                    OrderDetailBean tempOrderDetailBean = new OrderDetailBean();
                    tempOrderDetailBean.setOdid(gc.rs2.getInt(1));
                    tempOrderDetailBean.setQty(gc.rs2.getInt(2));
                    tempOrderDetailBean.setAuid(gc.rs2.getInt(3));

                    String sqlAssignUniform = "select cost, cgst, sgst, igst from assignuniform where auid =?";

                    gc.ps3 = GetConnection.getMySQLConnection().prepareStatement(sqlAssignUniform);
                    gc.ps3.setInt(1, tempOrderDetailBean.getAuid());
                    gc.rs3 = gc.ps3.executeQuery();
                    double cost = 0;
                    double cgst = 0;
                    double sgst = 0;
                    double igst = 0;

                    if (gc.rs3.next()) {
                        cost = gc.rs3.getDouble(1);
                        cgst = gc.rs3.getDouble(2);
                        sgst = gc.rs3.getDouble(3);
                        igst = gc.rs3.getDouble(4);
                    }

                    String sqlUpdateOrderDetail = "update orderdetail set amount =?, cgstaxamount = ?, sgstaxamount =?, "
                            + " igstaxamount = ? where ODID = ?";

                    double amount = cost * tempOrderDetailBean.getQty();
                    double cgsTaxAmount = (cost * tempOrderDetailBean.getQty() * cgst) / 100;
                    double sgsTaxAmount = (cost * tempOrderDetailBean.getQty() * sgst) / 100;
                    double igsTaxAmount = (cost * tempOrderDetailBean.getQty() * igst) / 100;
                    System.out.println("Cgst " + cgst + ", sgst " + sgst + ", igst " + igst);
                    System.out
                            .println("odid " + tempOrderDetailBean.getOdid() + ", " + " cost " + amount + "CGST Amount "
                                    + cgsTaxAmount + ", sgst amount " + sgsTaxAmount + ", igst amount " + igsTaxAmount);

                    // update orderDetail
                    gc.ps4 = GetConnection.getMySQLConnection().prepareStatement(sqlUpdateOrderDetail);
                    gc.ps4.setDouble(1, amount);
                    gc.ps4.setDouble(2, cgsTaxAmount);
                    gc.ps4.setDouble(3, sgsTaxAmount);
                    gc.ps4.setDouble(4, igsTaxAmount);
                    gc.ps4.setInt(5, tempOrderDetailBean.getOdid());
                    gc.ps4.executeUpdate();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // update student order

                    String sqlStudentUpdate = "update studentorder set total = (select sum(amount) from  "
                            + "orderdetail where oid =?), grandtotal = (select  round( sum(amount) "
                            + "+ sum(cgstaxamount) + sum(sgstaxamount) + sum(igstaxamount)) "
                            + "from orderdetail where oid =?) where oid=?";

                    gc.ps5 = GetConnection.getMySQLConnection().prepareStatement(sqlStudentUpdate);
                    gc.ps5.setString(1, tempOrderId);
                    gc.ps5.setString(2, tempOrderId);
                    gc.ps5.setString(3, tempOrderId);
                    gc.ps5.executeUpdate();

                }
                System.out.println("----------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new ErrorManagementFiles().setProperPriceAndTax("2018-02-01");
    }
}
