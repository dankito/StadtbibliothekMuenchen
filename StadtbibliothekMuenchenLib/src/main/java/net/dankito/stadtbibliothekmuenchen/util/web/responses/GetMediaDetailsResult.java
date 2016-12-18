package net.dankito.stadtbibliothekmuenchen.util.web.responses;

import net.dankito.stadtbibliothekmuenchen.model.MediaDetails;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;

/**
 * Created by ganymed on 18/12/16.
 */

public class GetMediaDetailsResult extends ResponseBase {

  protected SearchResult searchResult;

  protected MediaDetails details;


  public GetMediaDetailsResult(String error) {
    super(error);
  }

  public GetMediaDetailsResult(SearchResult searchResult, MediaDetails details) {
    super(details != null);
    this.searchResult = searchResult;
    this.details = details;
  }


  public SearchResult getSearchResult() {
    return searchResult;
  }

  public MediaDetails getDetails() {
    return details;
  }


  @Override
  public String toString() {
    if(getDetails() != null) {
      return getDetails().toString();
    }

    return super.toString();
  }

}
