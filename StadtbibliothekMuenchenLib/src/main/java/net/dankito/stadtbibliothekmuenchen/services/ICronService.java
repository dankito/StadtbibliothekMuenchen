package net.dankito.stadtbibliothekmuenchen.services;

import java.util.Calendar;

/**
 * Created by ganymed on 26/11/16.
 */

public interface ICronService {

  int startPeriodicalJob(Calendar periodicalCheckTime, Runnable runnableToExecute);

}
