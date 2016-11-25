package net.dankito.stadtbibliothekmuenchen.model;

/**
 * Created by ganymed on 25/11/16.
 */

public class UserSettings {

  protected String identityCardNumber;

  protected String password;


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

}
