package net.dankito.stadtbibliothekmuenchen.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.listener.UserSettingsManagerListener;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ganymed on 27/11/16.
 */

public class UserSettingsManager {

  protected static final String USER_SETTINGS_FILENAME = "UserSettings.json";


  protected IFileStorageService fileStorageService;

  protected IEncryptionService encryptionService;

  protected ObjectMapper mapper = new ObjectMapper();

  protected List<UserSettingsManagerListener> listeners = new CopyOnWriteArrayList<>();


  public UserSettingsManager(IFileStorageService fileStorageService, IEncryptionService encryptionService) {
    this.fileStorageService = fileStorageService;
    this.encryptionService = encryptionService;

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }


  public UserSettings deserializeUserSettings() {
    try {
      String encrypted = fileStorageService.readFromFile(USER_SETTINGS_FILENAME);
      String json = encryptionService.decrypt(encrypted);

      mapper.setTimeZone(TimeZone.getDefault()); // to fix bug that Calender gets deserialized with wrong timezone

      return mapper.readValue(json, UserSettings.class);
    } catch(Exception e) {
      return createDefaultUserSettings();
    }
  }

  protected UserSettings createDefaultUserSettings() {
    UserSettings defaultSettings = new UserSettings();

    Calendar periodicalCheck = Calendar.getInstance();
    periodicalCheck.setTimeInMillis(System.currentTimeMillis());
    periodicalCheck.set(Calendar.HOUR_OF_DAY, 6);
    periodicalCheck.set(Calendar.MINUTE, 0);
    defaultSettings.setPeriodicalBorrowsExpirationCheckTime(periodicalCheck);

    return defaultSettings;
  }


  public void saveUserSettings(UserSettings userSettings) throws Exception {
    String json = mapper.writeValueAsString(userSettings);
    String encrypted = encryptionService.encrypt(json);

    fileStorageService.writeToFile(encrypted, USER_SETTINGS_FILENAME);

    callListeners(userSettings);
  }


  public boolean addListener(UserSettingsManagerListener listener) {
    return listeners.add(listener);
  }

  public boolean removeListener(UserSettingsManagerListener listener) {
    return listeners.remove(listener);
  }

  protected void callListeners(UserSettings updatedSettings) {
    for(UserSettingsManagerListener listener : listeners) {
      listener.userSettingsUpdated(updatedSettings);
    }
  }

}
