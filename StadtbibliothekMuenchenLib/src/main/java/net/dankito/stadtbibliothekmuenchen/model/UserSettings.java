package net.dankito.stadtbibliothekmuenchen.model;

import net.dankito.stadtbibliothekmuenchen.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class UserSettings {

  protected String identityCardNumber;

  protected String password;

  protected List<Library> favoriteLibraries = new ArrayList<>();

  protected boolean isShowSystemNotificationsOnExpirationEnabled = true;

  protected boolean isFirstWarningEnabled = false;

  protected int countDaysBeforeExpirationForeFirstWarning = 7;

  protected boolean isSecondWarningEnabled = false;

  protected int countDaysBeforeExpirationForSecondWarning = 3;

  protected boolean isThirdWarningEnabled = true;

  protected int countDaysBeforeExpirationForThirdWarning = 1;

  protected Calendar periodicalBorrowsExpirationCheckTime = null;


  public UserSettings() {

  }


  public boolean isIdentityCardNumberSet() {
    return StringUtils.isNotNullOrEmpty(getIdentityCardNumber());
  }

  public String getIdentityCardNumber() {
    return identityCardNumber;
  }

  public void setIdentityCardNumber(String identityCardNumber) {
    this.identityCardNumber = identityCardNumber;
  }

  public boolean isPasswordSet() {
    return StringUtils.isNotNullOrEmpty(getPassword());
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean areFavoriteLibrariesSet() {
    return favoriteLibraries.size() > 0;
  }

  public List<Library> getFavoriteLibraries() {
    return new ArrayList<>(favoriteLibraries);
  }

  public void addFavoriteLibrary(Library library) {
    favoriteLibraries.add(library);
  }

  public void removeFavoriteLibrary(Library library) {
    favoriteLibraries.remove(library);
  }

  public boolean isShowSystemNotificationsOnExpirationEnabled() {
    return isShowSystemNotificationsOnExpirationEnabled;
  }

  public void setShowSystemNotificationsOnExpirationEnabled(boolean showSystemNotificationsOnExpirationEnabled) {
    isShowSystemNotificationsOnExpirationEnabled = showSystemNotificationsOnExpirationEnabled;
  }

  public boolean isFirstWarningEnabled() {
    return isFirstWarningEnabled;
  }

  public void setFirstWarningEnabled(boolean firstWarningEnabled) {
    isFirstWarningEnabled = firstWarningEnabled;
  }

  public int getCountDaysBeforeExpirationForFirstWarning() {
    return countDaysBeforeExpirationForeFirstWarning;
  }

  public void setCountDaysBeforeExpirationForFirstWarning(int countDaysBeforeExpirationForeFirstWarning) {
    this.countDaysBeforeExpirationForeFirstWarning = countDaysBeforeExpirationForeFirstWarning;
  }

  public boolean isSecondWarningEnabled() {
    return isSecondWarningEnabled;
  }

  public void setSecondWarningEnabled(boolean secondWarningEnabled) {
    isSecondWarningEnabled = secondWarningEnabled;
  }

  public int getCountDaysBeforeExpirationForSecondWarning() {
    return countDaysBeforeExpirationForSecondWarning;
  }

  public void setCountDaysBeforeExpirationForSecondWarning(int countDaysBeforeExpirationForSecondWarning) {
    this.countDaysBeforeExpirationForSecondWarning = countDaysBeforeExpirationForSecondWarning;
  }

  public boolean isThirdWarningEnabled() {
    return isThirdWarningEnabled;
  }

  public void setThirdWarningEnabled(boolean thirdWarningEnabled) {
    isThirdWarningEnabled = thirdWarningEnabled;
  }

  public int getCountDaysBeforeExpirationForThirdWarning() {
    return countDaysBeforeExpirationForThirdWarning;
  }

  public void setCountDaysBeforeExpirationForThirdWarning(int countDaysBeforeExpirationForThirdWarning) {
    this.countDaysBeforeExpirationForThirdWarning = countDaysBeforeExpirationForThirdWarning;
  }

  public boolean isPeriodicalBorrowsExpirationCheckTimeSet() {
    return getPeriodicalBorrowsExpirationCheckTime() != null;
  }

  public Calendar getPeriodicalBorrowsExpirationCheckTime() {
    return periodicalBorrowsExpirationCheckTime;
  }

  public void setPeriodicalBorrowsExpirationCheckTime(Calendar periodicalBorrowsExpirationCheckTime) {
    this.periodicalBorrowsExpirationCheckTime = periodicalBorrowsExpirationCheckTime;
  }

}
