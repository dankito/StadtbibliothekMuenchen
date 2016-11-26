package net.dankito.stadtbibliothekmuenchen.services;

import android.content.Context;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;

import java.util.Date;

/**
 * Created by ganymed on 26/11/16.
 */

public class ExpirationsCheckerAndNotifier {

  protected static final String CHECKING_EXPIRATIONS_INDICATOR_NOTIFICATION_TAG = "CheckingExpirations";


  protected Context context;

  protected NotificationsService notificationsService;

  protected ICronService cronService;

  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  protected UserSettings userSettings;


  public ExpirationsCheckerAndNotifier(Context context, NotificationsService notificationsService, ICronService cronService,
                                       StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient, UserSettings userSettings) {
    this.context = context;
    this.notificationsService = notificationsService;
    this.cronService = cronService;
    this.stadtbibliothekMuenchenClient = stadtbibliothekMuenchenClient;
    this.userSettings = userSettings;

    if(userSettings.isPeriodicalBorrowsExpirationCheckTimeSet()) {
      cronService.startPeriodicalJob(userSettings.getPeriodicalBorrowsExpirationCheckTime(), periodicalExpirationChecker);
    }
  }


  protected Runnable periodicalExpirationChecker = new Runnable() {
    @Override
    public void run() {
      checkForExpirations();
    }
  };

  protected void checkForExpirations() {
    if(context != null) {
      int iconId = context.getResources().getIdentifier("@android:drawable/stat_sys_phone_call_forward", null, null);
      notificationsService.showNotification("Überprüfe Leihfristen", "Bin hier hart am Arbeiten", iconId, CHECKING_EXPIRATIONS_INDICATOR_NOTIFICATION_TAG);
    }

    if(stadtbibliothekMuenchenClient != null) {
      stadtbibliothekMuenchenClient.extendAllBorrowsAndGetBorrowsStateAsync(new ExtendAllBorrowsCallback() {
        @Override
        public void completed(ExtendAllBorrowsResult result) {
          if(result.isSuccessful()) {
            retrievedExpirations(result.getBorrows().getExpirations());
          }
        }
      });
    }
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
