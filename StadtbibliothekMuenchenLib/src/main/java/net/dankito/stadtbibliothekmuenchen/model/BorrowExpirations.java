package net.dankito.stadtbibliothekmuenchen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class BorrowExpirations {

  protected List<MediaBorrow> borrowAlreadyExpired = new ArrayList<>();

  protected List<MediaBorrow> borrowExpirationsForFirstWarning = new ArrayList<>();

  protected List<MediaBorrow> borrowExpirationsForSecondWarning = new ArrayList<>();

  protected List<MediaBorrow> borrowExpirationsForThirdWarning = new ArrayList<>();


  public BorrowExpirations() {

  }


  public boolean hasAlreadyExpiredBorrows() {
    return borrowAlreadyExpired.size() > 0;
  }

  public boolean addAlreadyExpiredBorrow(MediaBorrow borrow) {
    return borrowAlreadyExpired.add(borrow);
  }

  public List<MediaBorrow> getAlreadyExpiredBorrows() {
    return borrowAlreadyExpired;
  }


  public boolean hasBorrowExpirationForFirstWarning() {
    return borrowExpirationsForFirstWarning.size() > 0;
  }

  public boolean addBorrowExpirationForFirstWarning(MediaBorrow borrow) {
    return borrowExpirationsForFirstWarning.add(borrow);
  }

  public List<MediaBorrow> getBorrowExpirationsForFirstWarning() {
    return borrowExpirationsForFirstWarning;
  }


  public boolean hasBorrowExpirationForSecondWarning() {
    return borrowExpirationsForSecondWarning.size() > 0;
  }

  public boolean addBorrowExpirationForSecondWarning(MediaBorrow borrow) {
    return borrowExpirationsForSecondWarning.add(borrow);
  }

  public List<MediaBorrow> getBorrowExpirationsForSecondWarning() {
    return borrowExpirationsForSecondWarning;
  }


  public boolean hasBorrowExpirationForThirdWarning() {
    return borrowExpirationsForThirdWarning.size() > 0;
  }

  public boolean addBorrowExpirationForThirdWarning(MediaBorrow borrow) {
    return borrowExpirationsForThirdWarning.add(borrow);
  }

  public List<MediaBorrow> getBorrowExpirationsForThirdWarning() {
    return borrowExpirationsForThirdWarning;
  }

}
