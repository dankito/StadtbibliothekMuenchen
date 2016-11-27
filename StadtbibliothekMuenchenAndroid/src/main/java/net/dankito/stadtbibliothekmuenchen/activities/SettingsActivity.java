package net.dankito.stadtbibliothekmuenchen.activities;

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

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.StadtbibliothekMuenchenApplication;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.services.StadtbibliothekMuenchenClient;
import net.dankito.stadtbibliothekmuenchen.services.UserSettingsManager;
import net.dankito.stadtbibliothekmuenchen.util.AlertHelper;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;

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


  protected EditText edtxtAddress;

  protected EditText edtxtPassword;

  protected CheckBox chkbxPeriodicallyCheckForExpiredBorrows;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((StadtbibliothekMuenchenApplication)getApplicationContext()).getComponent().inject(this);

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

    chkbxPeriodicallyCheckForExpiredBorrows = (CheckBox)findViewById(R.id.chkbxPeriodicallyCheckForExpiredBorrows);
    chkbxPeriodicallyCheckForExpiredBorrows.setOnCheckedChangeListener(chkbxPeriodicallyCheckForExpiredBorrowsCheckedChangeListener);
    chkbxPeriodicallyCheckForExpiredBorrows.setChecked(userSettings.isPeriodicalBorrowsExpirationCheckTimeSet());

    Button btnOk = (Button)findViewById(R.id.btnOk);
    btnOk.setOnClickListener(btnOkClickListener);

    Button btnCancel = (Button)findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(btnCancelClickListener);

    setupToolbar();
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
