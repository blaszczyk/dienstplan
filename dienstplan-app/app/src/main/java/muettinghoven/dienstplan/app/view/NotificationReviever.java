package muettinghoven.dienstplan.app.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import muettinghoven.dienstplan.app.controller.NotificationController;

public class NotificationReviever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final NotificationController controller = new NotificationController(context);
        controller.startThread();
    }
}
