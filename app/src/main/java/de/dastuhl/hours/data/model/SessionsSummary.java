package de.dastuhl.hours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Martin on 24.09.2015.
 */
public class SessionsSummary implements Parcelable {

    public static final String WEEK_PROPERTY = "weekOfYear";
    public static final String MONTH_PROPERTY = "month";
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

    protected String twoFigure(int number) {
        String str = "00";
        String numberString = String.valueOf(number);
        return (str + numberString).substring(numberString.length());
    }

    /*
     * Parcelable
     */
    public SessionsSummary(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.weekOfYear = in.readInt();
        this.dayOfMonth = in.readInt();
        this.athleticDuration = in.readInt();
        this.swimDuration = in.readInt();
        this.cycleDuration = in.readInt();
        this.runDuration = in.readInt();
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
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.weekOfYear);
        dest.writeInt(this.dayOfMonth);
        dest.writeInt(this.athleticDuration);
        dest.writeInt(this.swimDuration);
        dest.writeInt(this.cycleDuration);
        dest.writeInt(this.runDuration);
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
