package net.dankito.stadtbibliothekmuenchen.util;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import net.dankito.stadtbibliothekmuenchen.R;


/**
 * Created by ganymed on 14/11/16.
 */

public class AlertHelper {

  public static void showErrorMessageThreadSafe(final Activity activity, final String errorMessage) {
    showErrorMessageThreadSafe(activity, errorMessage, null);
  }

  public static void showErrorMessageThreadSafe(final Activity activity, final String errorMessage, final String alertTitle) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showErrorMessage(activity, errorMessage, alertTitle);
      }
    });
  }

  public static void showErrorMessage(Activity activity, String errorMessage) {
    showErrorMessage(activity, errorMessage, null);
  }

  public static void showErrorMessage(Activity activity, String errorMessage, final String alertTitle) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder = builder.setMessage(errorMessage);

    if(alertTitle != null) {
      builder = builder.setTitle(alertTitle);
    }

    builder.setNegativeButton(R.string.ok, null);

    builder.create().show();
  }

}
