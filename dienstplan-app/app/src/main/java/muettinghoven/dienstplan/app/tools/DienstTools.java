package muettinghoven.dienstplan.app.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import muettinghoven.dienstplan.app.dto.ZeitraumDto;

public class DienstTools {




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
}
