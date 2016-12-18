package net.dankito.stadtbibliothekmuenchen.util.web.callbacks;

import net.dankito.stadtbibliothekmuenchen.util.web.responses.GetMediaDetailsResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ResponseBase;

/**
 * Created by ganymed on 18/12/16.
 */

public interface GetMediaDetailsCallback {

  void completed(GetMediaDetailsResult result);

}
