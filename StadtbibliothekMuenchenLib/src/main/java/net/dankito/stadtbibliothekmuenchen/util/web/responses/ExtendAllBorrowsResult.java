package net.dankito.stadtbibliothekmuenchen.util.web.responses;

import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;

/**
 * Created by ganymed on 25/11/16.
 */

public class ExtendAllBorrowsResult extends ResponseBase {

  protected MediaBorrows borrows = null;


  public ExtendAllBorrowsResult(String error) {
    super(error);
  }

  public ExtendAllBorrowsResult(MediaBorrows borrows) {
    super(true);
    this.borrows = borrows;
  }


  public MediaBorrows getBorrows() {
    return borrows;
  }


  @Override
  public String toString() {
    if(isSuccessful()) {
      return "Currently has " + borrows.getBorrows().size() + " borrows";
    }

    return super.toString();
  }

}
