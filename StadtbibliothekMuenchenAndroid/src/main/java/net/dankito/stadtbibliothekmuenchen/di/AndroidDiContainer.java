package net.dankito.stadtbibliothekmuenchen.di;

import android.app.Activity;

import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.AlarmManagerCronService;
import net.dankito.stadtbibliothekmuenchen.services.AndroidFileStorageService;
import net.dankito.stadtbibliothekmuenchen.services.BorrowExpirationCalculator;
import net.dankito.stadtbibliothekmuenchen.services.EncryptionService;
import net.dankito.stadtbibliothekmuenchen.services.ExpirationsCheckerAndNotifier;
import net.dankito.stadtbibliothekmuenchen.services.ICronService;
import net.dankito.stadtbibliothekmuenchen.services.IEncryptionService;
import net.dankito.stadtbibliothekmuenchen.services.IFileStorageService;
import net.dankito.stadtbibliothekmuenchen.services.NotificationsService;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.services.UserSettingsManager;
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
  public NotificationsService provideNotificationsService() {
    return new NotificationsService(getActivity());
  }

  @Provides
  @Singleton
  public ICronService provideCronService() {
    return new AlarmManagerCronService(getActivity());
  }

  @Provides
  @Singleton
  public IFileStorageService provideFileStorageService() {
    return new AndroidFileStorageService(getActivity());
  }

  @Provides
  @Singleton
  public IEncryptionService provideEncryptionService() {
    return new EncryptionService(getActivity());
  }

  @Provides
  @Singleton
  public UserSettingsManager provideUserSettingsManager(IFileStorageService fileStorageService, IEncryptionService encryptionService) {
    return new UserSettingsManager(fileStorageService, encryptionService);
  }

  @Provides
  @Singleton
  public ExpirationsCheckerAndNotifier provideExpirationsCheckerAndNotifier(NotificationsService notificationsService, ICronService cronService,
                                                                            StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient, UserSettings userSettings) {
    return new ExpirationsCheckerAndNotifier(getActivity(), notificationsService, cronService, stadtbibliothekMuenchenClient, userSettings);
  }

  @Provides
  @Singleton
  public StadtbibliothekMuenchenClient provideStadtbibliothekMuenchenClient(IWebClient webClient, BorrowExpirationCalculator borrowExpirationCalculator, UserSettings userSettings) {
    return new StadtbibliothekMuenchenClient(webClient, borrowExpirationCalculator, userSettings);
  }

}
