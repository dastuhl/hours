package de.dastuhl.hours.data.model;

import java.util.Calendar;

/**
 * Created by Martin on 17.10.2015.
 */
public class SessionsSummaryFactory {

    public static final SessionsSummaryFactory INSTANCE = new SessionsSummaryFactory();

    private SessionsSummaryFactory() {

    }

    public DailySessionsSummary createDailySessionsSummary(Calendar sessionDate, Integer pSwimDuration, Integer pCycleDuration, Integer pRunDuration, Integer pAthleticDuration) {
        return new DailySessionsSummary(sessionDate.get(Calendar.YEAR), sessionDate.get(Calendar.MONTH) + 1, sessionDate.get(Calendar.WEEK_OF_YEAR), sessionDate.get(Calendar.DAY_OF_MONTH),
                pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public DailySessionsSummary createDailySessionsSummary(SessionsSummary summary) {
        return new DailySessionsSummary(summary.getYear(), summary.getMonth(), summary.getWeekOfYear(), summary.getDayOfMonth(),
                summary.getSwimDuration(), summary.getCycleDuration(), summary.getRunDuration(), summary.getAthleticDuration());
    }

    public WeeklySessionsSummary createWeeklySessionsSummary(DailySessionsSummary dailySummary) {
        return new WeeklySessionsSummary(dailySummary.getYear(), null, dailySummary.getWeekOfYear(), null,
                dailySummary.getSwimDuration(), dailySummary.getCycleDuration(), dailySummary.getRunDuration(), dailySummary.getAthleticDuration());
    }

    public MonthlySessionsSummary createMonthlySessionsSummary(DailySessionsSummary dailySummary) {
        return new MonthlySessionsSummary(dailySummary.getYear(), dailySummary.getMonth(), null, null,
                dailySummary.getSwimDuration(), dailySummary.getCycleDuration(), dailySummary.getRunDuration(), dailySummary.getAthleticDuration());
    }

    public YearlySessionsSummary createYearlySessionsSummary(DailySessionsSummary dailySummary) {
        return  new YearlySessionsSummary(dailySummary.getYear(), null, null, null,
                dailySummary.getSwimDuration(), dailySummary.getCycleDuration(), dailySummary.getRunDuration(), dailySummary.getAthleticDuration());
    }

}
