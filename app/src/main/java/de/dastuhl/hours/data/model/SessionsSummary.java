package de.dastuhl.hours.data.model;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsSummary {

    public static final String YEAR_PROPERTY = "year";

    // yyyy/mm/dd for day-session, yyyy/mm for monthly summary, yyyy for yearly summary, yyyy-weekNr for weekly summary
    private Integer year;
    private Integer month;
    private Integer weekOfYear;
    private Integer dayOfMonth;
    private Integer swimDuration;
    private Integer cycleDuration;
    private Integer runDuration;
    private Integer athleticDuration;

    public SessionsSummary() {

    }

    public SessionsSummary(Integer pYear, Integer pMonth, Integer pWeekOfYear, Integer pDayOfMonth,
                           Integer pSwimDuration, Integer pCycleDuration, Integer pRunDuration, Integer pAthleticDuration) {
        this.athleticDuration = pAthleticDuration;
        this.cycleDuration = pCycleDuration;
        this.runDuration = pRunDuration;
        this.swimDuration = pSwimDuration;
        this.dayOfMonth = pDayOfMonth;
        this.weekOfYear = pWeekOfYear;
        this.month = pMonth;
        this.year = pYear;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public Integer getSwimDuration() {
        return swimDuration;
    }

    public Integer getCycleDuration() {
        return cycleDuration;
    }

    public Integer getRunDuration() {
        return runDuration;
    }

    public Integer getAthleticDuration() {
        return athleticDuration;
    }

    public void setAthleticDuration(Integer pAthleticDuration) {
        athleticDuration = pAthleticDuration;
    }

    public void setCycleDuration(Integer pCycleDuration) {
        cycleDuration = pCycleDuration;
    }

    public void setRunDuration(Integer pRunDuration) {
        runDuration = pRunDuration;
    }

    public void setSwimDuration(Integer pSwimDuration) {
        swimDuration = pSwimDuration;
    }

    String createPeriodString() {
        return "";
    }

    public Long createPriority() {
        String prio = createPeriodString();
        prio = prio.replaceAll("\\D+", "");
        return Long.valueOf(prio) * -1;
    }

    public Integer computeTotal() {
        Integer total = (this.swimDuration == null ? 0 : this.swimDuration)
                + (this.athleticDuration == null ? 0 : this.athleticDuration)
                + (this.cycleDuration == null ? 0 : this.cycleDuration)
                + (this.runDuration == null ? 0 : this.runDuration);
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionsSummary)) return false;

        SessionsSummary summary = (SessionsSummary) o;

        if (!year.equals(summary.year)) return false;
        if (month != null ? !month.equals(summary.month) : summary.month != null) return false;
        if (weekOfYear != null ? !weekOfYear.equals(summary.weekOfYear) : summary.weekOfYear != null)
            return false;
        return !(dayOfMonth != null ? !dayOfMonth.equals(summary.dayOfMonth) : summary.dayOfMonth != null);

    }

    @Override
    public int hashCode() {
        int result = year.hashCode();
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (weekOfYear != null ? weekOfYear.hashCode() : 0);
        result = 31 * result + (dayOfMonth != null ? dayOfMonth.hashCode() : 0);
        return result;
    }

}
