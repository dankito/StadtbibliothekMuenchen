package net.dankito.stadtbibliothekmuenchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.adapter.BorrowsAdapter;

/**
 * Created by ganymed on 25/11/16.
 */

public class TabBorrowsFragment extends Fragment {

  protected BorrowsAdapter borrowsAdapter;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tab_borrows, container, false);

    borrowsAdapter = new BorrowsAdapter(getActivity());

    ListView lstvwBorrows = (ListView)view.findViewById(R.id.lstvwBorrows);
    lstvwBorrows.setAdapter(borrowsAdapter);

    return view;
  }

}
