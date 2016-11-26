package net.dankito.stadtbibliothekmuenchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.adapter.BorrowsAdapter;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.CronService;
import net.dankito.stadtbibliothekmuenchen.services.ExpirationsCheckerAndNotifier;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.util.AlertHelper;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;

import javax.inject.Inject;

/**
 * Created by ganymed on 25/11/16.
 */

public class TabBorrowsFragment extends Fragment {

  @Inject
  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  @Inject
  protected ExpirationsCheckerAndNotifier expirationsCheckerAndNotifier;

  @Inject
  protected UserSettings userSettings;

  @Inject
  protected CronService cronService;

  protected BorrowsAdapter borrowsAdapter;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    injectComponents();

    View view = inflater.inflate(R.layout.fragment_tab_borrows, container, false);

    borrowsAdapter = new BorrowsAdapter(getActivity());

    ListView lstvwBorrows = (ListView)view.findViewById(R.id.lstvwBorrows);
    lstvwBorrows.setAdapter(borrowsAdapter);

    getBorrows();

    return view;
  }


  protected void getBorrows() {
    stadtbibliothekMuenchenClient.extendAllBorrowsAndGetBorrowsStateAsync(new ExtendAllBorrowsCallback() {
      @Override
      public void completed(ExtendAllBorrowsResult result) {
        if(result.isSuccessful()) {
          retrievedExtendAllBorrowsResult(result);
        }
        else {
          showErrorMessageThreadSafe(result.getError(), getActivity().getString(R.string.error_title_could_not_get_borrows));
        }
      }
    });
  }

  protected void retrievedExtendAllBorrowsResult(ExtendAllBorrowsResult result) {
    borrowsAdapter.setBorrowsThreadSafe(result.getBorrows());

    if(userSettings.isShowSystemNotificationsOnExpirationEnabled()) {
      expirationsCheckerAndNotifier.retrievedExpirations(result.getBorrows().getExpirations());
    }
  }


  protected void showErrorMessageThreadSafe(String errorMessage, String errorMessageTitle) {
    AlertHelper.showErrorMessageThreadSafe(getActivity(), errorMessage, errorMessageTitle);
  }

  protected void injectComponents() {
    ((MainActivity) getActivity()).getComponent().inject(this);
  }

}
