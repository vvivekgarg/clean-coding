package com.philips.beans;

import java.util.HashMap;
import java.util.List;

public class AuditReportBean {
    private int invoiceNo;
    private String date;
    private double billAmount;
    private double grandTotal;
    private String oid;
    private double handlingCharges;

    private List<HashMap<String, Double>> taxsAmount;

    public int getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(int invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<HashMap<String, Double>> getTaxsAmount() {
        return taxsAmount;
    }

    public void setTaxsAmount(List<HashMap<String, Double>> taxsAmount) {
        this.taxsAmount = taxsAmount;
    }

    @Override
    public String toString() {
        return "AuditReportBean [invoiceNo=" + invoiceNo + ", date=" + date + ", billAmount=" + billAmount
                + ", grandTotal=" + grandTotal + ", taxsAmount=" + taxsAmount + "]";
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public double getHandlingCharges() {
        return handlingCharges;
    }

    public void setHandlingCharges(double handlingCharges) {
        this.handlingCharges = handlingCharges;
    }

}
