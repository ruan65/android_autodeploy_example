package ru.mess.messenger.tools;

import android.util.Log;

/**
 * Created by neliousness on 12/19/17.
 */

public class Utils {

//    public static final String BASE_URL = "http://10.0.2.2:8080/login";
    public static final String BASE_URL = "http://172.22.16.82:8080/login";


    /**
     * Capitalize first letter of a string
     * @param name
     * @return
     */
    public static String capitalizeFirst(String name)
    {
        if (name.length() > 1) {
            char[] letters = name.toCharArray();

            String tempLetter = letters[0]+"";

            return  tempLetter.toUpperCase() + name.substring(1, name.length());
        }
        else
        {
            return name;
        }

    }


    public  static String removeSpaces(String raw)
    {
        return raw.trim();
    }



}
