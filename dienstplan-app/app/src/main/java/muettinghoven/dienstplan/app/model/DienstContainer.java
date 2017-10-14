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
        BEWOHNER;
    }

    private final int id;

    private final String name;

    private final Typ typ;

    private final List<DienstAusfuehrung> ausfuehrungen;

    public DienstContainer(final int id, final String name, final Typ typ) {
        this.id = id;
        this.name = name;
        this.typ = typ;
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

    public List<DienstAusfuehrung> getAusfuehrungen() {
        return ausfuehrungen;
    }

    public void sort(Comparator<? super DienstAusfuehrung> comparator) {
        Collections.sort(ausfuehrungen,comparator);
    }

    public boolean isAktuell() {
        for(final DienstAusfuehrung ausfuehrung : ausfuehrungen)
            if(ausfuehrung.isAktuell())
                return true;
        return false;
    }

    @Override
    public int compareTo(@NonNull final DienstContainer that) {
        return Integer.compare(this.id,that.id);
    }

}
