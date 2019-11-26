package com.philips.beans;

// Added by Naveen on 25-Jan-2016
public class OrderBean {
    private String oId;
    private int studentId;
    private double total;
    private String modeOfPayment;
    private int uId;
    private int ccValue;
    private String paid;
    private String oDate;
    private double grandTotal;
    private String cancelled;
    private int refNo;
    private String remarks;
    private String cancelledDate;

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public int getCcValue() {
        return ccValue;
    }

    public void setCcValue(int ccValue) {
        this.ccValue = ccValue;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getoDate() {
        return oDate;
    }

    public void setoDate(String oDate) {
        this.oDate = oDate;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public int getRefNo() {
        return refNo;
    }

    public void setRefNo(int refNo) {
        this.refNo = refNo;
    }

    @Override
    public String toString() {
        return "OrderBean [oId=" + oId + ", studentId=" + studentId + ", total=" + total + ", modeOfPayment="
                + modeOfPayment + ", uId=" + uId + ", ccValue=" + ccValue + ", paid=" + paid + ", oDate=" + oDate
                + ", grandTotal=" + grandTotal + ", cancelled=" + cancelled + ", refNo=" + refNo + ", remarks="
                + remarks + ", cancelledDate=" + cancelledDate + "]";
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

}
