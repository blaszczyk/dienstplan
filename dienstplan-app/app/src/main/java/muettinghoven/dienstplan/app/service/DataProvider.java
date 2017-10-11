package muettinghoven.dienstplan.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import muettinghoven.dienstplan.ContainerAdapter;
import muettinghoven.dienstplan.app.dto.*;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;

public class DataProvider {

    private final DataCache provider;

    public DataProvider(DataCache provider) {
        this.provider = provider;
    }


    public List<DienstAusfuehrung> forBewohner(final int bewohnerID) throws ServiceException
    {
        final BewohnerDto bewohner = provider.getBewohner(1);
        final int[] ids = bewohner.getDienstAusfuehrungsIds();
        final List<DienstAusfuehrungDto> dtos = provider.getDienstausfuehrung(toList(ids));
        final List<DienstAusfuehrung> dienste = new ArrayList<>();
        for(final DienstAusfuehrungDto dto : dtos)
        {
            final DienstDto dienst = provider.getDienst(dto.getDienstId());
            final ZeitraumDto zeitraum = provider.getZetiraum(dto.getZeitraumId());
            final DienstAusfuehrung dienstAusfuehrung = new DienstAusfuehrung(dto,bewohner,dienst,zeitraum);
            dienste.add(dienstAusfuehrung);
        }
        return dienste;
    }

    private static List<Integer> toList(final int[] ints)
    {
        final List<Integer> list = new ArrayList<>(ints.length);
        for(final int i : ints)
            list.add(i);
        return list;
    }

    public Map<Integer,String> getDienstplaene(final int bewohnerId) throws ServiceException{
        final BewohnerDto bewohner = provider.getBewohner(bewohnerId);
        final List<DienstplanDto> dienstplaene = provider.getDienstplan(asList(bewohner.getDienstplansIds()));
        final Map<Integer,String> namen = new LinkedHashMap<>(dienstplaene.size());
        for(final DienstplanDto dienstplan : dienstplaene)
            namen.put(dienstplan.getId(),dienstplan.getName());
        return namen;
    }

    public Map<Integer,String> getDienstNamen(final int planId) throws ServiceException{
        final DienstplanDto plan = provider.getDienstplan(planId);
        final List<DienstDto> dienste = provider.getDienst(asList(plan.getDienstsIds()));
        final Map<Integer,String> result = new TreeMap<>();
        for(final DienstDto dienst : dienste)
            result.put(dienst.getId(), dienst.getName());
        return result;
    }

    public Map<Integer,String> getZeitraeume(final int planId, final Zeiteinheit einheit) throws ServiceException{
        final DienstplanDto plan = provider.getDienstplan(planId);
        final List<ZeitraumDto> zeitraeume = provider.getZetiraum(asList(plan.getZeitraumsIds()));
        final Map<Integer,String> result = new TreeMap<>();
        for(final ZeitraumDto zeitraum :zeitraeume )
            if(einheit.equals(zeitraum.getZeiteinheit()))
                result.put(zeitraum.getId(),DienstAusfuehrung.zeitraum(zeitraum));
        return result;
    }


    private List<Integer> asList(final int[] is) {
        final List<Integer> list = new ArrayList<>(is.length);
        for(int i = 0; i < is.length; i++)
            list.add(is[i]);
        return list;
    }

    public List<DienstAusfuehrung> getAusfuehrungenFor(ContainerAdapter.Type type, int id) throws ServiceException {
        final List<DienstAusfuehrungDto> dtos;
        final List<DienstAusfuehrung> ausfuehrungen;
        switch (type){
            case DIENST:
                final DienstDto dienst = provider.getDienst(id);
                dtos = provider.getDienstausfuehrung(asList(dienst.getDienstAusfuehrungsIds()));
                ausfuehrungen = new ArrayList<>(dtos.size());
                for(final DienstAusfuehrungDto dto : dtos) {
                    final ZeitraumDto zeitraum = provider.getZetiraum(dto.getZeitraumId());
                    final BewohnerDto bewohner = provider.getBewohner(dto.getBewohnerId());
                    ausfuehrungen.add(new DienstAusfuehrung(dto,bewohner,dienst,zeitraum));
                }
                return ausfuehrungen;
            case ZEITRAUM:
                final ZeitraumDto zeitraum = provider.getZetiraum(id);
                dtos = provider.getDienstausfuehrung(asList(zeitraum.getDienstAusfuehrungsIds()));
                ausfuehrungen = new ArrayList<>(dtos.size());
                for(final DienstAusfuehrungDto dto : dtos) {
                    final DienstDto dienstDto = provider.getDienst(dto.getDienstId());
                    final BewohnerDto bewohner = provider.getBewohner(dto.getBewohnerId());
                    ausfuehrungen.add(new DienstAusfuehrung(dto,bewohner,dienstDto,zeitraum));
                }
                return ausfuehrungen;
        }
        return Collections.emptyList();
    }
}
