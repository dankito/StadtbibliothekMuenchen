package net.dankito.stadtbibliothekmuenchen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class MediaBorrows {

  protected List<MediaBorrow> borrows = new ArrayList<>();

  protected BorrowExpirations expirations = null;


  public boolean addBorrow(MediaBorrow borrow) {
    return borrows.add(borrow);
  }

  public List<MediaBorrow> getBorrows() {
    return borrows;
  }

  public BorrowExpirations getExpirations() {
    return expirations;
  }

  public void setExpirations(BorrowExpirations expirations) {
    this.expirations = expirations;
  }


  @Override
  public String toString() {
    return borrows.size() + " borrows";
  }

}
