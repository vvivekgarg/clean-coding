package com.philips.beans;

public class ProUnIDAuidBean {
    private int unid;
    private int auid;

    public ProUnIDAuidBean() {
    }

    @Override
    public String toString() {
        return "ProUnIDAuidBean [unid=" + unid + ", auid=" + auid + "]";
    }

    public ProUnIDAuidBean(int unid, int auid) {
        super();
        this.unid = unid;
        this.auid = auid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + auid;
        result = prime * result + unid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProUnIDAuidBean other = (ProUnIDAuidBean) obj;

        // commented this by naveen
        // because we can the intersection list only for uniformId's
        /*
         * if (auid != other.auid) return false;
         */

        if (unid != other.unid)
            return false;
        return true;
    }

    public int getUnid() {
        return unid;
    }

    public void setUnid(int unid) {
        this.unid = unid;
    }

    public int getAuid() {
        return auid;
    }

    public void setAuid(int auid) {
        this.auid = auid;
    }

}
