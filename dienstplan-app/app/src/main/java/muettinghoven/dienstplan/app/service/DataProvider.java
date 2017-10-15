package muettinghoven.dienstplan.app.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import muettinghoven.dienstplan.app.dto.*;
import muettinghoven.dienstplan.app.model.*;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.tools.DienstTools;

public class DataProvider {

    private final Comparator<DienstAusfuehrung> BY_DATUM = new Comparator<DienstAusfuehrung>() {
        @Override
        public int compare(final DienstAusfuehrung a1, final DienstAusfuehrung a2) {
            return Long.compare(a1.getAnfangszeit(),a2.getAnfangszeit());
        }
    };

    private  final Comparator<DienstAusfuehrung> BY_DIENST = new Comparator<DienstAusfuehrung>() {
        @Override
        public int compare(final DienstAusfuehrung a1, final DienstAusfuehrung a2) {
            return a1.getDienst().compareTo(a2.getDienst());
        }
    };

    private final DataCache provider;

    public DataProvider(DataCache provider) {
        this.provider = provider;
    }

    public Dienstplan getPlan(final int id) throws ServiceException {
        final DienstplanDto planDto = provider.getDienstplan(id);
        final Dienstplan plan = new Dienstplan(id,planDto.getName());

        final Map<Integer,DienstContainer> zeitraeume = new TreeMap<>();
        for(final int zeitraumId : planDto.getZeitraumsIds())
        {
            final ZeitraumDto zeitraumDto = provider.getZetiraum(zeitraumId);
            final DienstContainer zeitraum = new DienstContainer(zeitraumId, DienstTools.zeitraum(zeitraumDto), DienstContainer.Typ.ZEITRAUM);
            plan.addZeitraum(zeitraumDto.getZeiteinheit(),zeitraum);
            zeitraeume.put(zeitraumId,zeitraum);
        }

        final Map<Integer,DienstContainer> dienste = new TreeMap<>();
        for(final int dienstId : planDto.getDienstsIds())
        {
            final DienstDto dienstDto = provider.getDienst(dienstId);
            final DienstContainer dienst = new DienstContainer(dienstId, dienstDto.getName(), DienstContainer.Typ.DIENST);
            plan.addDienst(dienst);
            for(final int ausfuehrungId : dienstDto.getDienstAusfuehrungsIds())
            {
                final DienstAusfuehrungDto ausfuehrungDto = provider.getDienstausfuehrung(ausfuehrungId);
                final BewohnerDto bewohnerDto = provider.getBewohner(ausfuehrungDto.getBewohnerId());
                final ZeitraumDto zeitraumDto = provider.getZetiraum(ausfuehrungDto.getZeitraumId());
                final DienstAusfuehrung ausfuehrung = new DienstAusfuehrung(ausfuehrungDto,bewohnerDto,dienstDto,zeitraumDto);

                dienst.add(ausfuehrung);
                zeitraeume.get(ausfuehrungDto.getZeitraumId()).add(ausfuehrung);
                if(ausfuehrung.isAktuell())
                    plan.addAktueller(ausfuehrung);
            }
            dienst.sort(BY_DATUM);
        }

        for(final DienstContainer zeitraum : dienste.values())
            zeitraum.sort(BY_DIENST);

        plan.sortContainers();
        plan.sortAktuell(BY_DATUM);
        return plan;
    }

    public DienstContainer getBewohner(final int bewohnerID) throws ServiceException
    {
        final BewohnerDto bewohnerDto = provider.getBewohner(bewohnerID);
        final DienstContainer bewohner = new DienstContainer(bewohnerID,bewohnerDto.getName(), DienstContainer.Typ.BEWOHNER);

        final int[] ids = bewohnerDto.getDienstAusfuehrungsIds();
        final List<DienstAusfuehrungDto> dtos = provider.getDienstausfuehrung(toList(ids));
        for(final DienstAusfuehrungDto dto : dtos)
        {
            final DienstDto dienst = provider.getDienst(dto.getDienstId());
            final ZeitraumDto zeitraum = provider.getZetiraum(dto.getZeitraumId());
            final DienstAusfuehrung dienstAusfuehrung = new DienstAusfuehrung(dto,bewohnerDto,dienst,zeitraum);
            bewohner.add(dienstAusfuehrung);
        }
        bewohner.sort(BY_DATUM);
        return bewohner;
    }

    public Map<Integer,String> getDienstplaene(final int bewohnerId) throws ServiceException{
        final BewohnerDto bewohner = provider.getBewohner(bewohnerId);
        final List<DienstplanDto> dienstplaene = provider.getDienstplan(toList(bewohner.getDienstplansIds()));
        final Map<Integer,String> namen = new LinkedHashMap<>(dienstplaene.size());
        for(final DienstplanDto dienstplan : dienstplaene)
            namen.put(dienstplan.getId(),dienstplan.getName());
        return namen;
    }

    private static List<Integer> toList(final int[] ints)
    {
        final List<Integer> list = new ArrayList<>(ints.length);
        for(final int i : ints)
            list.add(i);
        return list;
    }

}
