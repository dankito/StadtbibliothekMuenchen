package net.dankito.stadtbibliothekmuenchen.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.StadtbibliothekMuenchenApplication;
import net.dankito.stadtbibliothekmuenchen.fragments.TimePickerFragment;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.services.UserSettingsManager;
import net.dankito.stadtbibliothekmuenchen.util.AlertHelper;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by ganymed on 27/11/16.
 */

public class SettingsActivity extends AppCompatActivity {

  @Inject
  protected UserSettings userSettings;

  @Inject
  protected UserSettingsManager userSettingsManager;

  @Inject
  protected StadtbibliothekMuenchenClient stadtbibliothekMuenchenClient;

  protected Calendar newTimeToCheckForExpirations = null;


  protected EditText edtxtAddress;

  protected EditText edtxtPassword;

  protected CheckBox chkbxPeriodicallyCheckForExpiredBorrows;

  protected TextView txtvwTimeToCheckForExpirations;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((StadtbibliothekMuenchenApplication)getApplicationContext()).getComponent().inject(this);
    this.newTimeToCheckForExpirations = userSettings.getPeriodicalBorrowsExpirationCheckTime();

    setupViews();
  }

  protected void setupViews() {
    setContentView(R.layout.fragment_settings);

    if(userSettings.isIdentityCardNumberSet() == false || userSettings.isPasswordSet() == false) {
      TextView txtvwHintIdentityCardNumberOrPasswordNotSet = (TextView)findViewById(R.id.txtvwHintIdentityCardNumberOrPasswordNotSet);
      txtvwHintIdentityCardNumberOrPasswordNotSet.setVisibility(View.VISIBLE);
    }

    edtxtAddress = (EditText)findViewById(R.id.edtxtIdentityCardNumber);
    edtxtAddress.setText(userSettings.getIdentityCardNumber());

    edtxtPassword = (EditText)findViewById(R.id.edtxtPassword);
    edtxtPassword.setText(userSettings.getPassword());

    Button btnTestUserSettings = (Button)findViewById(R.id.btnTestUserSettings);
    btnTestUserSettings.setOnClickListener(btnTestUserSettingsClickListener);

    txtvwTimeToCheckForExpirations = (TextView)findViewById(R.id.txtvwTimeToCheckForExpirations);
    txtvwTimeToCheckForExpirations.setOnClickListener(txtvwTimeToCheckForExpirationsClickListener);
    showTimeToCheckForExpirations(userSettings.getPeriodicalBorrowsExpirationCheckTime());

    chkbxPeriodicallyCheckForExpiredBorrows = (CheckBox)findViewById(R.id.chkbxPeriodicallyCheckForExpiredBorrows);
    chkbxPeriodicallyCheckForExpiredBorrows.setOnCheckedChangeListener(chkbxPeriodicallyCheckForExpiredBorrowsCheckedChangeListener);
    chkbxPeriodicallyCheckForExpiredBorrows.setChecked(userSettings.isPeriodicalBorrowsExpirationCheckTimeSet());

    Button btnOk = (Button)findViewById(R.id.btnOk);
    btnOk.setOnClickListener(btnOkClickListener);

    Button btnCancel = (Button)findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(btnCancelClickListener);

    setupToolbar();
  }

  protected void showTimeToCheckForExpirations(Calendar checkTime) {
    txtvwTimeToCheckForExpirations.setText(getString(R.string.fragment_setting_time_to_check_for_expirations,
        checkTime.get(Calendar.HOUR_OF_DAY), checkTime.get(Calendar.MINUTE)));
  }

  protected void setupToolbar() {
    Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.settings);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if(id == android.R.id.home) {
      closeActivity();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  protected void testUserSettings() {
    stadtbibliothekMuenchenClient.loginAsync(edtxtAddress.getText().toString(), edtxtPassword.getText().toString(), new LoginCallback() {
      @Override
      public void completed(LoginResult result) {
        if(result.isSuccessful() == false) {
          showTestingUserSettingsFailedMessage(result);
        }
        else {
          showTestingUserSettingsSucceededMessage();
        }
      }
    });
  }

  protected void showTestingUserSettingsFailedMessage(LoginResult result) {
    String title = getString(R.string.fragment_settings_testing_fritz_box_settings_failed_title);
    AlertHelper.showMessageThreadSafe(this, result.getError(), title);
  }

  protected void showTestingUserSettingsSucceededMessage() {
    AlertHelper.showMessageThreadSafe(this, getString(R.string.fragment_settings_testing_fritz_box_settings_succeeded));
  }


  protected void saveUserSettings() {
    userSettings.setIdentityCardNumber(edtxtAddress.getText().toString());
    userSettings.setPassword(edtxtPassword.getText().toString());

    if(chkbxPeriodicallyCheckForExpiredBorrows.isChecked() == false) {
      userSettings.setPeriodicalBorrowsExpirationCheckTime(null);
    }
    else {
      userSettings.setPeriodicalBorrowsExpirationCheckTime(newTimeToCheckForExpirations);
    }

    try {
      userSettingsManager.saveUserSettings(userSettings);
    } catch(Exception e) {
      // TODO: show error message to user
    }
  }


  protected View.OnClickListener btnTestUserSettingsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      testUserSettings();
    }
  };

  protected CompoundButton.OnCheckedChangeListener chkbxPeriodicallyCheckForExpiredBorrowsCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
      txtvwTimeToCheckForExpirations.setEnabled(checked);
    }
  };

  View.OnClickListener txtvwTimeToCheckForExpirationsClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      TimePickerFragment timePickerFragment = new TimePickerFragment();
      timePickerFragment.setListener(timePickerListener);

      Calendar checkTime = userSettings.getPeriodicalBorrowsExpirationCheckTime();
      timePickerFragment.setHourToShow(checkTime.get(Calendar.HOUR_OF_DAY));
      timePickerFragment.setMinuteToShow(checkTime.get(Calendar.MINUTE));

      timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }
  };

  protected TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
    public void onTimeSet(TimePicker view, int selectedHour,
                          int selectedMinute) {
      newTimeToCheckForExpirations = Calendar.getInstance();
      newTimeToCheckForExpirations.set(Calendar.HOUR_OF_DAY, selectedHour);
      newTimeToCheckForExpirations.set(Calendar.MINUTE, selectedMinute);

      showTimeToCheckForExpirations(newTimeToCheckForExpirations);
    }
  };

  protected View.OnClickListener btnOkClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      saveUserSettings();
      closeActivity();
    }
  };

  protected View.OnClickListener btnCancelClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      closeActivity();
    }
  };

  protected void closeActivity() {
    finish();
  }

}
