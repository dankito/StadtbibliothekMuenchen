package net.dankito.stadtbibliothekmuenchen.util.web.callbacks;

import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;

/**
 * Created by ganymed on 25/11/16.
 */

public interface ExtendAllBorrowsCallback {

  void completed(ExtendAllBorrowsResult result);

}
