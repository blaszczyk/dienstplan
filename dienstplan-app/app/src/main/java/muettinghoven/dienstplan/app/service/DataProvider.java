package muettinghoven.dienstplan.app.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import muettinghoven.dienstplan.app.dto.*;
import muettinghoven.dienstplan.app.model.DienstAusfuehrung;

public class DataProvider {

    private final DienstplanProvider provider;

    public DataProvider(DienstplanProvider provider) {
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


    private List<Integer> asList(final int[] is) {
        final List<Integer> list = new ArrayList<>(is.length);
        for(int i = 0; i < is.length; i++)
            list.add(is[i]);
        return list;
    }

}
