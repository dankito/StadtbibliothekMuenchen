package net.dankito.stadtbibliothekmuenchen.services.listener;

import net.dankito.stadtbibliothekmuenchen.model.UserSettings;

/**
 * Created by ganymed on 27/11/16.
 */

public interface UserSettingsManagerListener {

  void userSettingsUpdated(UserSettings updatedSettings);

}
