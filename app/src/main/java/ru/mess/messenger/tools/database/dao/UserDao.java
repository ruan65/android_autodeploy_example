package ru.mess.messenger.tools.database.dao;

import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ru.mess.messenger.models.User;

import static ru.mess.messenger.tools.database.DBHelper.getConnection;

/**
 * Created by Shugig on 11/16/2017.
 */

public class UserDao {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AVATAR = "ava";
    public static final String COLUMN_CDATE = "cdate";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PW = "pw";

    public static void create()
    {
        try {
        Statement statement = getConnection().createStatement();
        String query = "INSERT INTO users VALUES (5,'user6-128x128.jpg','2017-11-16 13:14:05.415','user5@server.com','Nelio','hover',null,null);";


            final ResultSet set = statement.executeQuery(query);
            set.next();

            //getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<User> read()
    {
        ArrayList<User>  users = new ArrayList<>();
        try {



            Statement statement = getConnection().createStatement();
            String query = "SELECT * FROM users";

            final ResultSet set = statement.executeQuery(query);

            while (set.next())
            {
                User user = new User();

                int id = set.getInt(COLUMN_ID);
                String avater = set.getString(COLUMN_AVATAR);
                String login = set.getString(COLUMN_LOGIN);
                String name = set.getString(COLUMN_NAME);
                String password = set.getString(COLUMN_PW);


                //user.setId(id);
                //user.setAvatar(avater);
                //user.setName(name);
                //user.setUsername(login);
                //user.setPassword(password);

                users.add(user);




            }

            Log.d("status",users.size()+ " users");



        } catch (SQLException e) {
            e.printStackTrace();
        }


        return new ArrayList<>(users);
    }

    public static void update()
    {

    }

    public static void delete()
    {

    }

    public static User find(String value, String by, ArrayList<User> users)
    {

        boolean found = false;
        User foundUser = null;
        loop:
        for (User user : users)
        {
           if (by.equals(COLUMN_LOGIN))
           {
               if (value.equals(user.getLogin()))
               {
                   foundUser = user;
                   found = true;
                   break loop;
               }

           }
        }

        if (found)
        {
             return foundUser;
        }
        else
        {
            return null;
        }
    }

    public static boolean validateUser(User user, String password)
    {
       // if (user.getPassword().equals(password))
        {
            return true;
        }

        //return false;
    }

    public void closeConnection()
    {

    }
}
