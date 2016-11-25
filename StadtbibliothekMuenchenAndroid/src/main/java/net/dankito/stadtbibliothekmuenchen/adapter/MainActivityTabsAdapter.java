package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.fragments.TabBorrowsFragment;
import net.dankito.stadtbibliothekmuenchen.fragments.TabSearchFragment;

/**
 * Created by ganymed on 25/11/16.
 */

public class MainActivityTabsAdapter extends FragmentPagerAdapter {

  protected Activity activity;

  protected FragmentManager fragmentManager;

  protected TabBorrowsFragment borrowsFragment = null;

  protected TabSearchFragment searchFragment = null;


  public MainActivityTabsAdapter(Activity activity, FragmentManager fragmentManager) {
    super(fragmentManager);

    this.activity = activity;
    this.fragmentManager = fragmentManager;
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    Resources resources = activity.getResources();

    switch (position) {
      case 0:
        return resources.getString(R.string.tab_title_borrows);
      case 1:
        return resources.getString(R.string.tab_title_search);
    }

    return null;
  }

  @Override
  public Fragment getItem(int position) {
    if(position == 0) {
      if(borrowsFragment == null) {
        borrowsFragment = new TabBorrowsFragment();
      }
      return borrowsFragment;
    }
    else if(position == 1) {
      if(searchFragment == null) {
        searchFragment = new TabSearchFragment();
      }
      return searchFragment;
    }

    return null;
  }

}
