package net.dankito.stadtbibliothekmuenchen.model;

import java.util.Date;

/**
 * Created by ganymed on 25/11/16.
 */

public class MediaBorrow {

  protected String title;

  protected String author;

  protected String library;

  protected String signature;

  protected String mediaNumber;

  protected Date dueOn;

  protected String note;


  public MediaBorrow() {

  }


  public boolean areNecessaryInformationSet() {
    return getTitle() != null && getDueOn() != null;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getLibrary() {
    return library;
  }

  public void setLibrary(String library) {
    this.library = library;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getMediaNumber() {
    return mediaNumber;
  }

  public void setMediaNumber(String mediaNumber) {
    this.mediaNumber = mediaNumber;
  }

  public Date getDueOn() {
    return dueOn;
  }

  public void setDueOn(Date dueOn) {
    this.dueOn = dueOn;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }


  @Override
  public String toString() {
    return getTitle() + ": " + getDueOn();
  }

}
