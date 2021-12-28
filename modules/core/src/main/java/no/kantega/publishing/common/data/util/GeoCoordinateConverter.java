package no.kantega.publishing.common.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoCoordinateConverter {
    private static final Logger log = LoggerFactory.getLogger(GeoCoordinateConverter.class);
    /**
     * Converts various latitude/longitude formats to a long
     * Supported formats:  H"M'S and H/1,M/1,S/1000
     *
     * @param hoursMinutesAndSeconds
     * @return
     */
    public static double convertHoursMinutesAndSecondsStringToDouble(String hoursMinutesAndSeconds) {
        double result = -1;

        hoursMinutesAndSeconds = replaceUnwantedCharsWithSpace(hoursMinutesAndSeconds);

        String hmsParts[] = hoursMinutesAndSeconds.split(" ");
        if (hmsParts.length == 3) {
            double h = convertPartToLong(hmsParts[0]);
            double m = convertPartToLong(hmsParts[1]);
            double s = convertPartToLong(hmsParts[2]);

            result = h + (m / 60) + (s / 3600);
        }

        return result;
    }

    private static String replaceUnwantedCharsWithSpace(String hms) {
        hms = hms.replace('"', ' ');
        hms = hms.replace('\'', ' ');
        hms = hms.replace(',', ' ');
        return hms;
    }

    private static double convertPartToLong(String hmsPart) {
        double result = -1;
        try {
            if (hmsPart.contains("/")) {
                String[] parts = hmsPart.split("/", 2);
                result = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            } else {
                result = Double.parseDouble(hmsPart);
            }

        } catch (NumberFormatException e) {
            log.error("", e);
        }
        return result;
    }
}
