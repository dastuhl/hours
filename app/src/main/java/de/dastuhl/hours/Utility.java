package de.dastuhl.hours;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Martin on 18.10.2015.
 */
public class Utility {

    private Utility() {

    }

    public static String getPeriodString(Integer minutes) {
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
        return Lists.newArrayList(context.getResources().getColor(R.color.color_swim),
                context.getResources().getColor(R.color.color_cycle),
                context.getResources().getColor(R.color.color_run),
                context.getResources().getColor(R.color.color_athletic));
    }

    public static boolean getPreferredBarChartStyle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_barchart_key), true);
    }

    public static float getPreferredMaxValueDays(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value=  prefs.getString(context.getString(R.string.pref_max_value_days_key), "480");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueWeeks(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value=  prefs.getString(context.getString(R.string.pref_max_value_weeks_key), "1800");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueMonths(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value=  prefs.getString(context.getString(R.string.pref_max_value_months_key), "4800");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueYears(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value=  prefs.getString(context.getString(R.string.pref_max_value_years_key), "60000");
        return Float.valueOf(value);
    }

}
