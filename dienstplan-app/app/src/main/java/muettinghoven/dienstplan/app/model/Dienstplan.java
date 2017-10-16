package muettinghoven.dienstplan.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.dto.Zeiteinheit;

public class Dienstplan {

    private final int id;
    private final String name;
    private final DienstContainer aktuell;
    private final List<DienstContainer> dienste;
    private final Map<Zeiteinheit,List<DienstContainer>> zeitraeume;

    public Dienstplan(int id, String name) {
        this.id = id;
        this.name = name;
        aktuell = new DienstContainer(0,"Aktuell", DienstContainer.Typ.AKTUELL, 0);
        dienste = new ArrayList<>();
        zeitraeume = new EnumMap<Zeiteinheit, List<DienstContainer>>(Zeiteinheit.class);
        for(final Zeiteinheit einheit : Zeiteinheit.values())
            zeitraeume.put(einheit,new ArrayList<DienstContainer>());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DienstContainer getAktuell() {
        return aktuell;
    }

    public List<DienstContainer> getDienste() {
        return dienste;
    }

    public List<DienstContainer> getZeitraeume(final Zeiteinheit einheit) {
        return zeitraeume.get(einheit);
    }

    public void addAktueller(final DienstAusfuehrung ausfuehrung) {
        aktuell.add(ausfuehrung);
    }

    public void addDienst(final DienstContainer dienst) {
        dienste.add(dienst);
    }

    public void addZeitraum(final Zeiteinheit einheit, final DienstContainer zeitraum) {
        zeitraeume.get(einheit).add(zeitraum);
    }

    public List<DienstContainer> getContainingList(final DienstContainer container) {
        if(dienste.contains(container))
            return dienste;
        for(final List<DienstContainer> containers : zeitraeume.values())
            if (containers.contains(container))
                return containers;
        return Collections.emptyList();
    }

    public void sortContainers() {
        Collections.sort(dienste);
        for(final List<DienstContainer> containers : zeitraeume.values())
            Collections.sort(containers);
    }

    public void sortAktuell(final Comparator<? super DienstAusfuehrung> comparator) {
        aktuell.sort(comparator);
        aktuell.klumpeAktuelle();
    }
}
