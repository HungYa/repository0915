package io.renren.modules.job.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private String dbDriver="com.mysql.jdbc.Driver";
    private String dbUrl1 = "jdbc:mysql://127.0.0.1:3306/llpe";
    private String dbUrl2 = "jdbc:mysql://127.0.0.1:3306/kelaitest";
    private String dbUser="root";
    private String dbPass="root";
    public Connection getDBConnection(String connectionname) {
        Connection conn=null;
        if(connectionname == "hospitalread") {

            try
            {
                Class.forName(dbDriver);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                conn = DriverManager.getConnection(dbUrl1,dbUser,dbPass);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        if(connectionname == "kelaisc") {
            try
            {
                Class.forName(dbDriver);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                conn = DriverManager.getConnection(dbUrl2,dbUser,dbPass);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return conn;
    }

}
