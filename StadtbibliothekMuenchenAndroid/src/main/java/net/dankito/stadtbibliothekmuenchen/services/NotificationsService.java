package net.dankito.stadtbibliothekmuenchen.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;

import java.util.Date;

/**
 * Created by ganymed on 26/11/16.
 */

public class NotificationsService {

  protected static int nextRequestCode = 1;


  protected Context context;


  public NotificationsService(Context context) {
    this.context = context;
  }


  public void showSystemNotificationForBorrowExpirations(BorrowExpirations expirations) {
    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    for(MediaBorrow expiredBorrow : expirations.getAlreadyExpiredBorrows()) {
      showAlreadyExpiredNotification(expiredBorrow, notificationManager);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForThirdWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow, notificationManager);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForSecondWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow, notificationManager);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForFirstWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow, notificationManager);
    }
  }

  protected void showAlreadyExpiredNotification(MediaBorrow expiredBorrow, NotificationManager notificationManager) {
    String title = context.getResources().getString(R.string.notification_borrow_has_expired);
    String text = expiredBorrow.getTitle();

    int iconId = context.getResources().getIdentifier("@android:drawable/stat_notify_error", null, null);

    showNotification(expiredBorrow, title, text, iconId, notificationManager);
  }

  protected void showIsGoingToExpireSoonNotification(MediaBorrow soonExpiringBorrow, NotificationManager notificationManager) {
    String title = context.getResources().getString(R.string.notification_is_going_to_expire_soon, calculateInHowManyDaysBorrowExpires(soonExpiringBorrow));
    String text = soonExpiringBorrow.getTitle();

    int iconId = context.getResources().getIdentifier("@android:drawable/stat_sys_warning", null, null);

    showNotification(soonExpiringBorrow, title, text, iconId, notificationManager);
  }

  protected int calculateInHowManyDaysBorrowExpires(MediaBorrow borrow) {
    Date today = new Date();
    long timeTillExpiration = borrow.getDueOn().getTime() - today.getTime();

    return (int)(timeTillExpiration / (24 * 60 * 60 * 1000)) + 1;
  }

  protected void showNotification(MediaBorrow borrow, String title, String text, int iconId, NotificationManager notificationManager) {
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

    notificationManager.notify(borrow.getMediaNumber(), requestCode, notification);
  }
}
