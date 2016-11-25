package net.dankito.stadtbibliothekmuenchen.util.web.responses;

import net.dankito.stadtbibliothekmuenchen.model.SearchResults;

/**
 * Created by ganymed on 25/11/16.
 */

public class SimpleSearchResponse extends ResponseBase {

  protected String searchTerm;

  protected SearchResults searchResults;


  public SimpleSearchResponse(String error) {
    super(error);
  }

  public SimpleSearchResponse(String searchTerm, SearchResults searchResults) {
    super(true);
    this.searchTerm = searchTerm;
    this.searchResults = searchResults;
  }


  public String getSearchTerm() {
    return searchTerm;
  }

  public SearchResults getSearchResults() {
    return searchResults;
  }


  @Override
  public String toString() {
    if(searchResults != null) {
      return "" + searchResults;
    }
    else {
      return super.toString();
    }
  }

}
