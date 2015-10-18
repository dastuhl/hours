package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Martin on 24.09.2015.
 */
public class DailySessionsSummary extends SessionsSummary {

    public DailySessionsSummary() {

    }

    public DailySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public DailySessionsSummary(Calendar sessionDate, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        this(sessionDate.get(Calendar.YEAR), sessionDate.get(Calendar.MONTH) + 1, sessionDate.get(Calendar.WEEK_OF_YEAR), sessionDate.get(Calendar.DAY_OF_MONTH),
                pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public static DailySessionsSummary fromSessionsSummary(SessionsSummary summary) {
        return new DailySessionsSummary(summary.getYear(), summary.getMonth(), summary.getWeekOfYear(), summary.getDayOfMonth(),
                summary.getSwimDuration(), summary.getCycleDuration(), summary.getRunDuration(), summary.getAthleticDuration());
    }

    public String createTimerangeString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getYear()).append("-").append(twoFigure(getMonth())).append("-").append(twoFigure(getDayOfMonth()));
        String result = builder.toString();
        if (result.length() != 10) {
            throw new IllegalStateException("invalid Date " + result);
        }
        return result;
    }

    public String createKeyForYearlySummary() {
        return String.valueOf(getYear());
    }

    public String createKeyForMonthlySummary() {
        return new StringBuilder().append(getYear()).append(getMonth()).toString();
    }

    public String createKeyForWeeklySummary() {
        return new StringBuilder().append(getYear()).append(getWeekOfYear()).toString();
    }

}
