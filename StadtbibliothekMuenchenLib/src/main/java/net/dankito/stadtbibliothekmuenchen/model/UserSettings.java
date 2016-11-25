package net.dankito.stadtbibliothekmuenchen.model;

/**
 * Created by ganymed on 25/11/16.
 */

public class UserSettings {

  protected String identityCardNumber;

  protected String password;

  protected boolean isFirstWarningEnabled = false;

  protected int countDaysBeforeExpirationForeFirstWarning = 7;

  protected boolean isSecondWarningEnabled = false;

  protected int countDaysBeforeExpirationForSecondWarning = 3;

  protected boolean isThirdWarningEnabled = true;

  protected int countDaysBeforeExpirationForThirdWarning = 1;


  public UserSettings() {

  }


  public String getIdentityCardNumber() {
    return identityCardNumber;
  }

  public void setIdentityCardNumber(String identityCardNumber) {
    this.identityCardNumber = identityCardNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

}
