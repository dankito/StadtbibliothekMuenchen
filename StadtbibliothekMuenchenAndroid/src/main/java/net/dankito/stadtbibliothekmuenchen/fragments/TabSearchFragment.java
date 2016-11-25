package net.dankito.stadtbibliothekmuenchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.adapter.SearchResultsAdapter;

/**
 * Created by ganymed on 25/11/16.
 */

public class TabSearchFragment extends Fragment {

  protected SearchResultsAdapter searchResultsAdapter;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    injectComponents();

    View view = inflater.inflate(R.layout.fragment_tab_search, container, false);

    searchResultsAdapter = new SearchResultsAdapter(getActivity());

    ListView lstvwSearchResults = (ListView)view.findViewById(R.id.lstvwSearchResults);
    lstvwSearchResults.setAdapter(searchResultsAdapter);

    FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabSearch);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        floatingActionButtonSearchClicked();
      }
    });

    return view;
  }


  protected void floatingActionButtonSearchClicked() {

  }


  protected void injectComponents() {
    ((MainActivity) getActivity()).getComponent().inject(this);
  }
}
