package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martin on 24.09.2015.
 */
public class YearlySessionsSummary extends CumulatedSessionsSummary {

    public YearlySessionsSummary() {

    }

    public YearlySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public String createTimerangeString() {
        String result = String.valueOf(getYear());
        if (result.length() != 4) {
            throw new IllegalStateException("invalid Date " + result);
        }
        return result;
    }
}
