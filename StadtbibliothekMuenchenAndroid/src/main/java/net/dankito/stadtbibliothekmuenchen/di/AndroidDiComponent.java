package net.dankito.stadtbibliothekmuenchen.di;

import net.dankito.stadtbibliothekmuenchen.MainActivity;
import net.dankito.stadtbibliothekmuenchen.activities.SettingsActivity;
import net.dankito.stadtbibliothekmuenchen.fragments.TabBorrowsFragment;
import net.dankito.stadtbibliothekmuenchen.fragments.TabSearchFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ganymed on 03/11/16.
 */
@Singleton
@Component(modules = { AndroidDiContainer.class } )
public interface AndroidDiComponent {

  // to update the fields in your activities
  void inject(MainActivity activity);

  void inject(SettingsActivity settingsActivity);

  void inject(TabBorrowsFragment tabBorrowsFragment);

  void inject(TabSearchFragment tabSearchFragment);

}
