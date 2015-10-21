package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsSummary implements Parcelable {

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

    public String createTimerangeString() {
        return "";
    }

    public Long createPriority() {
        String prio = createTimerangeString();
        prio = prio.replaceAll("-", "");
        return Long.valueOf(prio) * -1;
    }

    public Integer computeTotals() {
        Integer total = (this.swimDuration == null ? 0 : this.swimDuration)
                + (this.athleticDuration == null ? 0 : this.athleticDuration)
                + (this.cycleDuration == null ? 0 : this.cycleDuration)
                + (this.runDuration == null ? 0 : this.runDuration);
        return total;
    }

    /*
     * Parcelable
     */
    public SessionsSummary(Parcel in) {
        this.year = (Integer) in.readValue(null);
        this.month = (Integer) in.readValue(null);
        this.weekOfYear = (Integer) in.readValue(null);
        this.dayOfMonth = (Integer) in.readValue(null);
        this.athleticDuration = (Integer) in.readValue(null);
        this.swimDuration = (Integer) in.readValue(null);
        this.cycleDuration = (Integer) in.readValue(null);
        this.runDuration = (Integer) in.readValue(null);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.year);
        dest.writeValue(this.month);
        dest.writeValue(this.weekOfYear);
        dest.writeValue(this.dayOfMonth);
        dest.writeValue(this.athleticDuration);
        dest.writeValue(this.swimDuration);
        dest.writeValue(this.cycleDuration);
        dest.writeValue(this.runDuration);
    }

    public static final Parcelable.Creator<SessionsSummary> CREATOR
            = new Parcelable.Creator<SessionsSummary>() {
        public SessionsSummary createFromParcel(Parcel in) {
            return new SessionsSummary(in);
        }

        public SessionsSummary[] newArray(int size) {
            return new SessionsSummary[size];
        }
    };
}
