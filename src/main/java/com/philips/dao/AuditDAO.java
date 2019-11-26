package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.philips.beans.AuditReportBean;
import com.philips.connection.GetConnection;

/**
 * 
 * @author naveenkumar
 * @version 1.0
 * @since Jan 14 2016
 * @description This class is created as part of the new requirement, for
 *              getting the audit report for different taxes
 */

public class AuditDAO {
    static Logger logger = GetConnection.getLogger(AuditDAO.class);

    /**
     * 
     * @param startDate
     * @param endDate
     * @return ArrayList<ArrayList<Object>>
     */

    public ArrayList<AuditReportBean> getAuditReportBetweenDates(String startDate, String endDate) {

        // Sample Query dont delete

        /*
         * select si.invoiceno, si.date, so.total as 'BILL AMOUNT',
         * GROUP_CONCAT(if(tax='5.50',taxamount,0)) AS 'Tax@5.50',
         * GROUP_CONCAT(if(tax='12.00',taxamount,0)) AS 'Tax@12.00',
         * GROUP_CONCAT(if(tax='14.50',taxamount,0)) AS 'Tax@14.50', so.grandtotal from
         * orderdetail od, studentinvoice si, studentorder so where od.oid = si.oid and
         * od.oid = so.oid and si.oid = so.oid and od.oid in (select oid from
         * studentinvoice where date between '2014-01-01' and '2016-04-10') group by
         * od.oid order by si.invoiceno;
         */

        String sql = "select si.invoiceno, si.date, so.total ,so.grandtotal,  si.oid, so.handlingcharges,";
        // distinctList will hold, number of taxes in that period

        // for CGST
        List<Double> distinctList = getDistinctNewTaxBetweenRange(startDate, endDate, "cgstax");

        System.out.println("CGST : " + distinctList);
        for (Double temp : distinctList) {
            sql += "GROUP_CONCAT(if(cgstax='" + temp + "',cgstaxamount,0)) AS 'TAX@" + temp + "',";
        }

        // for SGST
        distinctList = getDistinctNewTaxBetweenRange(startDate, endDate, "sgstax");
        System.out.println("SGST : " + distinctList);

        for (Double temp : distinctList) {
            sql += "GROUP_CONCAT(if(sgstax='" + temp + "',sgstaxamount,0)) AS 'TAX@" + temp + "',";
        }

        // for IGST
        distinctList = getDistinctNewTaxBetweenRange(startDate, endDate, "igstax");
        System.out.println("IGST : " + distinctList);

        for (Double temp : distinctList) {
            sql += "GROUP_CONCAT(if(igstax='" + temp + "',igstaxamount,0)) AS 'TAX@" + temp + "',";
        }

        sql = sql.substring(0, sql.length() - 1);

        sql += " from orderdetail od, studentinvoice si, studentorder so where od.oid = "
                + "si.oid and od.oid = so.oid and si.oid = so.oid and od.oid in  (select oid "
                + "from studentinvoice where date between ? and ?) group by " + "od.oid order by si.invoiceno";

        // dont use logger here as sql command will contain single quotes ', this will
        // cause an error
        // logger.info("SQL for Audit Report @"+ sql);

        System.out.println("sql query in getAuditReportBetweenDates ->  " + sql);

        GetConnection gc = new GetConnection();

        ArrayList<AuditReportBean> finalReport = new ArrayList<AuditReportBean>();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, startDate);
            gc.ps1.setString(2, endDate);

            gc.rs1 = gc.ps1.executeQuery();

            // Will store OID, Tax, and sum of taxamount for particular OID
            HashMap<String, HashMap<String, Double>> hashMap = new HashMap<String, HashMap<String, Double>>();

