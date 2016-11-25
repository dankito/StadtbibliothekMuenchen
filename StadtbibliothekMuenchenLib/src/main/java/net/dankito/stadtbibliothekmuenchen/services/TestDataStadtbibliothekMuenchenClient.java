package net.dankito.stadtbibliothekmuenchen.services;

import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.util.web.IWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.SimpleSearchCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

import java.util.Date;
import java.util.Random;

/**
 * Created by ganymed on 25/11/16.
 */

public class TestDataStadtbibliothekMuenchenClient extends StadtbibliothekMuenchenClient {

  protected Random random = new Random(System.currentTimeMillis());


  public TestDataStadtbibliothekMuenchenClient(IWebClient webClient, BorrowExpirationCalculator borrowExpirationCalculator) {
    super(webClient, borrowExpirationCalculator);
  }


  @Override
  public void extendAllBorrowsAndGetBorrowsStateAsync(String idCardNumber, String password, ExtendAllBorrowsCallback callback) {
    callback.completed(new ExtendAllBorrowsResult(createTestMediaBorrows()));
  }

  protected MediaBorrows createTestMediaBorrows() {
    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = new MediaBorrow();
    borrow01.setTitle("Faust. Der Tragoedie erster Teil");
    borrow01.setAuthor("Johann Wolfgang von Goethe");
    borrow01.setDueOn(createTestDate(7));
    borrows.addBorrow(borrow01);

    MediaBorrow borrow02 = new MediaBorrow();
    borrow02.setTitle("Schulden");
    borrow02.setAuthor("David Graeber");
    borrow02.setDueOn(createTestDate(20));
    borrows.addBorrow(borrow02);

    MediaBorrow borrow03 = new MediaBorrow();
    borrow03.setTitle("Liebe in Zeiten der Cholera");
    borrow03.setAuthor("Gabriel Garcia Marquez");
    borrow03.setDueOn(createTestDate(-1));
    borrows.addBorrow(borrow03);

    MediaBorrow borrow04 = new MediaBorrow();
    borrow04.setTitle("Angriff auf die Freiheit");
    borrow04.setAuthor("Ilija Trojanow / Julia Zeh");
    borrow04.setDueOn(createTestDate(1));
    borrows.addBorrow(borrow04);

    MediaBorrow borrow05 = new MediaBorrow();
    borrow05.setTitle("Befreiung vom Überfluss. Auf dem Weg in die Postwachstumsökonomie");
    borrow05.setAuthor("Niko Paech");
    borrow05.setDueOn(createTestDate(44));
    borrows.addBorrow(borrow05);

    MediaBorrow borrow06 = new MediaBorrow();
    borrow06.setTitle("Gullivers Reisen");
    borrow06.setAuthor("Jonathan Swift");
    borrow06.setDueOn(createTestDate(8));
    borrows.addBorrow(borrow06);

    MediaBorrow borrow07 = new MediaBorrow();
    borrow07.setTitle("Canary Row");
    borrow07.setAuthor("John Steinbeck");
    borrow07.setDueOn(createTestDate(2));
    borrows.addBorrow(borrow07);

    MediaBorrow borrow08 = new MediaBorrow();
    borrow08.setTitle("Eine Geschichte des 19. Jahrhunderts");
    borrow08.setAuthor("Jürgen Osterhammel");
    borrow08.setDueOn(createTestDate(-5));
    borrows.addBorrow(borrow08);

    MediaBorrow borrow09 = new MediaBorrow();
    borrow09.setTitle("Kritik der reinen Vernunft");
    borrow09.setAuthor("Immanuel Kant");
    borrow09.setDueOn(createTestDate(12));
    borrows.addBorrow(borrow09);

    MediaBorrow borrow10 = new MediaBorrow();
    borrow10.setTitle("Liebe");
    borrow10.setAuthor("Marieke");
    borrow10.setDueOn(createTestDate(144));
    borrows.addBorrow(borrow10);

    return borrows;
  }

  protected Date createTestDate(int countDaysFromToday) {
    return new Date(new Date().getTime() + (countDaysFromToday * 24 * 60 * 60 * 1000));
  }


  @Override
  public void doSimpleSearchAsync(String searchTerm, SimpleSearchCallback callback) {
    callback.completed(new SimpleSearchResponse(searchTerm, createTestSearchResults()));
  }

  protected SearchResults createTestSearchResults() {
    SearchResults searchResults = new SearchResults();

    SearchResult searchResult01 = new SearchResult();
    searchResult01.setMediaInfo("Johann Wolfgang von Goethe - Faust. Der Tragoedie erster Teil");
    searchResult01.setYear("2016");
    searchResult01.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult01.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult01);

    SearchResult searchResult02 = new SearchResult();
    searchResult02.setMediaInfo("David Graeber - Schulden");
    searchResult02.setYear("2016");
    searchResult02.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult02.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult02);

    SearchResult searchResult03 = new SearchResult();
    searchResult03.setMediaInfo("Gabriel Garcia Marquez - Liebe in Zeiten der Cholera");
    searchResult03.setYear("2016");
    searchResult03.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult03.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult03);

    SearchResult searchResult04 = new SearchResult();
    searchResult04.setMediaInfo("Ilija Trojanow / Julia Zeh - Angriff auf die Freiheit");
    searchResult04.setYear("2016");
    searchResult04.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult04.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult04);

    SearchResult searchResult05 = new SearchResult();
    searchResult05.setMediaInfo("Niko Paech - Befreiung vom Überfluss. Auf dem Weg in die Postwachstumsökonomie");
    searchResult05.setYear("2016");
    searchResult05.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult05.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult05);

    SearchResult searchResult06 = new SearchResult();
    searchResult06.setMediaInfo("Jonathan Swift - Gullivers Reisen");
    searchResult06.setYear("2016");
    searchResult06.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult06.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult06);

    SearchResult searchResult07 = new SearchResult();
    searchResult07.setMediaInfo("John Steinbeck - Canary Row");
    searchResult07.setYear("2016");
    searchResult07.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult07.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult07);

    SearchResult searchResult08 = new SearchResult();
    searchResult08.setMediaInfo("Jürgen Osterhammel - Eine Geschichte des 19. Jahrhunderts");
    searchResult08.setYear("2016");
    searchResult08.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult08.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult08);

    SearchResult searchResult09 = new SearchResult();
    searchResult09.setMediaInfo("Immanuel Kant - Kritik der reinen Vernunft");
    searchResult09.setYear("2016");
    searchResult09.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult09.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult09);

    SearchResult searchResult10 = new SearchResult();
    searchResult10.setMediaInfo("Marieke - Liebe");
    searchResult10.setYear("2016");
    searchResult10.setMediaTypeIconUrl("https://ssl.muenchen.de/aDISWeb/images/POOLSTPM@@@@@@@@/gif/POOLSTPM@@@@@@@@_53007F00_25F99500.gif");
    searchResult10.setAvailabilityIconUrl(createRandomAvailability());
    searchResults.addSearchResult(searchResult10);


    return searchResults;
  }

  protected String createRandomAvailability() {
    int nextAvailabilityIndex = random.nextInt(3);

    if(nextAvailabilityIndex == 0) {
      return "https://ssl.muenchen.de/aDISWeb/icons/verfu_ja.gif";
    }
    else if(nextAvailabilityIndex == 1) {
      return "https://ssl.muenchen.de/aDISWeb/icons/verfu_nein.gif";
    }
    else {
      return "https://ssl.muenchen.de/aDISWeb/icons/verfu_info.gif";
    }
  }
}
