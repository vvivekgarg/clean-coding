package com.philips.dao;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.philips.beans.HouseBean;
import com.philips.connection.GetConnection;;

public class HouseDAO {

    static Logger logger = GetConnection.getLogger(HouseDAO.class);

    public HouseBean getHouse(int houseId) {

        String sql = "select hid, color1, color2, color3, color4 from house where hid =?";
        HouseBean hb = null;
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, houseId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                hb = new HouseBean();
                hb.sethId(houseId);

                hb.setColor1(gc.rs1.getString(2));
                hb.setColor2(gc.rs1.getString(3));
                hb.setColor3(gc.rs1.getString(4));
                hb.setColor4(gc.rs1.getString(5));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
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
        return hb;

    }

    public HouseBean getHouseFromAssignedUniformId(int auId) {

        String sql = "SELECT hid, color1, color2, color3, color4 FROM HOUSE WHERE"
                + " HID = (SELECT HID FROM ASSIGNUNIFORM WHERE AUID  =?)";
        HouseBean hb = null;
        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setInt(1, auId);

            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                hb = new HouseBean();
                hb.sethId(gc.rs1.getInt(1));

                hb.setColor1(gc.rs1.getString(2));
                hb.setColor2(gc.rs1.getString(3));
                hb.setColor3(gc.rs1.getString(4));
                hb.setColor4(gc.rs1.getString(5));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
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
        return hb;

    }

}
