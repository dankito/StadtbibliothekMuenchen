package net.dankito.stadtbibliothekmuenchen.model;

/**
 * Created by ganymed on 18/12/16.
 */

public class MediaDetails {

  protected String title;

  protected String year;

  protected String mediaTypeIconUrl;

  protected String availabilityIconUrl;


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getMediaTypeIconUrl() {
    return mediaTypeIconUrl;
  }

  public void setMediaTypeIconUrl(String mediaTypeIconUrl) {
    this.mediaTypeIconUrl = mediaTypeIconUrl;
  }

  public String getAvailabilityIconUrl() {
    return availabilityIconUrl;
  }

  public void setAvailabilityIconUrl(String availabilityIconUrl) {
    this.availabilityIconUrl = availabilityIconUrl;
  }


  @Override
  public String toString() {
    return getTitle();
  }

}
