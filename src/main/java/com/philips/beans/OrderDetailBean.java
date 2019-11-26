package com.philips.beans;

public class OrderDetailBean {
    private int odid;
    private int unid;
    private int qty;
    private int auid;
    private String oid;
    private double amount;
    private int smid;
    private double cgsTax;
    private double sgsTax;
    private double igsTax;
    private double cgstTaxAmount;
    private double sgstTaxAmount;
    private double igstTaxAmount;
    private String packed;

    public int getOdid() {
        return odid;
    }

    public void setOdid(int odid) {
        this.odid = odid;
    }

    public int getUnid() {
        return unid;
    }

    public void setUnid(int unid) {
        this.unid = unid;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getAuid() {
        return auid;
    }

    public void setAuid(int auid) {
        this.auid = auid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getSmid() {
        return smid;
    }

    public void setSmid(int smid) {
        this.smid = smid;
    }

    public double getCgsTax() {
        return cgsTax;
    }

    public void setCgsTax(double cgsTax) {
        this.cgsTax = cgsTax;
    }

    public double getSgsTax() {
        return sgsTax;
    }

    public void setSgsTax(double sgsTax) {
        this.sgsTax = sgsTax;
    }

    public double getIgsTax() {
        return igsTax;
    }

    public void setIgsTax(double igsTax) {
        this.igsTax = igsTax;
    }

    public double getCgstTaxAmount() {
        return cgstTaxAmount;
    }

    public void setCgstTaxAmount(double cgstTaxAmount) {
        this.cgstTaxAmount = cgstTaxAmount;
    }

    public double getSgstTaxAmount() {
        return sgstTaxAmount;
    }

    public void setSgstTaxAmount(double sgstTaxAmount) {
        this.sgstTaxAmount = sgstTaxAmount;
    }

    public double getIgstTaxAmount() {
        return igstTaxAmount;
    }

    public void setIgstTaxAmount(double igstTaxAmount) {
        this.igstTaxAmount = igstTaxAmount;
    }

    public String getPacked() {
        return packed;
    }

    public void setPacked(String packed) {
        this.packed = packed;
    }

    @Override
    public String toString() {
        return "OrderDetailBean [odid=" + odid + ", unid=" + unid + ", qty=" + qty + ", auid=" + auid + ", oid=" + oid
                + ", amount=" + amount + ", smid=" + smid + ", cgsTax=" + cgsTax + ", sgsTax=" + sgsTax + ", igsTax="
                + igsTax + ", cgstTaxAmount=" + cgstTaxAmount + ", sgstTaxAmount=" + sgstTaxAmount + ", igstTaxAmount="
                + igstTaxAmount + ", packed=" + packed + "]";
    }

}
