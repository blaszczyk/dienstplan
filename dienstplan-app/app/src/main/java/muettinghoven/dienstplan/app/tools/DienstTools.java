package muettinghoven.dienstplan.app.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.dto.ZeitraumDto;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;

public class DienstTools {


    private static final long MILLIS_PRO_TAG = 24L * 60L * 60L * 1000L;

    public static String zeitraum(final ZeitraumDto zeitraum)
    {

        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar calendar = GregorianCalendar.getInstance();
        final Date datum = new Date(zeitraum.getAnfangsdatum());
        calendar.setTime(datum);
        switch (zeitraum.getZeiteinheit())
        {
            case TAG:
                return dateFormat.format(datum);
            case WOCHE:
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                return dateFormat.format(datum) + " - " + dateFormat.format(calendar.getTime());
            case MONAT:
                calendar.add(Calendar.MONTH, 1);
                return dateFormat.format(datum) + " - " + dateFormat.format(calendar.getTime());
        }
        return "invalid";
    }

    public static boolean isAktuell(final ZeitraumDto zeitraum) {
        final long diff = zeitraum.getAnfangsdatum() - System.currentTimeMillis();
        final long diffTage = diff / MILLIS_PRO_TAG;
        final long lowerBound = lowerBound(zeitraum.getZeiteinheit());
        final long upperBound = upperBound(zeitraum.getZeiteinheit());
        return lowerBound <= diffTage && diffTage <= upperBound;
    }

    private static long lowerBound(final Zeiteinheit einheit) {
        switch (einheit) {
            case TAG:
                return -1;
            case WOCHE:
                return -7;
            case MONAT:
                return -31;
        }
        return 1;
    }
    private static long upperBound(final Zeiteinheit einheit) {
        switch (einheit) {
            case TAG:
                return 1;
            case WOCHE:
                return 7;
            case MONAT:
                return 31;
        }
        return 1;
    }

    public static int findErsterAktueller(final List<DienstAusfuehrung> dienste) {
        for(int i = 0; i < dienste.size(); i++)
            if(dienste.get(i).isAktuell())
                return i;
        return 0;
    }

    public static int aktueller(List<DienstContainer> containers) {
        for(int i = 0; i < containers.size(); i++)
            if(containers.get(i).isAktuell())
                return i;
        return 0;
    }
}
