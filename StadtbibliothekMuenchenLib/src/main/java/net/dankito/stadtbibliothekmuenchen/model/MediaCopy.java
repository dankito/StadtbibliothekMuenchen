package net.dankito.stadtbibliothekmuenchen.model;

/**
 * Created by ganymed on 18/12/16.
 */

public class MediaCopy {

  protected Library library;

  protected String location;

  protected String shelfmark; // or signatory ?

  protected String lendingPeriod;

  protected String availability;


  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getShelfmark() {
    return shelfmark;
  }

  public void setShelfmark(String shelfmark) {
    this.shelfmark = shelfmark;
  }

  public String getLendingPeriod() {
    return lendingPeriod;
  }

  public void setLendingPeriod(String lendingPeriod) {
    this.lendingPeriod = lendingPeriod;
  }

  public boolean isAvailable() {
    return getAvailability() != null && "verfügbar".equals(getAvailability().toLowerCase());
  }

  public String getAvailability() {
    return availability;
  }

  public void setAvailability(String availability) {
    this.availability = availability;
  }


  @Override
  public String toString() {
    return getLibrary() + ": " + getAvailability();
  }

}
