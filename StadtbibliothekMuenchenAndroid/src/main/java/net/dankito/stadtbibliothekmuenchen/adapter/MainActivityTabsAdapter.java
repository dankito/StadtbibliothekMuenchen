package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.R;

/**
 * Created by ganymed on 25/11/16.
 */

public class MainActivityTabsAdapter extends FragmentPagerAdapter {

  protected Activity activity;

  protected FragmentManager fragmentManager;


  public MainActivityTabsAdapter(Activity activity, FragmentManager fragmentManager) {
    super(fragmentManager);

    this.activity = activity;
    this.fragmentManager = fragmentManager;
  }

  @Override
  public int getCount() {
    return 1;
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
    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    return MainActivity.PlaceholderFragment.newInstance(position + 1);
  }

}