            while (gc.rs1.next()) {
                // System.out.println(gc.rs1.getString(1));
                AuditReportBean auditBean = new AuditReportBean();
                auditBean.setInvoiceNo(gc.rs1.getInt(1));
                auditBean.setDate(gc.rs1.getString(2));
                auditBean.setBillAmount(gc.rs1.getDouble(3));
                auditBean.setGrandTotal(gc.rs1.getDouble(4));
                auditBean.setOid(gc.rs1.getString(5));
                auditBean.setHandlingCharges(gc.rs1.getDouble(6));

                List<HashMap<String, Double>> taxsAmount = new ArrayList<HashMap<String, Double>>();

                // this loop is for each of the tax
                for (Double temp : distinctList) {
                    HashMap<String, Double> perProductTax = new HashMap<String, Double>();

                    double totalVal = 0;

                    // this is to calculate the taxes returned by the db, in the form of ',', which
                    // we are getting by GROUP_CONCAT function
                    for (String val : gc.rs1.getString("Tax@" + temp.toString()).split(",")) {
                        totalVal += Double.parseDouble(val);
                    }
                    // System.out.println("@"+ temp.toString() +" - "+totalVal + "\t");

                    perProductTax.put(temp.toString(), totalVal);
                    taxsAmount.add(perProductTax);
                    auditBean.setTaxsAmount(taxsAmount);
                }
                // hashMap.put(gc.rs1.getString(1), perProductTax);
                finalReport.add(auditBean);
                // System.out.println("2015/16/CHIN/10 - "
                // +hashMap.get("2015/16/CHIN/10").get("5.5"));
                /*
                 * try { Thread.sleep(1000); } catch (InterruptedException e) {
                 * e.printStackTrace(); logger.debug("In Audit Report : "+e); }
                 * 
                 * System.out.println(finalReport);
                 * 
                 */
            }

            return finalReport;

        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("In Audit Report : " + e);
        } finally {
            try {

                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                if (gc.rs1 != null) {
                    gc.rs1.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Double> getDistinctTaxBetweenRange(String startDate, String endDate) {
        String sql = "select distinct tax from orderdetail where oid in	(select oid from studentinvoice where date between ? and ?)";

        List<Double> list = new ArrayList<Double>();
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, startDate);
            gc.ps1.setString(2, endDate);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                list.add(gc.rs1.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {

                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;

    }

    /**
     * 
     * @param startDate
     * @param endDate
     * @param taxType
     * @return
     * @see taxType is for cgstax, sgstax, igstax
     */

    public List<Double> getDistinctNewTaxBetweenRange(String startDate, String endDate, String taxType) {
        String sql = "select distinct " + taxType
                + " from orderdetail where oid in	(select oid from studentinvoice where date between ? and ?)";

        List<Double> list = new ArrayList<Double>();
        GetConnection gc = new GetConnection();

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, startDate);
            gc.ps1.setString(2, endDate);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {

                System.out.println(gc.rs1.getDouble(1));
                list.add(gc.rs1.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {

                if (gc.ps1 != null) {
                    gc.ps1.close();
                }
                GetConnection.closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;

    }

    public static void main(String[] args) {
        List<AuditReportBean> list = new AuditDAO().getAuditReportBetweenDates("2017-03-20", "2018-04-07");
        System.out.println("In Main : " + list);

        Map<String, Double> map = new HashMap<String, Double>();

        map.put("5.5", 0.0);
        map.put("14.5", 0.0);
        map.put("totalBillAmount", 0.0);
        map.put("grandTotal", 0.0);

        System.out.println(list);
        System.out.println("-------------------------------");

        System.out.println("Invoice No, Date, Bill Amount, Grand Total, Order Id, taxes, Handling Charges");

        for (AuditReportBean arb : list) {
            System.out.print(arb.getInvoiceNo() + ", " + arb.getDate() + ", " + arb.getBillAmount() + ", "
                    + arb.getGrandTotal() + ", " + arb.getOid() + ", " + arb.getHandlingCharges());

            map.put("totalBillAmount", map.get("totalBillAmount") + arb.getBillAmount());
            map.put("grandTotal", map.get("grandTotal") + arb.getGrandTotal());
            // System.out.println(arb.getTaxsAmount());

            for (Map temp : arb.getTaxsAmount()) {
                Iterator itr = temp.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, Double> entry = (Entry<String, Double>) itr.next();

                    System.out.print("(" + entry.getKey() + "->" + entry.getValue() + ")");
                    map.put(entry.getKey(), map.get(entry.getKey()) + entry.getValue());
                }
            }

            System.out.println("");

        }

        System.out.println("Summary : Grand Total " + map.get("grandTotal") + ", Total Bill Amount"
                + map.get("totalBillAmount") + ", Tax @5.5 :" + map.get("5.5") + ", Tax@14.5: " + map.get("14.5"));

    }

    public static void main1(String[] args) {
        System.out.println(new AuditDAO().getDistinctNewTaxBetweenRange("2017-03-20", "2018-03-21", "cgstax"));
    }

}