package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

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
