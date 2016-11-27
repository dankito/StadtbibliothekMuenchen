package net.dankito.stadtbibliothekmuenchen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.dankito.stadtbibliothekmuenchen.activities.SettingsActivity;
import net.dankito.stadtbibliothekmuenchen.adapter.MainActivityTabsAdapter;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {


  @Inject
  protected UserSettings userSettings;

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  protected MainActivityTabsAdapter tabsAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  protected ViewPager viewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    injectComponents();

    setupUi();

    checkIfUserSettingsAreSetUp();
  }

  protected void injectComponents() {
    ((StadtbibliothekMuenchenApplication)getApplicationContext()).getComponent().inject(this);
  }

  protected void setupUi() {
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    tabsAdapter = new MainActivityTabsAdapter(this, getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    viewPager = (ViewPager) findViewById(R.id.container);
    viewPager.setAdapter(tabsAdapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
  }

  protected void checkIfUserSettingsAreSetUp() {
    if(userSettings.isIdentityCardNumberSet() == false || userSettings.isPasswordSet() == false) {
      showSettingsDialog();
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if(id == R.id.mnitmSettings) {
      showSettingsDialog();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  protected void showSettingsDialog() {
    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
    startActivity(intent);
  }

}
