package com.philips.beans;

public class StudentBean {
    private int studId;
    private String studUSN;
    private String studName;
    private String studSex;
    private String studClass;
    private String studSection;
    private String studParent;
    private String studParentMob;
    private String studParentEmail;
    private int studUID;
    private int studSchoolId;
    private String studStatus;

    // Added by naveen 17 Jan 2016
    // for listing students who were absent when taking the measurement
    private String measurementDate;

    // added orderId for new requirement on 25-jul-2017
    // for the method getOrderPlacedStudentsOfSchoolUnidWithOrderNo

    private String orderId;

    public int getStudId() {
        return studId;
    }

    public void setStudId(int studId) {
        this.studId = studId;
    }

    public String getStudUSN() {
        return studUSN;
    }

    public void setStudUSN(String studUSN) {
        this.studUSN = studUSN;
    }

    public String getStudName() {
        return studName;
    }

    public void setStudName(String studName) {
        this.studName = studName;
    }

    public String getStudClass() {
        return studClass;
    }

    public void setStudClass(String studClass) {
        this.studClass = studClass;
    }

    public String getStudSection() {
        return studSection;
    }

    public void setStudSection(String studSection) {
        this.studSection = studSection;
    }

    public String getStudParent() {
        return studParent;
    }

    public void setStudParent(String studParent) {
        this.studParent = studParent;
    }

    public String getStudParentMob() {
        return studParentMob;
    }

    public void setStudParentMob(String studParentMob) {
        this.studParentMob = studParentMob;
    }

    public String getStudParentEmail() {
        return studParentEmail;
    }

    public void setStudParentEmail(String studParentEmail) {
        this.studParentEmail = studParentEmail;
    }

    public int getStudUID() {
        return studUID;
    }

    public void setStudUID(int studUID) {
        this.studUID = studUID;
    }

    public int getStudSchoolId() {
        return studSchoolId;
    }

    public void setStudSchoolId(int studSchoolId) {
        this.studSchoolId = studSchoolId;
    }

    public String getStudSex() {
        return studSex;
    }

    public void setStudSex(String studSex) {
        this.studSex = studSex;
    }

    public String getStudStatus() {
        return studStatus;
    }

    public void setStudStatus(String studStatus) {
        this.studStatus = studStatus;
    }

    public String getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(String measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "StudentBean [studId=" + studId + ", studUSN=" + studUSN + ", studName=" + studName + ", studSex="
                + studSex + ", studClass=" + studClass + ", studSection=" + studSection + ", studParent=" + studParent
                + ", studParentMob=" + studParentMob + ", studParentEmail=" + studParentEmail + ", studUID=" + studUID
                + ", studSchoolId=" + studSchoolId + ", studStatus=" + studStatus + ", measurementDate="
                + measurementDate + ", orderId=" + orderId + "]";
    }

}
