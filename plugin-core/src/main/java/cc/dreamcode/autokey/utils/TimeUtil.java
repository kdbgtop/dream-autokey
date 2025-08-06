package cc.dreamcode.autokey.utils;

public final class TimeUtil {

    private TimeUtil() {}

    public static String convertSecondsToDisplay(long seconds) {
        if (seconds >= 3600) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " godzina" : " godziny");
        } else if (seconds >= 60) {
            long minutes = seconds / 60;
            return minutes + (minutes == 1 ? " minuta" : " minuty");
        } else {
            return seconds + (seconds == 1 ? " sekunda" : " sekundy");
        }
    }
}