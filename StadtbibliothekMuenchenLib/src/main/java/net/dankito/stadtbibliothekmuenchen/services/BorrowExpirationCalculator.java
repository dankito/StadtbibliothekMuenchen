package net.dankito.stadtbibliothekmuenchen.services;

import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ganymed on 25/11/16.
 */

public class BorrowExpirationCalculator {

  protected static final DateFormat CALCULATE_TODAY_AT_MIDNIGHT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


  public BorrowExpirations calculateExpirations(MediaBorrows borrows, UserSettings userSettings) {
    BorrowExpirations expirations = new BorrowExpirations();
    Date today = getTodayAtMidnightDate();

    calculateAlreadyExpiredBorrows(today, expirations, borrows);

    if(userSettings.isThirdWarningEnabled()) {
      calculateExpirationsForThirdWarning(today, expirations, borrows, userSettings);
    }

    if(userSettings.isSecondWarningEnabled()) {
      calculateExpirationsForSecondWarning(today, expirations, borrows, userSettings);
    }

    if(userSettings.isFirstWarningEnabled()) {
      calculateExpirationsForFirstWarning(today, expirations, borrows, userSettings);
    }

    return expirations;
  }

  protected void calculateAlreadyExpiredBorrows(Date today, BorrowExpirations expirations, MediaBorrows borrows) {
    for(MediaBorrow borrow : borrows.getBorrows()) {
      if(isLessThanTheseDaysFromNow(today, borrow.getDueOn(), 0)) {
        expirations.addAlreadyExpiredBorrow(borrow);
      }
    }
  }

  protected void calculateExpirationsForThirdWarning(Date today, BorrowExpirations expirations, MediaBorrows borrows, UserSettings userSettings) {
    for(MediaBorrow borrow : borrows.getBorrows()) {
      if(isLessThanTheseDaysFromNow(today, borrow.getDueOn(), userSettings.getCountDaysBeforeExpirationForThirdWarning())) {
        if(expirations.getAlreadyExpiredBorrows().contains(borrow) == false) {
          expirations.addBorrowExpirationForThirdWarning(borrow);
        }
      }
    }
  }

  protected void calculateExpirationsForSecondWarning(Date today, BorrowExpirations expirations, MediaBorrows borrows, UserSettings userSettings) {
    for(MediaBorrow borrow : borrows.getBorrows()) {
      if(isLessThanTheseDaysFromNow(today, borrow.getDueOn(), userSettings.getCountDaysBeforeExpirationForSecondWarning())) {
        if(expirations.getAlreadyExpiredBorrows().contains(borrow) == false && expirations.getBorrowExpirationsForThirdWarning().contains(borrow) == false) {
          expirations.addBorrowExpirationForSecondWarning(borrow);
        }
      }
    }
  }

  protected void calculateExpirationsForFirstWarning(Date today, BorrowExpirations expirations, MediaBorrows borrows, UserSettings userSettings) {
    for(MediaBorrow borrow : borrows.getBorrows()) {
      if(isLessThanTheseDaysFromNow(today, borrow.getDueOn(), userSettings.getCountDaysBeforeExpirationForFirstWarning())) {
        if(expirations.getAlreadyExpiredBorrows().contains(borrow) == false && expirations.getBorrowExpirationsForThirdWarning().contains(borrow) == false
            && expirations.getBorrowExpirationsForSecondWarning().contains(borrow) == false) {
          expirations.addBorrowExpirationForFirstWarning(borrow);
        }
      }
    }
  }

  protected boolean isLessThanTheseDaysFromNow(Date today, Date dueOn, int countDaysBeforeExpiration) {
    Date dateToGenerateWarningAt = new Date(today.getTime() + countDaysBeforeExpiration * 24 * 60 * 60 * 1000);

    return dueOn.getTime() - dateToGenerateWarningAt.getTime() <= 0;
  }

  protected Date getTodayAtMidnightDate() {
    try {
      String todayDateString = CALCULATE_TODAY_AT_MIDNIGHT_DATE_FORMAT.format(new Date());

      return CALCULATE_TODAY_AT_MIDNIGHT_DATE_FORMAT.parse(todayDateString); // now time is cut off
    } catch(Exception ignored) { } // should never come to this

    return new Date();
  }
}
