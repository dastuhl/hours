package de.dastuhl.hours.data.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.dastuhl.hours.Utility;

/**
 * Created by Martin on 24.09.2015.
 */
public class DailySessionsSummary extends SessionsSummary implements Comparable<DailySessionsSummary> {

    public DailySessionsSummary() {

    }

    public DailySessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth, pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public DailySessionsSummary(Calendar sessionDate, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        this(sessionDate.get(Calendar.YEAR), sessionDate.get(Calendar.MONTH) + 1, sessionDate.get(Calendar.WEEK_OF_YEAR), sessionDate.get(Calendar.DAY_OF_MONTH),
                pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public DailySessionsSummary(Calendar sessionDate) {
        this(sessionDate, 0, 0, 0, 0);
    }


    public static DailySessionsSummary newInstanceForToday() {
        Calendar cal = Calendar.getInstance();
        return new DailySessionsSummary(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0
        );
    }

    public static DailySessionsSummary newInstanceFromSessionsSummary(SessionsSummary summary) {
        return new DailySessionsSummary(summary.getYear(), summary.getMonth(), summary.getWeekOfYear(), summary.getDayOfMonth(),
                summary.getSwimDuration(), summary.getCycleDuration(), summary.getRunDuration(), summary.getAthleticDuration());
    }

    String createPeriodString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getYear()).append("-").append(Utility.twoFigure(getMonth())).append("-").append(Utility.twoFigure(getDayOfMonth()));
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
        return String.valueOf(getYear()) + getMonth();
    }

    public String createKeyForWeeklySummary() {
        int year = Utility.getYearForWeekOyYearPeriod(getYear(), getMonth(), getWeekOfYear());
        return String.valueOf(year) + getWeekOfYear();
    }

    public Calendar buildCalendar() {
        return new GregorianCalendar(getYear(), getMonth() - 1, getDayOfMonth(), 0, 0, 0);
    }

    @Override
    public int compareTo(DailySessionsSummary another) {
        return this.buildCalendar().compareTo(another.buildCalendar());
    }
}
