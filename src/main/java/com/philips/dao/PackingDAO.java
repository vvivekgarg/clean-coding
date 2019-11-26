package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.philips.beans.PackingBean;
import com.philips.connection.GetConnection;

/**
 * 
 * @author Naveen This class is part of the new requirement, for getting packing
 *         the methods getOrderPlacedStudentsOfSchool is copied from StudentDAO,
 *         re written since the studentDAO class is not returning oid, here we
 *         need oid
 */

public class PackingDAO {

    static Logger logger = GetConnection.getLogger(PackingDAO.class);

    /**
     * 
     * @param schId
     * @param cls
     * @param startDate
     * @param endDate
     * @param sex
     *            @return, this will return h -> hold, n -> not packed, p -> packed
     *            used, becuase when user sees, first hold (Orange), Not Packed
     *            (Red), Packed(Green)
     */
    public List<PackingBean> getOrderPlacedStudentsOfSchool(int schId, String cls, String startDate, String endDate,
            String sex) {

        String sql = "	select so.OID, st.sex, count(od.odid) as odid ,count(if(od.packed='N',1, null)) as packedcount "
                + "from student st,studentorder so, orderdetail od  where st.stid=so.stid and od.OID = so.OID and st.schid= ? "
                + "and st.class= ? and so.cancelled='N' and so.paid='Y' and so.odate between ? and  ? "
                + "and st.sex =? group by od.oid order by so.refno";

        List<PackingBean> packingList = new ArrayList<PackingBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, schId);
            gc.ps1.setString(2, cls);
            gc.ps1.setString(3, startDate);
            gc.ps1.setString(4, endDate);
            gc.ps1.setString(5, sex);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                logger.info("Packing Dash board for Order: " + gc.rs1.getString(1));

                PackingBean packingBean = new PackingBean();
                packingBean.setoId(gc.rs1.getString(1));
                packingBean.setSex(gc.rs1.getString(2));
                packingBean.setCountOrderDetailId(gc.rs1.getInt(3));
                packingBean.setCountPackedForOrder(gc.rs1.getInt(4));

                String packingStatus = null;

                if ((gc.rs1.getInt(3) - gc.rs1.getInt(4)) == 0) {
                    packingStatus = "N";
                } else if ((gc.rs1.getInt(3) - gc.rs1.getInt(4)) == gc.rs1.getInt(3)) {
                    packingStatus = "P";
                } else if ((gc.rs1.getInt(3) - gc.rs1.getInt(4)) != gc.rs1.getInt(3)) {
                    packingStatus = "H";

                }

                packingBean.setPackedStatus(packingStatus);
                packingList.add(packingBean);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        java.util.Collections.sort(packingList, new Comparator<PackingBean>() {

            @Override
            public int compare(PackingBean o1, PackingBean o2) {
                return o1.getPackedStatus().compareTo(o2.getPackedStatus());
            }

        });
        return packingList;

    }

    public List<String> getMeasurementsForOdid(int odid) {
        String sql = "select mo.name, smd.VALUE from mesurementorder mo, studentmesurementdetails smd, "
                + "orderdetail od where smd.MID = mo.MID and smd.SMID = od.smid and od.ODID = ?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.ps1.setInt(1, odid);

            gc.rs1 = gc.ps1.executeQuery();

            List<String> measurementNames = new ArrayList<String>();

            while (gc.rs1.next()) {
                measurementNames.add(gc.rs1.getString(1) + ", " + gc.rs1.getString(2));
            }

            // this condition is check because when displaying PackingReportDashBoad Graph
            // you cannot display
            // more than 3 uniform measurements
            if (measurementNames.size() < 4) {
                return measurementNames;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    public static void main(String[] args) {

        List<PackingBean> list = new PackingDAO().getOrderPlacedStudentsOfSchool(2, "NUR", "2017-01-01", "2018-01-10",
                "B");

        for (PackingBean pb : list) {
            logger.info(pb);
        }

    }

}
