package muettinghoven.dienstplan.app.tools;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class Preferences {

    final File propertiesFile;

    public Preferences(final Context context) {
        this.propertiesFile = new File(context.getFilesDir(),PROPERTIES_FILE_NAME);
    }

    private int bewohnerId;
    private String baseURL;
    private Set<Integer> hideErinnerungen;

    private static final String KEY_SERVICE_URL = "muettinghoven.dienstplan.url";
    private static final String KEY_BEWOHNER_ID = "muettinghoven.dienstplan.bewodner.id";
    private static final String KEY_HIDE_ERINNERUNGEN = "muettinghoven.dienstplan.erinnerungen.hide";
    private static final String PROPERTIES_FILE_NAME = "main-properties.xml";

    public void saveProperties() {
        final Properties properties = new Properties();

        properties.put(KEY_SERVICE_URL,baseURL);
        properties.put(KEY_BEWOHNER_ID,Integer.toString(bewohnerId));

        final StringBuilder sb = new StringBuilder();
        for(final Integer i : hideErinnerungen)
            sb.append(i).append(" ");
        properties.put(KEY_HIDE_ERINNERUNGEN,sb.toString().trim());

        try {
            properties.storeToXML(new FileOutputStream(propertiesFile),"generated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProperties() {
        final Properties properties = new Properties();
        try {
            properties.loadFromXML(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bewohnerId = Integer.parseInt(properties.getProperty(KEY_BEWOHNER_ID));
        baseURL = properties.getProperty(KEY_SERVICE_URL);

        hideErinnerungen = new TreeSet<>();
        final String hideIdsString = properties.getProperty(KEY_HIDE_ERINNERUNGEN);
        if(hideIdsString != null && hideIdsString.length() > 0) {
            final String[] split = hideIdsString.split("\\s");
            for (final String id : split)
                hideErinnerungen.add(Integer.parseInt(id));
        }
    }

    public int getBewohnerId() {
        return bewohnerId;
    }

    public void setBewohnerId(final int bewohnerId) {
        this.bewohnerId = bewohnerId;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(final String baseURL) {
        this.baseURL = baseURL;
    }

    public boolean isHideErinnerung(final int ausfuhrungId) {
        return hideErinnerungen.contains(ausfuhrungId);
    }

    public void setHideErinnerung(final int ausfuehrungId, final boolean hide) {
        if(hide)
            hideErinnerungen.remove(ausfuehrungId);
        else
            hideErinnerungen.add(ausfuehrungId);
    }

    public void toggleErinnerung(int ausfuhrungId) {
        if(hideErinnerungen.contains(ausfuhrungId))
            hideErinnerungen.remove(ausfuhrungId);
        else
            hideErinnerungen.add(ausfuhrungId);
    }
}
