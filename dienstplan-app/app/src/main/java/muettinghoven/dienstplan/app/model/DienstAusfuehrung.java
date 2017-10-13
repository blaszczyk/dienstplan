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
import muettinghoven.dienstplan.app.tools.DienstTools;

public class DienstAusfuehrung implements Serializable {

    private final int id;

    private final String bewohner;

    private final String dienst;

    private final String dienstBeschreibung;

    private final String zeitraum;

    private final String kommentar;

    public DienstAusfuehrung(final DienstAusfuehrungDto ausfuehrung, final BewohnerDto bewohner, final DienstDto dienst, final ZeitraumDto zeitraum)
    {
        this(ausfuehrung.getId(),bewohner.getName(),dienst.getName(),dienst.getBeschreibung(), DienstTools.zeitraum(zeitraum),ausfuehrung.getKommentar());
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
}
