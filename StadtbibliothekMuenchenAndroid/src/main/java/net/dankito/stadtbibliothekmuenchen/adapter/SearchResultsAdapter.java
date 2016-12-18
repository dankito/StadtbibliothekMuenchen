package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.Library;
import net.dankito.stadtbibliothekmuenchen.model.MediaCopy;
import net.dankito.stadtbibliothekmuenchen.model.MediaDetails;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.GetMediaDetailsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.GetMediaDetailsResult;

import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class SearchResultsAdapter extends BaseAdapter {

  protected Activity activity;

  protected SearchResults searchResults;

  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  protected UserSettings userSettings;


  public SearchResultsAdapter(Activity activity, StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient, UserSettings userSettings) {
    this.activity = activity;
    this.stadtbibliothekMuenchenClient = stadtbibliothekMuenchenClient;
    this.userSettings = userSettings;
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
    convertView.setTag(searchResult);

    TextView txtvwSearchResultMediaInfo = (TextView)convertView.findViewById(R.id.txtvwSearchResultMediaInfo);
    txtvwSearchResultMediaInfo.setText(searchResult.getMediaInfo());

    TextView txtvwYear = (TextView)convertView.findViewById(R.id.txtvwYear);
    txtvwYear.setText(searchResult.getYear());

    ImageView imgvwMediaTypeIcon = (ImageView)convertView.findViewById(R.id.imgvwMediaTypeIcon);
    Picasso.with(activity)
        .load(searchResult.getMediaTypeIconUrl())
        .into(imgvwMediaTypeIcon);

    ImageView imgvwAvailabilityIcon = (ImageView)convertView.findViewById(R.id.imgvwAvailabilityIcon);
    Picasso.with(activity)
        .load(searchResult.getAvailabilityIconUrl())
        .into(imgvwAvailabilityIcon);

    adjustViewToUserSettings(searchResult, convertView);

    return convertView;
  }

  protected void adjustViewToUserSettings(SearchResult searchResult, View convertView) {
    ImageView imgvwAvailableInFavoriteLibraryIcon = (ImageView)convertView.findViewById(R.id.imgvwAvailableInFavoriteLibraryIcon);

    if(userSettings.areFavoriteLibrariesSet() == false) {
      imgvwAvailableInFavoriteLibraryIcon.setVisibility(View.INVISIBLE);
    }
    else {
      if(searchResult.getDetails() != null) {
        setMediaAvailableInFavoriteLibraryIcon(searchResult.getDetails(), imgvwAvailableInFavoriteLibraryIcon);
      }
      else {
        imgvwAvailableInFavoriteLibraryIcon.setVisibility(View.INVISIBLE);

        if(searchResult.isAvailable()) {
          getMediaDetails(searchResult, convertView);
        }
      }
    }
  }

  protected void setMediaAvailableInFavoriteLibraryIcon(MediaDetails details, ImageView imgvwAvailableInFavoriteLibraryIcon) {
    imgvwAvailableInFavoriteLibraryIcon.setVisibility(View.VISIBLE);
    List<Library> favoriteLibraries = userSettings.getFavoriteLibraries();

    for(MediaCopy copy : details.getCopies()) {
      if(favoriteLibraries.contains(copy.getLibrary())) {
        if(copy.isAvailable()) {
          imgvwAvailableInFavoriteLibraryIcon.setImageResource(android.R.drawable.checkbox_on_background);
          return;
        }
      }
    }

    imgvwAvailableInFavoriteLibraryIcon.setImageResource(android.R.drawable.ic_delete);
  }

  protected void getMediaDetails(SearchResult searchResult, final View convertView) {
    stadtbibliothekMuenchenClient.getMediaDetailsAsync(searchResult, new GetMediaDetailsCallback() {
      @Override
      public void completed(GetMediaDetailsResult result) {
        if(result.isSuccessful()) {
          mediaDetailsRetrieved(result, convertView);
        }
      }
    });
  }

  protected void mediaDetailsRetrieved(final GetMediaDetailsResult result, final View convertView) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        SearchResult searchResult = result.getSearchResult();

        if(searchResult == convertView.getTag()) {
          adjustViewToUserSettings(searchResult, convertView);
        }
      }
    });
  }

}
