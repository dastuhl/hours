package de.dastuhl.hours;

import android.content.Context;
import android.graphics.Color;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

/**
 * Created by Martin on 18.10.2015.
 */
public class Util {

    private Util() {

    }

    public static String getTimeString(Integer minutes) {
        int hours = 0;
        int min = 0;
        if (minutes != null) {
            hours = minutes / 60;
            min = minutes % 60;
        }

        return new StringBuilder().append(twoFigure(hours)).append(":").append(twoFigure(min))
                .toString();
    }

    public static String twoFigure(int number) {
        String str = "00";
        String numberString = String.valueOf(number);
        return (str + numberString).substring(numberString.length());
    }

    public static List<Integer> getSportsColors(Context context) {
        return Lists.newArrayList(context.getResources().getColor(R.color.blue),
                context.getResources().getColor(R.color.red),
                context.getResources().getColor(R.color.green),
                context.getResources().getColor(R.color.yellow));
    }
}
