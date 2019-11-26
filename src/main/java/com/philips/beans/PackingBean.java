package com.philips.beans;

public class PackingBean {
    private String oId;
    private String sex;
    // this will keep number of OrderDetails count per order
    private int countOrderDetailId;

    // this will keep number of packed item for order
    private int countPackedForOrder;
    // this will have values of H-> Hold, N -> Not Packed, P -> Packed
    private String packedStatus;

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getCountOrderDetailId() {
        return countOrderDetailId;
    }

    public void setCountOrderDetailId(int countOrderDetailId) {
        this.countOrderDetailId = countOrderDetailId;
    }

    public int getCountPackedForOrder() {
        return countPackedForOrder;
    }

    public void setCountPackedForOrder(int countPackedForOrder) {
        this.countPackedForOrder = countPackedForOrder;
    }

    public String getPackedStatus() {
        return packedStatus;
    }

    public void setPackedStatus(String packedStatus) {
        this.packedStatus = packedStatus;
    }

    @Override
    public String toString() {
        return "PackingBean [oId=" + oId + ", sex=" + sex + ", countOrderDetailId=" + countOrderDetailId
                + ", countPackedForOrder=" + countPackedForOrder + ", packedStatus=" + packedStatus + "]";
    }

}
