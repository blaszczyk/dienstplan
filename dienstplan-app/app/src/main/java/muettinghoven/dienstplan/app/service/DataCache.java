package muettinghoven.dienstplan.app.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import bn.blaszczyk.rose.model.Identifyable;
import muettinghoven.dienstplan.app.dto.BewohnerDto;
import muettinghoven.dienstplan.app.dto.DienstAusfuehrungDto;
import muettinghoven.dienstplan.app.dto.DienstDto;
import muettinghoven.dienstplan.app.dto.DienstplanDto;
import muettinghoven.dienstplan.app.dto.ZeitraumDto;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataCache
{
    private static final Gson GSON = new Gson();

    private static final Type BEWOHNER_LIST = new TypeToken<ArrayList<BewohnerDto>>(){}.getType();
    private static final Type DIENST_LIST = new TypeToken<ArrayList<DienstDto>>(){}.getType();
    private static final Type PLAN_LIST = new TypeToken<ArrayList<DienstplanDto>>(){}.getType();
    private static final Type ZEITRAUM_LIST = new TypeToken<ArrayList<ZeitraumDto>>(){}.getType();
    private static final Type AUSFUEHRUNG_LIST = new TypeToken<ArrayList<DienstAusfuehrungDto>>(){}.getType();

    private final DienstplanService service;

    private final File baseDir;

    private final OtherService otherService;

    private final Map<Integer,BewohnerDto> bewohner = new HashMap<>();
    private final Map<Integer,DienstplanDto> dienstplaene = new HashMap<>();
    private final Map<Integer,DienstDto> dienste = new HashMap<>();
    private final Map<Integer,ZeitraumDto> zeitraeume = new HashMap<>();
    private final Map<Integer, DienstAusfuehrungDto> dienstausfuehrungen = new HashMap<>();

    public DataCache(final String baseUrl, final File baseDir)
    {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl + "/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        service = retrofit.create(DienstplanService.class);
        otherService = retrofit.create(OtherService.class);
        this.baseDir = baseDir;
    }

    public void loadDataForBewohner(final int bewohnerId) throws ServiceException {
        try {
            final BewohnerDto bewohner = service.getBewohnerById(bewohnerId).execute().body();
            store(bewohner,this.bewohner);
            final List<DienstplanDto> plaene = service.getDienstplansByIds(bewohner.getDienstplansIds()).execute().body();
            store(plaene,this.dienstplaene);
            for(final DienstplanDto plan : plaene)
            {
                final List<DienstDto> dienste = service.getDienstsByIds(plan.getDienstsIds()).execute().body();
                store(dienste,this.dienste);
                final List<BewohnerDto> mitBewohner = service.getBewohnersByIds(plan.getBewohnersIds()).execute().body();
                store(mitBewohner,this.bewohner);
                final List<ZeitraumDto> zeitraeume = service.getZeitraumsByIds(plan.getZeitraumsIds()).execute().body();
                store(zeitraeume, this.zeitraeume);
                for(final DienstDto dienst : dienste)
                {
                    final List<DienstAusfuehrungDto> ausfuehrungen = service.getDienstAusfuehrungsByIds(dienst.getDienstAusfuehrungsIds()).execute().body();
                    store(ausfuehrungen, this.dienstausfuehrungen);
                }
            }
            final List<DienstAusfuehrungDto> myDienstAusfuehrungen = service.getDienstAusfuehrungsByIds(bewohner.getDienstAusfuehrungsIds()).execute().body();
            store(myDienstAusfuehrungen, this.dienstausfuehrungen);
        }
        catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public boolean isConnected() {
        try {
            final int code = otherService.ping().execute().code();
            return code < 300;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void saveToFiles() throws ServiceException {
        saveToFile("dienste",dienste);
        saveToFile("bewohner", bewohner);
        saveToFile("plaene", dienstplaene);
        saveToFile("zeitreaume", zeitraeume);
        saveToFile("dienstausfuehrungen", dienstausfuehrungen);
    }

    private <T extends Identifyable> void saveToFile(final String fileName, final Map<Integer,T> map) throws ServiceException {
        final File file = new File(baseDir, fileName + ".json");
        try(final Writer writer = new FileWriter(file)) {
            GSON.toJson(map.values(),writer);
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    public void loadFromFiles() throws ServiceException {
        loadFromFile("dienste",dienste,DIENST_LIST);
        loadFromFile("bewohner", bewohner,BEWOHNER_LIST);
        loadFromFile("plaene", dienstplaene,PLAN_LIST);
        loadFromFile("zeitreaume", zeitraeume,ZEITRAUM_LIST);
        loadFromFile("dienstausfuehrungen", dienstausfuehrungen,AUSFUEHRUNG_LIST);
    }

    private <T extends Identifyable> void loadFromFile(final String fileName, final Map<Integer,T> map, final Type type) throws ServiceException {
        final File file = new File(baseDir, fileName + ".json");
        try(final Reader reader = new FileReader(file)) {
            final List<T> dtos = GSON.fromJson(reader, type);
            store(dtos, map);
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    public List<BewohnerDto> getBewohner(final List<Integer> ids) throws ServiceException
    {
        try {
            if (!hasAllIds(ids, bewohner)) {
                final int[] missingIds = missingIds(ids, bewohner);
                final List<BewohnerDto> missingDtos = service.getBewohnersByIds(missingIds).execute().body();
                store(missingDtos, bewohner);
            }
            return getByIds(ids, bewohner);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public BewohnerDto getBewohner(final int id) throws ServiceException
    {
        try {
            if (!hasId(id, bewohner)) {
                final BewohnerDto dto = service.getBewohnerById(id).execute().body();
                store(dto, bewohner);
            }
            return getById(id, bewohner);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public List<DienstDto> getDienst(final List<Integer> ids) throws ServiceException
    {
        try {
            if (!hasAllIds(ids, dienste)) {
                final int[] missingIds = missingIds(ids, dienste);
                final List<DienstDto> missingDtos = service.getDienstsByIds(missingIds).execute().body();
                store(missingDtos, dienste);
            }
            return getByIds(ids, dienste);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public DienstDto getDienst(final int id) throws ServiceException
    {
        try {
            if (!hasId(id, dienste)) {
                final DienstDto dto = service.getDienstById(id).execute().body();
                store(dto, dienste);
            }
            return getById(id, dienste);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public List<DienstplanDto> getDienstplan(final List<Integer> ids) throws ServiceException
    {
        try {
            if (!hasAllIds(ids, dienstplaene)) {
                final int[] missingIds = missingIds(ids, dienstplaene);
                final List<DienstplanDto> missingDtos = service.getDienstplansByIds(missingIds).execute().body();
                store(missingDtos, dienstplaene);
            }
            return getByIds(ids, dienstplaene);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public DienstplanDto getDienstplan(final int id) throws ServiceException
    {
        try {
            if (!hasId(id, dienstplaene)) {
                final DienstplanDto dto = service.getDienstplanById(id).execute().body();
                store(dto, dienstplaene);
            }
            return getById(id, dienstplaene);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public List<ZeitraumDto> getZetiraum(final List<Integer> ids) throws ServiceException
    {
        try {
            if (!hasAllIds(ids, zeitraeume)) {
                final int[] missingIds = missingIds(ids, zeitraeume);
                final List<ZeitraumDto> missingDtos = service.getZeitraumsByIds(missingIds).execute().body();
                store(missingDtos, zeitraeume);
            }
            return getByIds(ids, zeitraeume);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public ZeitraumDto getZetiraum(final int id) throws ServiceException
    {
        try {
            if (!hasId(id, zeitraeume)) {
                final ZeitraumDto dto = service.getZeitraumById(id).execute().body();
                store(dto, zeitraeume);
            }
            return getById(id, zeitraeume);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public List<DienstAusfuehrungDto> getDienstausfuehrung(final List<Integer> ids) throws ServiceException
    {
        try {
            if (!hasAllIds(ids, dienstausfuehrungen)) {
                final int[] missingIds = missingIds(ids, dienstausfuehrungen);
                final List<DienstAusfuehrungDto> missingDtos = service.getDienstAusfuehrungsByIds(missingIds).execute().body();
                store(missingDtos, dienstausfuehrungen);
            }
            return getByIds(ids, dienstausfuehrungen);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    public DienstAusfuehrungDto getDienstausfuehrung(final int id) throws ServiceException
    {
        try {
            if (!hasId(id, dienstausfuehrungen)) {
                final DienstAusfuehrungDto dto = service.getDienstAusfuehrungById(id).execute().body();
                store(dto, dienstausfuehrungen);
            }
            return getById(id, dienstausfuehrungen);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
    }

    private static <E> boolean hasAllIds(final Iterable<Integer> ids, final Map<Integer,E> map)
    {
        for(final Integer id : ids)
            if(!map.containsKey(id))
                return false;
        return true;
    }

    private static <E> boolean hasId(final int id, final Map<Integer,E> map)
    {
        return map.containsKey(id);
    }

    private static <E> int[] missingIds(final Iterable<Integer> ids, final Map<Integer,E> map)
    {
        final Set<Integer> missingIds = new TreeSet<>();
        for(final Integer id : ids)
            if(!map.containsKey(id))
                missingIds.add(id);
        final int[] missingIdsArray = new int[missingIds.size()];
        int count = 0;
        for(final Integer id : missingIds)
            missingIdsArray[count++] = id;
        return missingIdsArray;
    }

    private static <E extends Identifyable> void store(final Iterable<E> dtos, final Map<Integer,E> map)
    {
        for(E dto : dtos)
            map.put(dto.getId(),dto);
    }

    private static <E extends Identifyable> void store(final E dto, final Map<Integer,E> map)
    {
           map.put(dto.getId(),dto);
    }

    private static <E> List<E> getByIds(final List<Integer> ids, final Map<Integer,E> map)
    {
        final List<E> dtos = new ArrayList<>(ids.size());
        for(final Integer id : ids)
            dtos.add(map.get(id));
        return dtos;
    }

    private static <E> E getById(final int id, final Map<Integer,E> map)
    {
        return map.get(id);
    }

}
