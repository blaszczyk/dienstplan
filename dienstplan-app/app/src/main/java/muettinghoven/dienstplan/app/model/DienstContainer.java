package muettinghoven.dienstplan.app.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DienstContainer implements Comparable<DienstContainer>{

    public static enum Typ {
        DIENST,
        ZEITRAUM,
        BEWOHNER,
        AKTUELL;
    }

    private final int id;

    private final String name;

    private final Typ typ;

    private final int ordnung;

    private final List<DienstAusfuehrung> ausfuehrungen;

    public DienstContainer(final int id, final String name, final Typ typ, final int ordnung) {
        this.id = id;
        this.name = name;
        this.typ = typ;
        this.ordnung = ordnung;
        this.ausfuehrungen = new ArrayList<>();
    }

    public boolean add(final DienstAusfuehrung ausfuehrung) {
        return ausfuehrungen.add(ausfuehrung);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Typ getTyp() {
        return typ;
    }

    public int getOrdnung() {
        return ordnung;
    }

    public List<DienstAusfuehrung> getAusfuehrungen() {
        return ausfuehrungen;
    }

    public void sort(Comparator<? super DienstAusfuehrung> comparator) {
        Collections.sort(ausfuehrungen,comparator);
    }

    public void klumpeAktuelle() {
        int lastAktuell = -1;
        for(int i = ausfuehrungen.size() - 1; i >= 0; i--) {
            final DienstAusfuehrung ausfuehrung = ausfuehrungen.get(i);
            if (ausfuehrung.isAktuell()) {
                if(lastAktuell < 0)
                    lastAktuell = i;
                else {
                    lastAktuell--;
                    if (i < lastAktuell)
                        moveTo(i, lastAktuell);
                }
            }
        }
    }

    private void moveTo(final int from, final int to) {
        final DienstAusfuehrung mover = ausfuehrungen.get(from);
        for(int i = from; i < to; i++)
            ausfuehrungen.set(i,ausfuehrungen.get(i+1));
        ausfuehrungen.set(to,mover);
    }

    public boolean isAktuell() {
        for(final DienstAusfuehrung ausfuehrung : ausfuehrungen)
            if(ausfuehrung.isAktuell())
                return true;
        return false;
    }

    @Override
    public int compareTo(@NonNull final DienstContainer that) {
        return Integer.compare(this.ordnung,that.ordnung);
    }

}
