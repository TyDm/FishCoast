package com.example.FishCoast;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.Locale;
import java.util.Objects;

import static java.security.AccessController.getContext;

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
            if (unit == 0)
                msgdouble+= "кг";
        }

        return msgdouble;
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
    public static Spannable setSearchSpan(String positionName, String searchText, int color){
        Spannable nameSp = new SpannableString(positionName);
        if (searchText.length() > 0) {
            if (searchText.contains("%")){
                String search = searchText;// текст для замены
                int indexSpace = search.indexOf("%");
                String word = search.substring(0, indexSpace); // одно слово
                int index = nameSp.toString().toLowerCase().indexOf(word.toLowerCase());
                if (index >= 0)
                nameSp.setSpan(new ForegroundColorSpan(color),
                        index, index + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                search = search.replaceFirst("%", " ");
                boolean contains = true;
                do {
                    int nameSpLastIndex = index + word.length();
                    int indexSpaceOld = indexSpace;
                    if (search.contains("%")){
                        indexSpace = search.indexOf("%");
                    }
                    else {
                        contains = false;
                        indexSpace = search.length();
                    }

                    word = search.substring(indexSpaceOld + 1, indexSpace);
                    index = nameSp.toString().toLowerCase().indexOf(word.toLowerCase(), nameSpLastIndex+1);
                    if (index >= 0)
                    nameSp.setSpan(new ForegroundColorSpan(color),
                            index, index + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    search = search.replaceFirst("%", " ");

                } while (contains);
            }
            else {
                int index = nameSp.toString().toLowerCase().indexOf(searchText.toLowerCase());
                if (index >= 0)
                nameSp.setSpan(new ForegroundColorSpan(color),
                        index, index + searchText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return nameSp;
    }

    public static InsetDrawable getCustomDivider(Context context, Resources resources){
        int[] ATTRS = new int[]{android.R.attr.listDivider};
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);
        int inset = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        a.recycle();
        return new InsetDrawable(divider, inset, 0, inset, 0);
    }

}

