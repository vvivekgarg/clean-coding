package com.philips.beans;

public class MesurementOrderBean {
    private int mId;
    private String name;
    private int mOrder;
    private int uId;
    private int unId;

    public MesurementOrderBean(String name, int mOrder, int uId, int unId) {
        this.name = name;
        this.mOrder = mOrder;
        this.uId = uId;
        this.unId = unId;
    }

    // used in uniformdao.getMesurementOrderunId
    public MesurementOrderBean() {
    }

    @Override
    public String toString() {
        return "MesurementOrderBean [mId=" + mId + ", name=" + name + ", mOrder=" + mOrder + ", uId=" + uId + ", unId="
                + unId + "]";
    }

    public MesurementOrderBean(int mId, String name, int mOrder, int uId, int unId) {
        super();
        this.mId = mId;
        this.name = name;
        this.mOrder = mOrder;
        this.uId = uId;
        this.unId = unId;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getmOrder() {
        return mOrder;
    }

    public void setmOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public int getUnId() {
        return unId;
    }

    public void setUnId(int unId) {
        this.unId = unId;
    }

}
