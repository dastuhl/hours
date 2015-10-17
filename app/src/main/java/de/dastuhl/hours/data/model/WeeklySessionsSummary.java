package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martin on 24.09.2015.
 */
public class WeeklySessionsSummary extends CumulatedSessionsSummary {

    public WeeklySessionsSummary() {

    }

    public WeeklySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public String createTimerangeString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getYear()).append("-").append(twoFigure(getWeekOfYear()));
        String result = builder.toString();
        if (result.length() != 7) {
            throw new IllegalStateException("invalid Date " + result);
        }
        return result;
    }

}
