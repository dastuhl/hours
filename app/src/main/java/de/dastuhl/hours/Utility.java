package de.dastuhl.hours;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.MonthlySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;
import de.dastuhl.hours.data.model.WeeklySessionsSummary;
import de.dastuhl.hours.data.model.YearlySessionsSummary;

/**
 * Created by Martin on 18.10.2015.
 */
public class Utility {

    private Utility() {

    }

    public static String getDurationString(Integer minutes) {
        int hours = 0;
        int min = 0;
        if (minutes != null) {
            hours = minutes / 60;
            min = minutes % 60;
        }

        return twoFigure(hours) + ":" + twoFigure(min);
    }

    public static String twoFigure(int number) {
        String str = "00";
        String numberString = String.valueOf(number);
        return (str + numberString).substring(numberString.length());
    }

    public static List<Integer> getSportsColors(Context context) {
        if (context != null) {
            return Lists.newArrayList(context.getResources().getColor(R.color.color_swim),
                    context.getResources().getColor(R.color.color_cycle),
                    context.getResources().getColor(R.color.color_run),
                    context.getResources().getColor(R.color.color_athletic));
        }
        return Collections.emptyList();
    }

    public static boolean getPreferredBarChartStyle(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_barchart_key), true);
    }

    public static float getPreferredMaxValueDays(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(context.getString(R.string.pref_max_value_days_key), "480");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueWeeks(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(context.getString(R.string.pref_max_value_weeks_key), "1800");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueMonths(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(context.getString(R.string.pref_max_value_months_key), "4800");
        return Float.valueOf(value);
    }

    public static float getPreferredMaxValueYears(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(context.getString(R.string.pref_max_value_years_key), "60000");
        return Float.valueOf(value);
    }

    public static String createFriendlyPeriodString(Context context, SessionsSummary summary) {
        if (summary instanceof DailySessionsSummary) {
            return createFriendlyPeriodString((DailySessionsSummary) summary);
        } else if (summary instanceof WeeklySessionsSummary) {
            return createFriendlyPeriodString(context, (WeeklySessionsSummary) summary);
        } else if (summary instanceof MonthlySessionsSummary) {
            return createFriendlyPeriodString((MonthlySessionsSummary) summary);
        } else if (summary instanceof YearlySessionsSummary) {
            return createFriendlyPeriodString((YearlySessionsSummary) summary);
        }
        return "";
    }

    public static String createFriendlyPeriodString(DailySessionsSummary summary) {

        Calendar cal = getCalendarFromDailySessionsSummary(summary);
        String result;

        DateFormat format = SimpleDateFormat.getDateInstance();
        format.setTimeZone(cal.getTimeZone());
        result = format.format(cal.getTime());

        return result;
    }

    @NonNull
    private static Calendar getCalendarFromDailySessionsSummary(DailySessionsSummary summary) {
        Calendar cal = Calendar.getInstance();
        cal.set(summary.getYear(), summary.getMonth() - 1, summary.getDayOfMonth());
        return cal;
    }

    public static String createFriendlyPeriodString(Context context, WeeklySessionsSummary summary) {
        if (context == null) {
            return "";
        }
        return context.getString(R.string.week) + " " + summary.getWeekOfYear() + " " + summary.getYear();
    }

    public static String createFriendlyPeriodString(MonthlySessionsSummary summary) {
        Calendar cal = new GregorianCalendar();
        cal.set(summary.getYear(), summary.getMonth() - 1, 1);

        String result;
        SimpleDateFormat format = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        format.setTimeZone(cal.getTimeZone());
        result = format.format(cal.getTime());

        return result;
    }

    public static String createFriendlyPeriodString(YearlySessionsSummary summary) {
        return String.valueOf(summary.getYear());
    }

    public static String getTitleFromSummary(Context context, SessionsSummary summary) {
        if (context != null) {
            if (summary instanceof DailySessionsSummary) {
                return context.getString(R.string.daily_summary);
            } else if (summary instanceof WeeklySessionsSummary) {
                return context.getString(R.string.weekly_summary);
            } else if (summary instanceof MonthlySessionsSummary) {
                return context.getString(R.string.monthly_summary);
            } else if (summary instanceof YearlySessionsSummary) {
                return context.getString(R.string.yearly_summary);
            }
        }

        return "";
    }

    public static String getDayName(Context context, SessionsSummary summary) {
        if (context != null && summary instanceof DailySessionsSummary) {
            Calendar cal = getCalendarFromDailySessionsSummary((DailySessionsSummary) summary);
            Calendar current = Calendar.getInstance();

            if (cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {
                if (cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {
                    return context.getString(R.string.today);
                } else if (cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR) - 1) {
                    return context.getString(R.string.yesterday);
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.getDefault());
            format.setTimeZone(cal.getTimeZone());
            return format.format(cal.getTime());
        }
        return "";
    }
}
