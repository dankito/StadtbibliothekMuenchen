package net.dankito.stadtbibliothekmuenchen.services;

import android.content.Context;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;

import java.util.Date;

/**
 * Created by ganymed on 26/11/16.
 */

public class ExpirationsCheckerAndNotifier {

  protected Context context;

  protected NotificationsService notificationsService;


  public ExpirationsCheckerAndNotifier(Context context, NotificationsService notificationsService) {
    this.context = context;
    this.notificationsService = notificationsService;
  }


  public void retrievedExpirations(BorrowExpirations expirations) {
    for(MediaBorrow expiredBorrow : expirations.getAlreadyExpiredBorrows()) {
      showAlreadyExpiredNotification(expiredBorrow);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForThirdWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForSecondWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow);
    }

    for(MediaBorrow soonExpiringBorrow : expirations.getBorrowExpirationsForFirstWarning()) {
      showIsGoingToExpireSoonNotification(soonExpiringBorrow);
    }
  }

  protected void showAlreadyExpiredNotification(MediaBorrow expiredBorrow) {
    String title = context.getResources().getString(R.string.notification_borrow_has_expired);
    String text = expiredBorrow.getTitle();

    int iconId = context.getResources().getIdentifier("@android:drawable/stat_notify_error", null, null);

    showNotification(expiredBorrow, title, text, iconId);
  }

  protected void showIsGoingToExpireSoonNotification(MediaBorrow soonExpiringBorrow) {
    String title = context.getResources().getString(R.string.notification_is_going_to_expire_soon, calculateInHowManyDaysBorrowExpires(soonExpiringBorrow));
    String text = soonExpiringBorrow.getTitle();

    int iconId = context.getResources().getIdentifier("@android:drawable/stat_sys_warning", null, null);

    showNotification(soonExpiringBorrow, title, text, iconId);
  }

  protected int calculateInHowManyDaysBorrowExpires(MediaBorrow borrow) {
    Date today = new Date();
    long timeTillExpiration = borrow.getDueOn().getTime() - today.getTime();

    return (int)(timeTillExpiration / (24 * 60 * 60 * 1000)) + 1;
  }


  protected void showNotification(MediaBorrow borrow, String title, String text, int iconId) {
    notificationsService.showNotification(title, text, iconId, borrow.getMediaNumber());
  }

}
