package com.philips.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.philips.beans.LoginBean;
import com.philips.beans.UserBean;
import com.philips.connection.GetConnection;;

public class UserDAO {

    static Logger logger = GetConnection.getLogger(UserDAO.class);

    GetConnection gc = new GetConnection();

    public boolean insertUser(UserBean ub) {
        String sql = "insert into user (uname, pass,utype, display_name, mobile) values(?,?,?,?,?)";

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, ub.getUserName().toUpperCase());
            gc.ps1.setString(2, ub.getPassWord());
            gc.ps1.setString(3, ub.getUserType());
            gc.ps1.setString(4, ub.getDisplayName().toUpperCase());
            gc.ps1.setString(5, ub.getMobNo());
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    // this method is used in user.jsp to display all registered users.
    public ArrayList<UserBean> getAllUsers() {

        ArrayList<UserBean> myList = new ArrayList<UserBean>();

        String sql = "select * from user";
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);

            gc.rs1 = gc.ps1.executeQuery();

            while (gc.rs1.next()) {
                UserBean temp = new UserBean();
                temp.setUserId(gc.rs1.getInt(1));
                temp.setUserName(gc.rs1.getString(2));
                temp.setPassWord(gc.rs1.getString(3));
                temp.setUserType(gc.rs1.getString(4));
                temp.setDisplayName(gc.rs1.getString(5));
                temp.setMobNo(gc.rs1.getString(6));

                myList.add(temp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                gc.rs1.close();
                gc.ps1.clearBatch();
                gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return myList;
    }// end of getAllProducts

    // method is used to check valid user
    public boolean validateUser(LoginBean login) {
        String sql = "select * from user where uname =? and pass =? and utype=?";

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, login.getuName());
            gc.ps1.setString(2, login.getPassWord());
            gc.ps1.setString(3, login.getUserType());

            gc.rs1 = gc.ps1.executeQuery();

            return gc.rs1.next();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (gc.rs1 != null)
                    gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public int getUserId(String uName) {

        String sql = "select uid from user where uname = ?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, uName);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                return gc.rs1.getInt(1);
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
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return 0;

    }

    public String getUserType(String uName) {

        String sql = "select utype from user where uname = ?";

        GetConnection gc = new GetConnection();
        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(1, uName);
            gc.rs1 = gc.ps1.executeQuery();

            if (gc.rs1.next()) {
                return gc.rs1.getString(1);
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
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return null;

    }

    public boolean updateUser(UserBean ub) {
        // TODO Auto-generated method stub
        String sql = "update user set pass=? where uname=?";

        try {
            gc.ps1 = GetConnection.getMySQLConnection().prepareStatement(sql);
            gc.ps1.setString(2, ub.getUserName());
            gc.ps1.setString(1, ub.getPassWord());
            return gc.ps1.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                // if(gc.rs1 != null) gc.rs1.close();
                if (gc.ps1 != null)
                    gc.ps1.close();
                GetConnection.getMySQLConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
