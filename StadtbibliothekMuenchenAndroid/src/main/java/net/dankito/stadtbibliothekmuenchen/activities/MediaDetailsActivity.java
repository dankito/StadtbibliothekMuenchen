package net.dankito.stadtbibliothekmuenchen.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.adapter.MediaCopiesAdapter;
import net.dankito.stadtbibliothekmuenchen.model.MediaDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ganymed on 27/11/16.
 */

public class MediaDetailsActivity extends AppCompatActivity {

  public static final String MEDIA_DETAILS_KEY = "MediaDetails";


  public MediaDetailsActivity() {

  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    MediaDetails details = getMediaDetails(savedInstanceState);

    setupViews(details);
  }

  protected MediaDetails getMediaDetails(Bundle savedInstanceState) {
    String mediaDetailsJson = null;

    if(savedInstanceState != null) {
      mediaDetailsJson = savedInstanceState.getString(MEDIA_DETAILS_KEY);
    }
    else if(getIntent() != null) {
      mediaDetailsJson = getIntent().getStringExtra(MEDIA_DETAILS_KEY);
    }

    if(mediaDetailsJson != null) {
      try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(mediaDetailsJson, MediaDetails.class);
      } catch(Exception e) {
        // TODO: show error (should never occur)
      }
    }

    return new MediaDetails();
  }

  protected void setupViews(MediaDetails details) {
    setContentView(R.layout.fragment_media_details);

    TextView txtMediaDetailsTitle = (TextView) findViewById(R.id.txtMediaDetailsTitle);
    txtMediaDetailsTitle.setText(details.getTitle());

    ListView lstvwMediaDetailsCopies = (ListView)findViewById(R.id.lstvwMediaDetailsCopies);
    lstvwMediaDetailsCopies.setAdapter(new MediaCopiesAdapter(this, details.getCopies()));

    setupToolbar();
  }

  protected void setupToolbar() {
    Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.media_details);

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

  protected void closeActivity() {
    finish();
  }

}
