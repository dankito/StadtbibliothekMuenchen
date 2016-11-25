package net.dankito.stadtbibliothekmuenchen.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.dankito.stadtbibliothekmuenchen.R;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ganymed on 25/11/16.
 */

public class BorrowsAdapter extends BaseAdapter {

  protected static final DateFormat DUE_ON_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


  protected Activity activity;

  protected MediaBorrows borrows;


  public BorrowsAdapter(Activity activity) {
    this.activity = activity;
  }


  public void setBorrowsThreadSafe(final MediaBorrows borrows) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        setBorrows(borrows);
      }
    });
  }

  public void setBorrows(MediaBorrows borrows) {
    this.borrows = borrows;

    notifyDataSetChanged();
  }


  @Override
  public int getCount() {
    if(borrows != null) {
      return borrows.getBorrows().size();
    }

    return 0;
  }

  @Override
  public Object getItem(int index) {
    return borrows.getBorrows().get(index);
  }

  @Override
  public long getItemId(int index) {
    return index;
  }

  @Override
  public View getView(int index, View convertView, ViewGroup parent) {
    if(convertView == null) {
      convertView = activity.getLayoutInflater().inflate(R.layout.list_item_borrow, parent, false);
    }

    MediaBorrow borrow = (MediaBorrow)getItem(index);

    String authorAndTitle = borrow.getTitle();
    if(borrow.isAuthorSet()) {
      authorAndTitle = borrow.getAuthor() + " - " + authorAndTitle;
    }

    TextView txtvwBorrowTitle = (TextView)convertView.findViewById(R.id.txtvwBorrowTitle);
    txtvwBorrowTitle.setText(authorAndTitle);

    TextView txtvwBorrowDueOnDate = (TextView)convertView.findViewById(R.id.txtvwBorrowDueOnDate);
    txtvwBorrowDueOnDate.setText(borrow.isDueOnSet() ? DUE_ON_DATE_FORMAT.format(borrow.getDueOn()) : "");

    return convertView;
  }

}
