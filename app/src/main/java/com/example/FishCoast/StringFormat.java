package com.example.FishCoast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class StringFormat {
    public static String itemName(String name){
        return name;
    }
    public static String doubleToString(Double d, int unit){
        String msgdouble;
        if (d < 0){
            msgdouble = "";
        }
        else
        {
            if (d % 1 == 0)
                msgdouble = String.format(Locale.getDefault(), "%1$1.0f", d);
            else
                msgdouble = String.format(Locale.getDefault(), "%1$1.1f", d);
            if (unit == 1)
                msgdouble+= "шт";
            else
                msgdouble+= "кг";
        }

        return msgdouble;
    }
    public static double stringQuantityToDouble(String quantity){
        return 0.0;
    }
    public static int unitStringtoInteger(String str){
        if (str.equals("шт")) return 1;
        else return 0;
    }
    public static String unitIntegerToString(int unit){
        String msg;
        if (unit == 1){
            msg = "шт";
        }
        else if (unit == 0){
            msg = "кг";
        }
        else {
            msg = "";
        }
        return msg;
    }
}

