package net.dankito.stadtbibliothekmuenchen.util.web.callbacks;

import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;

/**
 * Created by ganymed on 24/11/16.
 */

public interface LoginCallback {

  void completed(LoginResult result);

}
