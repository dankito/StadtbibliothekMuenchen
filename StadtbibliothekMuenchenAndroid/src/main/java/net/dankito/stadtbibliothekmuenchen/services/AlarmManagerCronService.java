package net.dankito.stadtbibliothekmuenchen.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ganymed on 26/11/16.
 */

public class AlarmManagerCronService extends BroadcastReceiver implements ICronService {

  protected static final String CRON_JOB_TOKEN_NUMBER_EXTRA_NAME = "CronJobTokenNumber";

  private static final Logger log = LoggerFactory.getLogger(AlarmManagerCronService.class);


  protected static Map<Integer, Runnable> startedJobs = new ConcurrentHashMap<>();

  protected static int NextCronJobTokenNumber = 1;


  protected Context context;


  // needed for Entry in AndroidManifest.xml, in this way AlarmManagerCronService gets waked up by Broadcast
  public AlarmManagerCronService() {

  }

  public AlarmManagerCronService(Context context) {
    this.context = context;
  }


  public void startPeriodicalJob(Calendar periodicalCheckTime, Runnable runnableToExecute) {
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    int tokenNumber = NextCronJobTokenNumber++;

    Intent intent = new Intent(context, AlarmManagerCronService.class);
    intent.putExtra(CRON_JOB_TOKEN_NUMBER_EXTRA_NAME, tokenNumber);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tokenNumber, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    // if this time today has already passed, schedule for next day
    if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= periodicalCheckTime.get(Calendar.HOUR_OF_DAY) &&
        Calendar.getInstance().get(Calendar.MINUTE) >= periodicalCheckTime.get(Calendar.MINUTE)) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }

    calendar.set(Calendar.HOUR_OF_DAY, periodicalCheckTime.get(Calendar.HOUR_OF_DAY));
    calendar.set(Calendar.MINUTE, periodicalCheckTime.get(Calendar.MINUTE));
    calendar.set(Calendar.SECOND, 0);

    alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY, pendingIntent);

    startedJobs.put(tokenNumber, runnableToExecute);

    log.info("Started a periodical cron job with token number " + tokenNumber + " for " + calendar.getTime());
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    int tokenNumber = intent.getIntExtra(CRON_JOB_TOKEN_NUMBER_EXTRA_NAME, -1);
    log.info("Received intent for CronJob with token number " + tokenNumber);

    Runnable runnableToExecute = startedJobs.get(tokenNumber);
    if(runnableToExecute != null) {
      runnableToExecute.run();
    }
  }
}
