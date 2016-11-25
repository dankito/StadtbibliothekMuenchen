package net.dankito.stadtbibliothekmuenchen.fragments;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.ListView;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.adapter.SearchResultsAdapter;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.SimpleSearchCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

import javax.inject.Inject;

/**
 * Created by ganymed on 25/11/16.
 */

public class TabSearchFragment extends Fragment {

  @Inject
  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  protected SearchResultsAdapter searchResultsAdapter;

  protected SearchView searchView;


  public TabSearchFragment() {
    setHasOptionsMenu(true);
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    injectComponents();

    View view = inflater.inflate(R.layout.fragment_tab_search, container, false);

    searchResultsAdapter = new SearchResultsAdapter(getActivity());

    ListView lstvwSearchResults = (ListView)view.findViewById(R.id.lstvwSearchResults);
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

  protected void searchMedia(String query) {
    stadtbibliothekMuenchenClient.doSimpleSearchAsync(query, new SimpleSearchCallback() {
      @Override
      public void completed(SimpleSearchResponse response) {
        if(response.isSuccessful()) {
          searchResultRetrieved(response.getSearchResults());
        }
        else {

        }
      }
    });
  }

  protected void searchResultRetrieved(SearchResults searchResults) {
    searchResultsAdapter.setSearchResultsThreadSafe(searchResults);
  }


  protected void injectComponents() {
    ((MainActivity) getActivity()).getComponent().inject(this);
  }
}
