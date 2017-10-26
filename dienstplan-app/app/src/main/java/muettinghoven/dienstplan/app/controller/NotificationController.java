package muettinghoven.dienstplan.app.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.List;

import muettinghoven.dienstplan.app.model.DienstAusfuehrung;
import muettinghoven.dienstplan.app.model.DienstContainer;
import muettinghoven.dienstplan.app.service.DataCache;
import muettinghoven.dienstplan.app.service.DataProvider;
import muettinghoven.dienstplan.app.service.ServiceException;
import muettinghoven.dienstplan.app.tools.DienstTools;
import muettinghoven.dienstplan.app.tools.Preferences;
import muettinghoven.dienstplan.app.view.MainActivity;
import muettinghoven.dienstplan.app.view.R;

public class NotificationController {

    private static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;
    private static final int NOTIFICATION_ID = 196883;

    private Thread notificationThread;

    private final Context context;

    public NotificationController(final Context context) {
        this.context = context;
    }

    public void startThread() {
        notificationThread = new Thread("notification-thread") {
            @Override
            public void run() {
                NotificationController.this.run();
            }
        };
        notificationThread.start();
    }

    public void stopThread() {
        if(notificationThread != null && notificationThread.isAlive())
            notificationThread.interrupt();
    }

    private void run() {
        long sleepTime = MILLIS_PER_HOUR;
        while(true) {
            try {
                final DienstContainer bewohner = getBewohner();
                final List<DienstAusfuehrung> ausfuehrungs = DienstTools.sortedByErinnerung(bewohner);
                filterChecked(ausfuehrungs);
                sleepTime = DienstTools.nextErinnerung(ausfuehrungs) * MILLIS_PER_HOUR;
                sendNotification(ausfuehrungs);
            } catch (ServiceException e) {
                sleepTime = MILLIS_PER_HOUR;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                return;
            }
        }
    }

    private String getUndDann(final List<DienstAusfuehrung> ausfuehrungs) {
        if(ausfuehrungs.size() > 1) {
            final StringBuilder sb = new StringBuilder("dann ");
            sb.append(ausfuehrungs.get(1).getDienst());
            for (int i = 2; i < ausfuehrungs.size(); i++)
                sb.append(", ").append(ausfuehrungs.get(i).getDienst());
            return sb.toString();
        }
        else
            return "";
    }

    private void sendNotification(final List<DienstAusfuehrung> ausfuehrungs) throws ServiceException {
        if(ausfuehrungs.isEmpty())
            return;

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.icon_main);
        builder.setSubText("To Do:");
        builder.setContentTitle(ausfuehrungs.get(0).getDienst());
        builder.setContentText(getUndDann(ausfuehrungs));
        builder.setVibrate(new long[]{0,250,150,250,150,250,150,800});

        final Intent mainActivityIntent = new Intent(context, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, mainActivityIntent,0);
        builder.setContentIntent(pendingIntent);

        final Notification notification = builder.build();
        manager.notify(NOTIFICATION_ID,notification);
    }

    private void filterChecked(final List<DienstAusfuehrung> ausfuehrungs) {
        final Preferences prefs = new Preferences(context);
        prefs.loadProperties();
        for(int i = ausfuehrungs.size() - 1; i >= 0; i--)
            if(prefs.isHideErinnerung(ausfuehrungs.get(i).getId()))
                ausfuehrungs.remove(i);
    }

    private DienstContainer getBewohner() throws ServiceException {
        final Preferences prefs = new Preferences(context);
        prefs.loadProperties();
        final int bewohnerId = prefs.getBewohnerId();
        final String baseURL = prefs.getBaseURL();

        final DataCache cache = new DataCache(baseURL,context.getFilesDir());
        cache.loadFromFiles();
        final DataProvider provider = new DataProvider(cache);

        return provider.getBewohner(bewohnerId);
    }

}
