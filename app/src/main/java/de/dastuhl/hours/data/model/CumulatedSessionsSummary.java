package de.dastuhl.hours.data.model;

/**
 * Created by Martin on 24.09.2015.
 */
public abstract class CumulatedSessionsSummary extends SessionsSummary {

    public CumulatedSessionsSummary() {

    }

    public CumulatedSessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear,
                                Integer pDayOfYear, Integer pSwimDuration, Integer pCycleDuration,
                                Integer pRunDuration, Integer pAthleticDuration) {
        super(pYear, pMonth,  pWeekOfYear, pDayOfYear, pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public void addSessionDurations(DailySessionsSummary dailySummary) {
        this.setAthleticDuration(this.getAthleticDuration() + dailySummary.getAthleticDuration());
        this.setSwimDuration(this.getSwimDuration() + dailySummary.getSwimDuration());
        this.setCycleDuration(this.getCycleDuration() + dailySummary.getCycleDuration());
        this.setRunDuration(this.getRunDuration() + dailySummary.getRunDuration());
    }

}
