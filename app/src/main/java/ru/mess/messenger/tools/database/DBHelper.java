package ru.mess.messenger.tools.database;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Shugig on 11/16/2017.
 */

public class DBHelper {

    public static Connection connection = null;

    private static final String url = "jdbc:postgresql://10.0.2.2:5432/msg";
    private static final String username = "msg_user";
    private static final String password = "123456";
    private static final String driver = "org.postgresql.Driver";

    public static void init()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(driver);

            if(connection == null)
            {
                connection = DriverManager.getConnection(url,username,password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static Connection getConnection() throws SQLException {



            if (connection == null || connection.isClosed() )
            {

                connection = DriverManager.getConnection(url,username,password);

            }


        return connection;
    }
}
