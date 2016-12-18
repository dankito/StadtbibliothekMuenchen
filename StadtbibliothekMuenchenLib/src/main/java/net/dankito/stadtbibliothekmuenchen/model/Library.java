package net.dankito.stadtbibliothekmuenchen.model;

/**
 * Created by ganymed on 18/12/16.
 */

public enum Library {


  ALLACH_UNTERMENZING("Allach-Untermenzing"),
  BERG_AM_LAIM("Berg am Laim"),
  BOGENHAUSEN("Bogenhausen"),
  FÜRSTENRIED("Fürstenried"),
  GIESING("Giesing"),
  HADERN("Hadern"),
  HASENBERGL("Hasenbergl"),
  ISARVORSTADT("Isarvorstadt"),
  LAIM("Laim"),
  MAXVORSTADT("Maxvorstadt"),
  MILBERTSHOFEN("Milbertshofen"),
  MOOSACH("Moosach"),
  NEUAUBING("Neuaubing"),
  NEUHAUSEN("Neuhausen"),
  NEUPERLACH("Neuperlach"),
  PASING("Pasing"),
  RAMERSDORF("Ramersdorf"),
  SCHWABING("Schwabing"),
  SENDLING("Sendling"),
  WALDTRUDERING("Waldtrudering"),
  WESTEND("Westend"),
  ZENTRALBIBLIOTHEK("Zentralbibliothek Am Gasteig"),
  MONACENSIA("Monacensia");


  private String name;

  Library(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }


  @Override
  public String toString() {
    return getName();
  }


  public static Library parse(String libraryString) {
    libraryString = libraryString.toLowerCase();

    for(Library library : Library.values()) {
      if(library.getName().toLowerCase().equals(libraryString)) {
        return library;
      }
    }

    if(libraryString.startsWith(Library.ZENTRALBIBLIOTHEK.getName().toLowerCase())) {
      return Library.ZENTRALBIBLIOTHEK;
    }

    return null;
  }

}
