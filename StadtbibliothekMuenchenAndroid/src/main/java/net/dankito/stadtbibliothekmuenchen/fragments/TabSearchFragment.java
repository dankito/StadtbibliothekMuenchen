package net.dankito.stadtbibliothekmuenchen.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.StadtbibliothekMuenchenApplication;
import net.dankito.stadtbibliothekmuenchen.activities.MediaDetailsActivity;
import net.dankito.stadtbibliothekmuenchen.adapter.SearchResultsAdapter;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.util.AlertHelper;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.GetMediaDetailsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.SimpleSearchCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.GetMediaDetailsResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

import javax.inject.Inject;

/**
 * Created by ganymed on 25/11/16.
 */

public class TabSearchFragment extends Fragment {

  @Inject
  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  @Inject
  protected UserSettings userSettings;

  protected SearchResultsAdapter searchResultsAdapter;

  protected SearchView searchView;

  protected ObjectMapper mapper = new ObjectMapper();


  public TabSearchFragment() {
    setHasOptionsMenu(true);
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    injectComponents();

    View view = inflater.inflate(R.layout.fragment_tab_search, container, false);

    searchResultsAdapter = new SearchResultsAdapter(getActivity(), stadtbibliothekMuenchenClient, userSettings);

    ListView lstvwSearchResults = (ListView)view.findViewById(R.id.lstvwSearchResults);
    lstvwSearchResults.setOnItemClickListener(lstvwSearchResultsItemClickListener);
    lstvwSearchResults.setAdapter(searchResultsAdapter);

    FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabSearch);
    fab.setOnClickListener(floatingActionButtonSearchClickListener);

    return view;
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_tab_search_fragment, menu);

    MenuItem searchMenuItem = menu.findItem(R.id.mnitmSearch);
    searchView = (SearchView) searchMenuItem.getActionView();
    if(searchView != null) {
      SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
      searchView.setQueryHint(getActivity().getString(R.string.search));
      searchView.setOnQueryTextListener(entriesQueryTextListener);
    }

    super.onCreateOptionsMenu(menu, inflater);
  }


  protected View.OnClickListener floatingActionButtonSearchClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if(searchView != null) {
        searchView.setIconified(false);
      }
    }
  };

  protected SearchView.OnQueryTextListener entriesQueryTextListener = new SearchView.OnQueryTextListener() {
    @Override
    public boolean onQueryTextSubmit(String query) {
      searchMedia(query);
      return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
      return true;
    }
  };

  protected void searchMedia(final String query) {
    stadtbibliothekMuenchenClient.doSimpleSearchAsync(query, new SimpleSearchCallback() {
      @Override
      public void completed(SimpleSearchResponse response) {
        if(response.isSuccessful()) {
          searchResultRetrieved(response.getSearchResults());
        }
        else {
          showErrorMessageThreadSafe(response.getError(), getActivity().getString(R.string.error_title_could_not_do_simple_search, query));
        }
      }
    });
  }

  protected void searchResultRetrieved(SearchResults searchResults) {
    searchResultsAdapter.setSearchResultsThreadSafe(searchResults);
  }


  protected AdapterView.OnItemClickListener lstvwSearchResultsItemClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
      SearchResult searchResult = (SearchResult)view.getTag();
      showMediaDetails(searchResult);
    }
  };

  protected void showMediaDetails(final SearchResult searchResult) {
    if(searchResult.getDetails() != null) {
      navigateToMediaDetailsActivity(searchResult);
    }
    else {
      stadtbibliothekMuenchenClient.getMediaDetailsAsync(searchResult, new GetMediaDetailsCallback() {
        @Override
        public void completed(GetMediaDetailsResult result) {
          if(result.isSuccessful() == false) {
            showErrorMessageThreadSafe(result.getError(), "");
          }
          else {
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                navigateToMediaDetailsActivity(searchResult);
              }
            });
          }
        }
      });
    }
  }

  protected void navigateToMediaDetailsActivity(SearchResult searchResult) {
    try {
      Intent intent = new Intent(getActivity(), MediaDetailsActivity.class);

      String mediaDetailsJson = mapper.writeValueAsString(searchResult.getDetails());
      intent.putExtra(MediaDetailsActivity.MEDIA_DETAILS_KEY, mediaDetailsJson);

      startActivity(intent);
    } catch(Exception e) {
      // TODO: show error message (but should never occur)
    }
  }


  protected void showErrorMessageThreadSafe(String errorMessage, String errorMessageTitle) {
    AlertHelper.showMessageThreadSafe(getActivity(), errorMessage, errorMessageTitle);
  }


  protected void injectComponents() {
    ((StadtbibliothekMuenchenApplication) getContext().getApplicationContext()).getComponent().inject(this);
  }
}
