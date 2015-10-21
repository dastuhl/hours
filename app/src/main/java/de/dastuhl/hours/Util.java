package de.dastuhl.hours;

/**
 * Created by Martin on 18.10.2015.
 */
public class Util {

    private Util() {

    }

    public static String getTimeString(Integer minutes) {
        int hours = 0;
        int min = 0;
        if (minutes != null) {
            hours = minutes / 60;
            min = minutes % 60;
        }

        return new StringBuilder().append(twoFigure(hours)).append(":").append(twoFigure(min))
                .toString();
    }

    public static String twoFigure(int number) {
        String str = "00";
        String numberString = String.valueOf(number);
        return (str + numberString).substring(numberString.length());
    }
}
