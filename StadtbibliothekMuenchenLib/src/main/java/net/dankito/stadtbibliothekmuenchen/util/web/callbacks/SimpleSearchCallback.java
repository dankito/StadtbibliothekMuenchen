package net.dankito.stadtbibliothekmuenchen.util.web.callbacks;

import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

/**
 * Created by ganymed on 25/11/16.
 */

public interface SimpleSearchCallback {

  void completed(SimpleSearchResponse response);

}
