package de.dastuhl.hours.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Martin on 24.09.2015.
 */
public class Session implements Parcelable {

    // yyyy/mm/dd for day-session, yyyy/mm for monthly summary, yyyy for yearly summary, yyyy-weekNr for weekly summary
    private Integer year;
    private Integer month;
    private Integer weekOfYear;
    private Integer dayOfMonth;
    private Integer swimDuration;
    private Integer cycleDuration;
    private Integer runDuration;
    private Integer athleticDuration;

    public Session() {

    }

    public Session(Calendar sessionDate, Integer pSwimDuration, Integer pCycleDuration, Integer pRunDuration, Integer pAthleticDuration) {
        this(sessionDate.get(Calendar.YEAR), sessionDate.get(Calendar.MONTH) + 1, sessionDate.get(Calendar.WEEK_OF_YEAR), sessionDate.get(Calendar.DAY_OF_MONTH),
                pSwimDuration, pCycleDuration, pRunDuration, pAthleticDuration);
    }

    public Session(Integer pYear, Integer pMonth, Integer pWeekOfYear, Integer pDayOfMonth,
                   Integer pSwimDuration, Integer pCycleDuration, Integer pRunDuration, Integer pAthleticDuration) {
        year = pYear;
        month = pMonth;
        weekOfYear = pWeekOfYear;
        dayOfMonth = pDayOfMonth;
        swimDuration = pSwimDuration;
        cycleDuration = pCycleDuration;
        runDuration = pRunDuration;
        athleticDuration = pAthleticDuration;
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
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        if (dayOfMonth != null && dayOfMonth.intValue() > 0) {
            builder.append("-").append(twoFigure(month)).append("-").append(twoFigure(dayOfMonth));
        } else if (weekOfYear != null && weekOfYear.intValue() > 0) {
            builder.append("-").append(twoFigure(weekOfYear));
        } else if (month != null && month.intValue() > 0) {
            builder.append("-").append(twoFigure(month));
        }
        String result = builder.toString();
        if (result.length() != 10) {
            throw new IllegalStateException("ungültiges Datum " + result);
        }
        return result;
    }

    public Long createPriority() {
        String prio = createTimerangeString();
        prio = prio.replaceAll("-", "");
        return Long.valueOf(prio) * -1;
    }

    private String twoFigure(int number) {
        String str = "00";
        String numberString = String.valueOf(number);
        return (str + numberString).substring(numberString.length());
    }


    /*
     * Parcelable
     */
    public Session (Parcel in) {
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

    public static final Parcelable.Creator<Session> CREATOR
            = new Parcelable.Creator<Session>() {
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
