package muettinghoven.dienstplan.app.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import muettinghoven.dienstplan.app.dto.BewohnerDto;
import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.dto.ZeitraumDto;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;

public class DienstTools {

    private static final long MILLIS_PRO_TAG_L = 24L * 60L * 60L * 1000L;

    private static final float MILLIS_PRO_TAG_F = 24f * 60f * 60f * 1000f;

    private static final Comparator<DienstAusfuehrung> BY_NEXT_ERINNERUNG = new Comparator<DienstAusfuehrung>() {
        @Override
        public int compare(final DienstAusfuehrung a1, final DienstAusfuehrung a2) {
            return Integer.compare(nextErinnerung(a1),nextErinnerung(a2));
        }
    };

    public static String zeitraum(final ZeitraumDto zeitraum)
    {
        final Calendar calendar = GregorianCalendar.getInstance();
        final Date datum = new Date(zeitraum.getAnfangsdatum());
        calendar.setTime(datum);
        switch (zeitraum.getZeiteinheit())
        {
            case TAG:
                final DateFormat dateFormatTag = new SimpleDateFormat("EEE dd.MM.yyyy", Locale.GERMAN);
                return dateFormatTag.format(datum);
            case WOCHE:
                calendar.add(Calendar.DAY_OF_YEAR, 6);
                final Date endWoche = calendar.getTime();
                final DateFormat dateFormatWocheEnd = new SimpleDateFormat("dd.MM.yyyy");
                final DateFormat dateFormatWocheStart;
                if(flips(datum,endWoche,Calendar.YEAR))
                    dateFormatWocheStart = dateFormatWocheEnd;
                else if(flips(datum,endWoche,Calendar.MONTH))
                    dateFormatWocheStart = new SimpleDateFormat("dd.MM.");
                else
                    dateFormatWocheStart = new SimpleDateFormat("dd.");
                return dateFormatWocheStart.format(datum) + " - " + dateFormatWocheEnd.format(endWoche);
            case MONAT:
                final DateFormat dateFormatMonat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
                return dateFormatMonat.format(datum);
        }
        return "invalid";
    }

    private static boolean flips(final Date start, final Date end, final int unit) {
        final Calendar c = Calendar.getInstance();
        c.setTime(start);
        final int m1 = c.get(unit);
        c.setTime(end);
        final int m2 = c.get(unit);
        return m1 != m2;
    }

    public static boolean isAktuell(final ZeitraumDto zeitraum) {
        final long diff = zeitraum.getAnfangsdatum() - System.currentTimeMillis();
        final float diffTage = diff / MILLIS_PRO_TAG_F;
        final float lowerBound = lowerBound(zeitraum.getZeiteinheit());
        final float upperBound = upperBound(zeitraum.getZeiteinheit());
        return lowerBound < diffTage && diffTage < upperBound;
    }

    private static float lowerBound(final Zeiteinheit einheit) {
        switch (einheit) {
            case TAG:
                return -1f;
            case WOCHE:
                return -7f;
            case MONAT:
                return -31f;
        }
        return 1;
    }
    private static float upperBound(final Zeiteinheit einheit) {
        switch (einheit) {
            case TAG:
                return 0f;
            case WOCHE:
                return 0f;
            case MONAT:
                return 0f;
        }
        return 1;
    }

    public static int findErsterAktueller(final List<DienstAusfuehrung> dienste) {
        for(int i = 0; i < dienste.size(); i++)
            if(dienste.get(i).isAktuell())
                return i;
        return 0;
    }

    public static int aktueller(final List<DienstContainer> containers) {
        for(int i = 0; i < containers.size(); i++)
            if(containers.get(i).isAktuell())
                return i;
        return 0;
    }

    public static String einheitName(final Zeiteinheit e) {
        switch (e) {
            case TAG:
                return "Tage";
            case WOCHE:
                return "Wochen";
            case MONAT:
                return "Monate";
        }
        return "you should not see this";
    }

    public static int nextErinnerung(final Iterable<DienstAusfuehrung> container) {
        int nextErinnerung = Integer.MAX_VALUE;
        for(final DienstAusfuehrung ausfuehrung : container)
            if(ausfuehrung.isAktuell())
                nextErinnerung = Math.min(nextErinnerung, nextErinnerung(ausfuehrung));
        return nextErinnerung;
    }

    public static int nextErinnerung(final DienstAusfuehrung ausfuehrung) {
        final String erinnerung = isParsableErinnerung(ausfuehrung.getErinnerung()) ?
                ausfuehrung.getErinnerung() :
                getDefaultErinnerung(ausfuehrung.getZeiteinheit());
        final String[] split = erinnerung.split("\\,");
        final int start = Integer.parseInt(split[0]);
        final int intervall = Integer.parseInt(split[1]);
        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour < start)
            return start - hour;
        else
            return intervall - 1;
    }

    public static List<DienstAusfuehrung> sortedByErinnerung(final Iterable<DienstAusfuehrung> ausfuehrungs) {
        final List<DienstAusfuehrung> result = new ArrayList<>();
        for(final DienstAusfuehrung ausfuehrung : ausfuehrungs)
            if (ausfuehrung.isAktuell())
                result.add(ausfuehrung);
        Collections.sort(result, BY_NEXT_ERINNERUNG);
        return result;
    }

    private static boolean isParsableErinnerung(final String erinnerung) {
        return erinnerung != null && Pattern.matches("^\\d+\\,\\d+$",erinnerung);
    }

    private static String getDefaultErinnerung(final Zeiteinheit zeiteinheit) {
        switch (zeiteinheit) {
            case TAG:
                return "8,2";
            case WOCHE:
                return "9,24";
            case MONAT:
                return "10,72";
        }
        return "7,1";
    }

    public static int ordnung(final ZeitraumDto zeitraumDto) {
        return (int) ( zeitraumDto.getAnfangsdatum() / MILLIS_PRO_TAG_L );
    }

    public static String bewohnerName(final BewohnerDto bewohner) {
        return bewohner == null ? "noch keiner" : bewohner.getName();
    }
}
