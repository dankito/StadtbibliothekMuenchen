package net.dankito.stadtbibliothekmuenchen.util.web.responses;

/**
 * Created by ganymed on 24/11/16.
 */

public class LoginResult extends ResponseBase {

  public LoginResult(String error) {
    super(error);
  }

  public LoginResult(boolean isSuccessful) {
    super(isSuccessful);
  }

}
