package net.dankito.stadtbibliothekmuenchen.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import net.dankito.stadtbibliothekmuenchen.MainActivity;

/**
 * Created by ganymed on 26/11/16.
 */

public class NotificationsService {

  protected static int nextRequestCode = 1;


  protected Context context;


  public NotificationsService(Context context) {
    this.context = context;
  }


  public void showNotification(String title, String text, int iconId) {
    showNotification(title, text, iconId, null);
  }

  public void showNotification(String title, String text, int iconId, String tag) {
    NotificationManager notificationManager = getNotificationManager();

    Intent intent = new Intent(context, MainActivity.class);
    int requestCode = nextRequestCode++;

    // The stack builder object will contain an artificial back stack for the started Activity.
    // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addParentStack(MainActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(intent);

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
//    PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, 0);

    Notification notification = new NotificationCompat.Builder(context)
        .setContentTitle(title)
        .setContentText(text)
        .setSmallIcon(iconId)
        .setContentIntent(pendingIntent)
        .build();

    if(tag != null) {
      notificationManager.notify(tag, requestCode, notification);
    }
    else {
      notificationManager.notify(requestCode, notification);
    }
  }


  protected NotificationManager getNotificationManager() {
    return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

}
