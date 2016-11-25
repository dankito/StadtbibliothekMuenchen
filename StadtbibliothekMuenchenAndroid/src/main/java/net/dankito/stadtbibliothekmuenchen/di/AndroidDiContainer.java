package net.dankito.stadtbibliothekmuenchen.di;

import android.app.Activity;

import net.dankito.stadtbibliothekmuenchen.services.BorrowExpirationCalculator;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.services.TestDataStadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.util.web.IWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.OkHttpWebClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ganymed on 03/11/16.
 */
@Module
public class AndroidDiContainer {

  protected final Activity activity;


  public AndroidDiContainer (Activity activity) {
    this.activity = activity;
  }


  @Provides //scope is not necessary for parameters stored within the module
  public Activity getActivity() {
    return activity;
  }


  @Provides
  @Singleton
  public UserSettings provideUserSettings() {
    return new UserSettings(); // TODO: read from storage
  }

  @Provides
  @Singleton
  public IWebClient provideWebClient() {
    return new OkHttpWebClient();
  }

  @Provides
  @Singleton
  public BorrowExpirationCalculator provideBorrowExpirationManager() {
    return new BorrowExpirationCalculator();
  }

  @Provides
  @Singleton
  public StadtbibliothekMuenchenClient provideStadtbibliothekMuenchenClient(IWebClient webClient, BorrowExpirationCalculator borrowExpirationCalculator) {
    return new TestDataStadtbibliothekMuenchenClient(webClient, borrowExpirationCalculator);
  }

}
