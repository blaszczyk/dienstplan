package muettinghoven.dienstplan.app.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import muettinghoven.dienstplan.app.dto.BewohnerDto;
import muettinghoven.dienstplan.app.dto.DienstAusfuehrungDto;
import muettinghoven.dienstplan.app.dto.DienstDto;
import muettinghoven.dienstplan.app.dto.ZeitraumDto;

public class DienstAusfuehrung implements Serializable {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static final Calendar CALENDAR = Calendar.getInstance();

    private final int id;

    private final String bewohner;

    private final String dienst;

    private final String dienstBeschreibung;

    private final String zeitraum;

    private final String kommentar;

    public DienstAusfuehrung(final DienstAusfuehrungDto ausfuehrung, final BewohnerDto bewohner, final DienstDto dienst, final ZeitraumDto zeitraum)
    {
        this(ausfuehrung.getId(),bewohner.getName(),dienst.getName(),dienst.getBeschreibung(),zeitraum(zeitraum),ausfuehrung.getKommentar());
    }

    public DienstAusfuehrung(final int id, final String bewohner, final String dienst, final String dienstBeschreibung, final String zeitraum, final String kommentar)
    {
        this.id = id;
        this.bewohner = bewohner;
        this.dienst = dienst;
        this.dienstBeschreibung = dienstBeschreibung;
        this.zeitraum = zeitraum;
        this.kommentar = kommentar;
    }

    public int getId()
    {
        return id;
    }

    public String getBewohner()
    {
        return bewohner;
    }

    public String getDienst()
    {
        return dienst;
    }

    public String getDienstBeschreibung() {
        return dienstBeschreibung;
    }

    public String getZeitraum()
    {
        return zeitraum;
    }

    public String getKommentar()
    {
        return kommentar;
    }

    @Override
    public String toString()
    {
        return "id:" + id + ", bewohner:" + bewohner + ", dienst:" + dienst + ", dienstBeschreibung:" + dienstBeschreibung + ", zeitraum:" + zeitraum + ", kommentar:" + kommentar;
    }


    private static String zeitraum(final ZeitraumDto zeitraum)
    {
        final Date datum = new Date(zeitraum.getAnfangsdatum());
        CALENDAR.setTime(datum);
        switch (zeitraum.getZeiteinheit())
        {
            case TAG:
                return DATE_FORMAT.format(datum);
            case WOCHE:
                CALENDAR.add(Calendar.DAY_OF_YEAR, 7);
                return DATE_FORMAT.format(datum) + " - " + DATE_FORMAT.format(CALENDAR.getTime());
            case MONAT:
                CALENDAR.add(Calendar.MONTH, 1);
                return DATE_FORMAT.format(datum) + " - " + DATE_FORMAT.format(CALENDAR.getTime());
        }
        return "invalid";
    }
}
