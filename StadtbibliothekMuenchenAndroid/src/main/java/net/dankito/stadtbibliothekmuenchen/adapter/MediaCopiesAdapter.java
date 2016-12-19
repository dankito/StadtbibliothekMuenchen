package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.MediaCopy;

import java.util.List;

/**
 * Created by ganymed on 25/11/16.
 */

public class MediaCopiesAdapter extends BaseAdapter {

  protected Activity activity;

  protected List<MediaCopy> copies;


  public MediaCopiesAdapter(Activity activity, List<MediaCopy> copies) {
    this.activity = activity;
    this.copies = copies;
  }


  @Override
  public int getCount() {
    return copies.size();
  }

  @Override
  public Object getItem(int index) {
    return copies.get(index);
  }

  @Override
  public long getItemId(int index) {
    return index;
  }

  @Override
  public View getView(int index, View convertView, ViewGroup parent) {
    if(convertView == null) {
      convertView = activity.getLayoutInflater().inflate(R.layout.list_item_media_copy, parent, false);
    }

    MediaCopy copy = (MediaCopy)getItem(index);
    convertView.setTag(copy);

    String libraryLocation = copy.getLibraryName();
    if(copy.getLocation() != null) {
      libraryLocation = libraryLocation + " - " + copy.getLocation();
    }

    TextView txtvwMediaCopyLibraryLocation = (TextView)convertView.findViewById(R.id.txtvwMediaCopyLibraryLocation);
    txtvwMediaCopyLibraryLocation.setText(libraryLocation);

    TextView txtvwMediaCopyShelfMark = (TextView)convertView.findViewById(R.id.txtvwMediaCopyShelfMark);
    txtvwMediaCopyShelfMark.setText(copy.getShelfmark());

    TextView txtvwMediaCopyLendingPeriod = (TextView)convertView.findViewById(R.id.txtvwMediaCopyLendingPeriod);
    txtvwMediaCopyLendingPeriod.setText(copy.getLendingPeriod());

    TextView txtvwMediaCopyAvailability = (TextView)convertView.findViewById(R.id.txtvwMediaCopyAvailability);
    txtvwMediaCopyAvailability.setText(copy.getAvailability());

    return convertView;
  }

}
