package net.dankito.stadtbibliothekmuenchen.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ganymed on 18/12/16.
 */

public class MediaDetails {

  protected String title;

  protected String year;

  protected String mediaTypeIconUrl;

  protected String availabilityIconUrl;

  protected Set<MediaCopy> copies = new HashSet<>();


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

  public void addCopy(MediaCopy copy) {
    copies.add(copy);
  }

  public List<MediaCopy> getCopies() {
    return new ArrayList<>(copies);
  }


  @Override
  public String toString() {
    return getTitle();
  }

}
