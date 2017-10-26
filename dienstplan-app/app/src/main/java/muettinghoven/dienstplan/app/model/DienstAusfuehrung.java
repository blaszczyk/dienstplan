package muettinghoven.dienstplan.app.model;

import java.io.Serializable;

import muettinghoven.dienstplan.app.dto.BewohnerDto;
import muettinghoven.dienstplan.app.dto.DienstAusfuehrungDto;
import muettinghoven.dienstplan.app.dto.DienstDto;
import muettinghoven.dienstplan.app.dto.Zeiteinheit;
import muettinghoven.dienstplan.app.dto.ZeitraumDto;
import muettinghoven.dienstplan.app.tools.DienstTools;

public class DienstAusfuehrung implements Serializable {

    private final int id;

    private final String bewohner;

    private final String dienst;

    private final String dienstBeschreibung;

    private final String zeitraum;

    private final Zeiteinheit zeiteinheit;

    private final String kommentar;

    private final long anfangszeit;

    private final String erinnerung;

    private final int dienstOrdnung;

    private boolean aktuell;

    public DienstAusfuehrung(final DienstAusfuehrungDto ausfuehrung, final BewohnerDto bewohner, final DienstDto dienst, final ZeitraumDto zeitraum)
    {
        this(ausfuehrung.getId(),
                DienstTools.bewohnerName(bewohner),
                dienst.getName(),dienst.getBeschreibung(),
                DienstTools.zeitraum(zeitraum),
                dienst.getZeiteinheit(),
                ausfuehrung.getKommentar(),
                zeitraum.getAnfangsdatum(),
                dienst.getErinnerung(),
                dienst.getOrdnung(),
                DienstTools.isAktuell(zeitraum));
    }

    private DienstAusfuehrung(
            final int id,
            final String bewohner,
            final String dienst,
            final String dienstBeschreibung,
            final String zeitraum,
            final Zeiteinheit zeiteinheit,
            final String kommentar,
            final long anfangszeit,
            final String erinnerung,
            final int dienstOrdnung,
            final boolean aktuell)
    {
        this.id = id;
        this.bewohner = bewohner;
        this.dienst = dienst;
        this.dienstBeschreibung = dienstBeschreibung;
        this.zeitraum = zeitraum;
        this.zeiteinheit = zeiteinheit;
        this.kommentar = kommentar;
        this.anfangszeit = anfangszeit;
        this.erinnerung = erinnerung;
        this.dienstOrdnung = dienstOrdnung;
        this.aktuell = aktuell;
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

    public Zeiteinheit getZeiteinheit() {
        return zeiteinheit;
    }

    public String getKommentar()
    {
        return kommentar;
    }

    public long getAnfangszeit() {
        return anfangszeit;
    }

    public String getErinnerung() {
        return erinnerung;
    }

    public int getDienstOrdnung() {
        return dienstOrdnung;
    }

    public boolean isAktuell() {
        return aktuell;
    }

    @Override
    public String toString() {
        return "DienstAusfuehrung{" +
                "id=" + id +
                ", bewohner='" + bewohner + '\'' +
                ", dienst='" + dienst + '\'' +
                ", dienstBeschreibung='" + dienstBeschreibung + '\'' +
                ", zeitraum='" + zeitraum + '\'' +
                ", kommentar='" + kommentar + '\'' +
                ", anfangszeit=" + anfangszeit +
                ", dienstOrdnung=" + dienstOrdnung +
                ", aktuell=" + aktuell +
                '}';
    }
}
