package net.dankito.stadtbibliothekmuenchen.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.StadtbibliothekMuenchenApplication;
import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by ganymed on 26/11/16.
 */

public class ExpirationsCheckerAndNotifier extends BroadcastReceiver {

  protected static final String CHECKING_EXPIRATIONS_INDICATOR_NOTIFICATION_TAG = "CheckingExpirations";

  protected static final String CHECKING_EXPIRATIONS_FAILED_NOTIFICATION_TAG = "CheckingExpirationsFailed";


  protected Context context;

  @Inject
  protected NotificationsService notificationsService;

  @Inject
  protected ICronService cronService;

  @Inject
  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  @Inject
  protected UserSettings userSettings;


  public ExpirationsCheckerAndNotifier() {

  }

  public ExpirationsCheckerAndNotifier(Context context) {
    this.context = context;

    injectComponents(context);

    mayStartPeriodicalBorrowsExpirationCheck();
  }

  protected void injectComponents(Context context) {
    ((StadtbibliothekMuenchenApplication)context.getApplicationContext()).getComponent().inject(this);
  }

  protected void mayStartPeriodicalBorrowsExpirationCheck() {
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
          checkingBorrowsStateCompleted(result);
        }
      });
    }
  }

  protected void checkingBorrowsStateCompleted(ExtendAllBorrowsResult result) {
    notificationsService.dismissNotification(CHECKING_EXPIRATIONS_INDICATOR_NOTIFICATION_TAG);

    if(result.isSuccessful()) {
      retrievedExpirations(result.getBorrows().getExpirations());
    }
    else {
      String title = context.getResources().getString(R.string.notification_could_not_check_expirations);
      String text = result.getError();
      int iconId = getErrorIcon();
      notificationsService.showNotification(title, text, iconId, CHECKING_EXPIRATIONS_FAILED_NOTIFICATION_TAG);
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

    int iconId = getErrorIcon();

    showNotification(expiredBorrow, title, text, iconId);
  }

  protected void showIsGoingToExpireSoonNotification(MediaBorrow soonExpiringBorrow) {
    String title = context.getResources().getString(R.string.notification_is_going_to_expire_soon, calculateInHowManyDaysBorrowExpires(soonExpiringBorrow));
    String text = soonExpiringBorrow.getTitle();

    int iconId = getWarningIcon();

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


  protected int getErrorIcon() {
    return context.getResources().getIdentifier("@android:drawable/stat_notify_error", null, null);
  }

  protected int getWarningIcon() {
    return context.getResources().getIdentifier("@android:drawable/stat_sys_warning", null, null);
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    this.context = context;
    injectComponents(context);

    if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) { // Android system has booted
      systemHasBooted();
    }
  }

  protected void systemHasBooted() {
    try {
      checkForExpirations();

      mayStartPeriodicalBorrowsExpirationCheck();
    } catch(Exception e) {

    }
  }

}
