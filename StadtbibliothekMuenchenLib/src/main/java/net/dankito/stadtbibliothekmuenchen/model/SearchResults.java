package net.dankito.stadtbibliothekmuenchen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class SearchResults {

  protected List<SearchResult> searchResults = new ArrayList<>();


  public SearchResults() {

  }


  public boolean addSearchResult(SearchResult searchResult) {
    return this.searchResults.add(searchResult);
  }

  public List<SearchResult> getResults() {
    return searchResults;
  }


  @Override
  public String toString() {
    return this.searchResults.size() + " search results";
  }

}
