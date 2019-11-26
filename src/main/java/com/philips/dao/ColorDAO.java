package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.philips.beans.ColorBean;
import com.philips.connection.GetConnection;;

public class ColorDAO {
    static Logger logger = GetConnection.getLogger(ColorDAO.class);

    public boolean insertColor(String c1, String c2, String c3, String c4, int userId) {
        // ColorBean cb=new ColorBean();
        GetConnection gc = new GetConnection();
        String sql = "insert into house (color1,color2,color3,color4,uid) values (?,?,?,?,?)";

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, c1);
            gc.ps1.setString(2, c2);
            gc.ps1.setString(3, c3);
            gc.ps1.setString(4, c4);
            gc.ps1.setInt(5, userId);
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                // if(gc.rs1!=null)gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            GetConnection.closeConnection();

        }

        return false;
    }

    public List<ColorBean> getAllColors() {
        String sql = "select colorname from color";

        List<ColorBean> colors = new ArrayList<ColorBean>();

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                ColorBean temp = new ColorBean();
                temp.setColorName(gc.rs1.getString(1));
                colors.add(temp);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
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

        return colors;
    }
}
