package com.philips.beans;

// this bean is used to keep current student information such as UNID, AUID, SMID and list of SMDID for each SMID

public class ProSrcBean {
    private int unid;
    private int auid;
    private int smid;

    public ProSrcBean() {
    }

    public ProSrcBean(int unid, int auid, int smid) {
        super();
        this.unid = unid;
        this.auid = auid;
        this.smid = smid;
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

    public int getSmid() {
        return smid;
    }

    public void setSmid(int smid) {
        this.smid = smid;
    }

    @Override
    public String toString() {
        return "ProSrcBean [unid=" + unid + ", auid=" + auid + ", smid=" + smid + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + auid;
        result = prime * result + smid;
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
        ProSrcBean other = (ProSrcBean) obj;
        if (auid != other.auid)
            return false;
        if (smid != other.smid)
            return false;
        if (unid != other.unid)
            return false;
        return true;
    }

}
