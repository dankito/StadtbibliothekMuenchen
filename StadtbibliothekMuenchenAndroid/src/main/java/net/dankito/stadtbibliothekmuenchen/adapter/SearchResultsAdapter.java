package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;

/**
 * Created by ganymed on 25/11/16.
 */

public class SearchResultsAdapter extends BaseAdapter {

  protected Activity activity;

  protected SearchResults searchResults;


  public SearchResultsAdapter(Activity activity) {
    this.activity = activity;
  }


  public void setSearchResultsThreadSafe(final SearchResults searchResults) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        setSearchResults(searchResults);
      }
    });
  }

  public void setSearchResults(SearchResults searchResults) {
    this.searchResults = searchResults;

    notifyDataSetChanged();
  }


  @Override
  public int getCount() {
    if(searchResults != null) {
      return searchResults.getResults().size();
    }

    return 0;
  }

  @Override
  public Object getItem(int index) {
    return searchResults.getResults().get(index);
  }

  @Override
  public long getItemId(int index) {
    return index;
  }

  @Override
  public View getView(int index, View convertView, ViewGroup parent) {
    if(convertView == null) {
      convertView = activity.getLayoutInflater().inflate(R.layout.list_item_search_results, parent, false);
    }

    SearchResult searchResult = (SearchResult) getItem(index);

    return convertView;
  }

}
