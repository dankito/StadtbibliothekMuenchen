package net.dankito.stadtbibliothekmuenchen.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

/**
 * Created by ganymed on 30/11/16.
 */

public class TimePickerFragment extends DialogFragment {

  protected int hourToShow;

  protected int minuteToShow;

  protected TimePickerDialog.OnTimeSetListener listener = null;


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Create a new instance of TimePickerDialog and return it
    return new TimePickerDialog(getActivity(), listener, hourToShow, minuteToShow,
        DateFormat.is24HourFormat(getActivity()));
  }


  public void setHourToShow(int hourToShow) {
    this.hourToShow = hourToShow;
  }

  public void setMinuteToShow(int minuteToShow) {
    this.minuteToShow = minuteToShow;
  }

  public void setListener(TimePickerDialog.OnTimeSetListener listener) {
    this.listener = listener;
  }

}
