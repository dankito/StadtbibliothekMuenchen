package net.dankito.stadtbibliothekmuenchen.model;

import org.jsoup.nodes.Document;

/**
 * Created by ganymed on 25/11/16.
 */

public class SearchResult {

  protected String mediaInfo;

  protected String mediaDetailsToken;

  protected Document searchResultsDocument;

  protected String year;

  protected String mediaTypeIconUrl;

  protected String availabilityIconUrl;

  protected MediaDetails details;


  public SearchResult() {

  }


  public String getMediaInfo() {
    return mediaInfo;
  }

  public void setMediaInfo(String mediaInfo) {
    this.mediaInfo = mediaInfo;
  }

  public String getMediaDetailsToken() {
    return mediaDetailsToken;
  }

  public void setMediaDetailsToken(String mediaDetailsToken) {
    this.mediaDetailsToken = mediaDetailsToken;
  }

  public Document getSearchResultsDocument() {
    return searchResultsDocument;
  }

  public void setSearchResultsDocument(Document searchResultsDocument) {
    this.searchResultsDocument = searchResultsDocument;
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

  public MediaDetails getDetails() {
    return details;
  }

  public void setDetails(MediaDetails details) {
    this.details = details;
  }


  @Override
  public String toString() {
    return getMediaInfo();
  }

}
