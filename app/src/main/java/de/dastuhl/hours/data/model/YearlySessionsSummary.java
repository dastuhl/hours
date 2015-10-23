package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martin on 24.09.2015.
 */
public class YearlySessionsSummary extends CumulatedSessionsSummary {

    public YearlySessionsSummary() {

    }

    private YearlySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public static YearlySessionsSummary fromDailySessionsSummary(DailySessionsSummary dailySummary) {
        return  new YearlySessionsSummary(dailySummary.getYear(), null, null, null,
                dailySummary.getSwimDuration(), dailySummary.getCycleDuration(), dailySummary.getRunDuration(), dailySummary.getAthleticDuration());
    }

    public String createPeriodString() {
        String result = String.valueOf(getYear());
        if (result.length() != 4) {
            throw new IllegalStateException("invalid Date " + result);
        }
        return result;
    }
}
