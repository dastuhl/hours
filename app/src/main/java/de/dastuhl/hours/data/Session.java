package de.dastuhl.hours.data;

/**
 * Created by Martin on 24.09.2015.
 */
public class Session {

    private String userId;
    private Integer year;
    private Integer month;
    private String date;
    private Integer swimDuration;
    private Integer cycleDuration;
    private Integer runDuration;
    private Integer athleticDuration;

    public Session() {

    }

    public Session(String pUserId, Integer pYear, Integer pMonth, String pDate, Integer pSwimDuration, Integer pCycleDuration, Integer pRunDuration, Integer pAthleticDuration) {
        userId = pUserId;
        year = pYear;
        month = pMonth;
        date = pDate;
        swimDuration = pSwimDuration;
        cycleDuration = pCycleDuration;
        runDuration = pRunDuration;
        athleticDuration = pAthleticDuration;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public String getDate() {
        return date;
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
}
