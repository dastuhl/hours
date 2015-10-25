package de.dastuhl.hours.data.model;

import de.dastuhl.hours.Utility;

/**
 * Created by Martin on 24.09.2015.
 */
public class MonthlySessionsSummary extends CumulatedSessionsSummary {

    public MonthlySessionsSummary() {

    }
    private MonthlySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public static MonthlySessionsSummary fromDailySessionsSummary(DailySessionsSummary dailySummary) {
        return new MonthlySessionsSummary(dailySummary.getYear(), dailySummary.getMonth(), null, null,
                dailySummary.getSwimDuration(), dailySummary.getCycleDuration(), dailySummary.getRunDuration(), dailySummary.getAthleticDuration());
    }

    String createPeriodString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getYear()).append("-").append(Utility.twoFigure(getMonth()));
        String result = builder.toString();
        if (result.length() != 7) {
            throw new IllegalStateException("invalid Date " + result);
        }
        return result;
    }

}
