package net.dankito.stadtbibliothekmuenchen.di;

import android.content.Context;

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

  protected final Context context;


  public AndroidDiContainer (Context context) {
    this.context = context;
  }


  @Provides //scope is not necessary for parameters stored within the module
  public Context getContext() {
    return context;
  }


  @Provides
  @Singleton
  public UserSettings provideUserSettings(UserSettingsManager userSettingsManager) {
    return userSettingsManager.deserializeUserSettings();
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
  public NotificationsService provideNotificationsService(IFileStorageService fileStorageService) {
    return new NotificationsService(getContext(), fileStorageService);
  }

  @Provides
  @Singleton
  public ICronService provideCronService() {
    return new AlarmManagerCronService(getContext());
  }

  @Provides
  @Singleton
  public IFileStorageService provideFileStorageService() {
    return new AndroidFileStorageService(getContext());
  }

  @Provides
  @Singleton
  public IEncryptionService provideEncryptionService() {
    return new EncryptionService(getContext());
  }

  @Provides
  @Singleton
  public UserSettingsManager provideUserSettingsManager(IFileStorageService fileStorageService, IEncryptionService encryptionService) {
    return new UserSettingsManager(fileStorageService, encryptionService);
  }

  @Provides
  @Singleton
  public ExpirationsCheckerAndNotifier provideExpirationsCheckerAndNotifier() {
    return new ExpirationsCheckerAndNotifier(getContext());
  }

  @Provides
  @Singleton
  public StadtbibliothekMuenchenClient provideStadtbibliothekMuenchenClient(IWebClient webClient, BorrowExpirationCalculator borrowExpirationCalculator, UserSettings userSettings) {
    return new StadtbibliothekMuenchenClient(webClient, borrowExpirationCalculator, userSettings);
  }

}
