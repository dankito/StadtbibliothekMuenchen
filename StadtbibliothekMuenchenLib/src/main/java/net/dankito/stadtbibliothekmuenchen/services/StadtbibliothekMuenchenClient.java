package net.dankito.stadtbibliothekmuenchen.services;

import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.util.web.IWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.RequestCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.RequestParameters;
import net.dankito.stadtbibliothekmuenchen.util.web.WebClientResponse;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.SimpleSearchCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ganymed on 24/11/16.
 */

public class StadtbibliothekMuenchenClient {

  public static final int COUNT_SEARCH_RESULTS_PER_PAGE = 22;


  protected static final String HOMEPAGE_URL = "https://ssl.muenchen.de";

  protected static final String ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT = "Anmelden / Login";

  protected static final String BENUTZERKONTO_NAVIGATION_ITEM_TEXT = "Benutzerkonto";

  protected static final String EINFACHE_SUCHE_NAVIGATION_ITEM_TEXT = "Einfache Suche";

  protected static final String ERWEITERTE_SUCHE_NAVIGATION_ITEM_TEXT = "Erweiterte Suche";

  protected static final String FORM_NAME = "Form0";


  protected static final int BORROW_TABLE_DATA_INDEX_DUE_ON = 1;

  protected static final int BORROW_TABLE_DATA_INDEX_LIBRARY = 2;

  protected static final int BORROW_TABLE_DATA_INDEX_TITLE = 3;

  protected static final int BORROW_TABLE_DATA_INDEX_NOTE = 4;

  protected static final DateFormat DueOnDateParser = new SimpleDateFormat("dd.MM.yyyy");


  protected static final int SEARCH_RESULT_TABLE_DATA_INDEX_MEDIA_TYPE = 2;

  protected static final int SEARCH_RESULT_TABLE_DATA_INDEX_MEDIA_INFO = 3;

  protected static final int SEARCH_RESULT_TABLE_DATA_INDEX_AVAILABILITY = 4;

  protected static final int SEARCH_RESULT_TABLE_DATA_INDEX_YEAR = 5;


  public static final int DOWNLOAD_CONNECTION_TIMEOUT_MILLIS = 2000;

  public static final int COUNT_CONNECTION_RETRIES = 2;


  private static final Logger log = LoggerFactory.getLogger(StadtbibliothekMuenchenClient.class);


  protected IWebClient webClient;

  protected BorrowExpirationCalculator borrowExpirationCalculator;

  protected UserSettings userSettings;

  protected boolean isLoggedIn = false;

  // stores the once retrieved front page html so that we can navigate to each option (User Account (Benutzerkonto), simple and extended search, ...)
  protected String frontPageHtml = null;


  public StadtbibliothekMuenchenClient(IWebClient webClient, BorrowExpirationCalculator borrowExpirationCalculator, UserSettings userSettings) {
    this.webClient = webClient;
    this.borrowExpirationCalculator = borrowExpirationCalculator;
    this.userSettings = userSettings;
  }


  public void loginAsync(final LoginCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(HOMEPAGE_URL);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new LoginResult(response.getError()));
        }
        else {
          StadtbibliothekMuenchenClient.this.frontPageHtml = response.getBody();

          parseFrontPageAndLogin(userSettings.getIdentityCardNumber(), userSettings. getPassword(), response, callback);
        }
      }
    });
  }

  protected void parseFrontPageAndLogin(String idCardNumber, String password, WebClientResponse response, LoginCallback callback) {
    try {
      Element loginNavigationItemElement = findNavigationItemWithText(response.getBody(), ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT);
      if(loginNavigationItemElement == null) {
        callback.completed(new LoginResult("Konnte Link mit '" + ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT + "' nicht finden."));
      }
      else {
        String loginPageUrl = makeLinkAbsolute(loginNavigationItemElement.attr("href"));
        // TODO: may extract jsession id
        loadLoginPageAndLogin(idCardNumber, password, loginPageUrl, callback);
      }
    } catch(Exception e) {
      log.error("Could not parse Home page and login", e);
      callback.completed(new LoginResult("Konnte Homepage nicht parsen und mich einloggen: " + e.getLocalizedMessage()));
    }
  }

  protected void loadLoginPageAndLogin(final String idCardNumber, final String password, String loginPageUrl, final LoginCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(loginPageUrl);
    parameters.addHeader("Referer", "https://ssl.muenchen.de/aDISWeb/app?service=direct/0/Home/$DirectLink&sp=SOPAC"); // TODO: where to get from?

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new LoginResult(response.getError()));
        }
        else {
          parseLoginPageAndLogin(idCardNumber, password, response, callback);
        }
      }
    });
  }

  protected void parseLoginPageAndLogin(String idCardNumber, String password, WebClientResponse response, LoginCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());
      Element formElement = getPageFormElement(document);
      if(formElement != null) {
        String loginUrl = makeLinkAbsolute(formElement.attr("action"));

        Elements inputElements = formElement.select("input");
        String requestBody = "";

        for(Element inputElement : inputElements) {
          String type = inputElement.attr("type");
          String name = inputElement.attr("name");

          if("hidden".equals(type) || ("submit".equals(type) && "textButton".equals(name)) ) {
            requestBody += name + "=" + URLEncoder.encode(inputElement.attr("value"), "ASCII") + "&";
          }
          else if("text".equals(type)) {
            requestBody += name + "=" + URLEncoder.encode(idCardNumber, "ASCII") + "&";
          }
          else if("password".equals(type)) {
            requestBody += name + "=" + URLEncoder.encode(password, "ASCII") + "&";
          }
        }

        requestBody = removeLastAmpersand(requestBody);

        doLogin(loginUrl, requestBody, callback);
      }
    } catch(Exception e) {
      log.error("Could not parse Login page and login", e);
      callback.completed(new LoginResult("Konnte Anmeldeseite nicht parsen: " + e.getLocalizedMessage()));
    }
  }

  protected void doLogin(String loginUrl, String requestBody, final LoginCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(loginUrl, requestBody);
    parameters.addHeader("Referer", loginUrl);

    webClient.postAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new LoginResult(response.getError()));
        }
        else {
          parseLoginResult(response, callback);
        }
      }
    });
  }

  protected void parseLoginResult(WebClientResponse response, LoginCallback callback) {
    try {
      boolean loginSuccessful = false;
      Document document = Jsoup.parse(response.getBody());
      Elements header1Elements = document.select("h1");

      for(Element header1Element : header1Elements) {
        if(header1Element.text().startsWith("Sie sind angemeldet")) {
          loginSuccessful = true;
          break;
        }
      }

      if(loginSuccessful) {
        this.isLoggedIn = loginSuccessful;

        pressLoginSuccessPageOkButton(document, callback);
      }
      else {
        callback.completed(new LoginResult(loginSuccessful));
      }
    } catch(Exception e) {
      log.error("Could not parse login result", e);
      callback.completed(new LoginResult("Einloggen ist fehlgeschlagen: " + e.getLocalizedMessage()));
    }
  }

  protected void pressLoginSuccessPageOkButton(Document document, final LoginCallback callback) {
    try {
      Element formElement = getPageFormElement(document);
      if(formElement != null) {
        String confirmLoginUrl = makeLinkAbsolute(formElement.attr("action"));

        Elements inputElements = formElement.select("input");
        String requestBody = "";

        for (Element inputElement : inputElements) {
          String name = inputElement.attr("name");
          String value = inputElement.attr("value");

          requestBody += name + "=" + URLEncoder.encode(value, "ASCII") + "&";
        }

        requestBody = removeLastAmpersand(requestBody);

        webClient.postAsync(createRequestParametersWithDefaultValues(confirmLoginUrl, requestBody), new RequestCallback() {
          @Override
          public void completed(WebClientResponse response) {
            if(response.isSuccessful()) {
              StadtbibliothekMuenchenClient.this.frontPageHtml = response.getBody();
              // TODO: may save requestCount value
            }

            callback.completed(new LoginResult(true));
          }
        });
      }
    } catch (Exception e) {
      log.warn("Could not confirm log in, not severe", e);
    }
  }


  public void extendAllBorrowsAndGetBorrowsStateAsync(final ExtendAllBorrowsCallback callback) {
    if(isLoggedIn) {
      navigateToUserAccountAndExtendAllBorrows(callback);
    }
    else {
      loginAsync(new LoginCallback() {
        @Override
        public void completed(LoginResult result) {
          if(result.isSuccessful() == false) {
            callback.completed(new ExtendAllBorrowsResult(result.getError()));
          }
          else {
            navigateToUserAccountAndExtendAllBorrows(callback);
          }
        }
      });
    }
  }

  protected void navigateToUserAccountAndExtendAllBorrows(ExtendAllBorrowsCallback callback) {
    if(frontPageHtml == null) { // should never be the case at this point
      // TODO: load front page html
      return;
    }

    try {
      Element userAccountNavigationItemElement = findNavigationItemWithText(frontPageHtml, BENUTZERKONTO_NAVIGATION_ITEM_TEXT);
      if(userAccountNavigationItemElement == null) {
        callback.completed(new ExtendAllBorrowsResult("Konnte Link mit '" + BENUTZERKONTO_NAVIGATION_ITEM_TEXT + "' nicht finden."));
      }
      else {
        loadUserAccountPageAndExtendAllBorrows(userAccountNavigationItemElement, callback);
      }
    } catch(Exception e) {
      log.error("Could not navigate to User Account page", e);
      callback.completed(new ExtendAllBorrowsResult("Konnte nicht zur Benutzerkonte Seite navigieren: " + e.getLocalizedMessage()));
    }
  }

  protected void loadUserAccountPageAndExtendAllBorrows(Element userAccountNavigationItemElement, final ExtendAllBorrowsCallback callback) {
    String userAccountPageUrl = makeLinkAbsolute(userAccountNavigationItemElement.attr("href"));
    RequestParameters parameters = createRequestParametersWithDefaultValues(userAccountPageUrl);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new ExtendAllBorrowsResult(response.getError()));
        }
        else {
          parseUserAccountPageAndExtendAllBorrows(response, callback);
        }
      }
    });
  }

  protected void parseUserAccountPageAndExtendAllBorrows(WebClientResponse response, ExtendAllBorrowsCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());

      Element showAndExtendBorrowsElement = document.body().select("a[title='Ausleihen zeigen oder verl&#228;ngern']").first();
      if(showAndExtendBorrowsElement == null) {
        showAndExtendBorrowsElement = document.body().select("a[title='Ausleihen zeigen oder verlängern']").first();
      }

      if(showAndExtendBorrowsElement == null) {
        callback.completed(new ExtendAllBorrowsResult("Konnte auf der Benutzerkonte Seite den Link 'Ausleihen zeigen oder verlängern' nicht finden"));
      }
      else {
        loadShowAllBorrowsPageAndExtendAllBorrows(showAndExtendBorrowsElement, callback);
      }
    } catch(Exception e) {
      log.error("Could not parse User Account page", e);
      callback.completed(new ExtendAllBorrowsResult("Konnte Benutzerkonto Seite nicht parsen: " + e.getLocalizedMessage()));
    }
  }

  protected void loadShowAllBorrowsPageAndExtendAllBorrows(Element showAndExtendBorrowsElement, final ExtendAllBorrowsCallback callback) {
    String showAllBorrowsUrl = makeLinkAbsolute(showAndExtendBorrowsElement.attr("href"));
    RequestParameters parameters = createRequestParametersWithDefaultValues(showAllBorrowsUrl);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new ExtendAllBorrowsResult(response.getError()));
        }
        else {
          parseShowAllBorrowsPageAndExtendAllBorrows(response, callback);
        }
      }
    });
  }

  protected void parseShowAllBorrowsPageAndExtendAllBorrows(WebClientResponse response, ExtendAllBorrowsCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());

      Element formElement = getPageFormElement(document);
      String extendAllBorrowsUrl = makeLinkAbsolute(formElement.attr("action"));

      String requestBody = "";
      Elements inputElements = document.body().select("input");
      for(Element inputElement : inputElements) {
        String name = inputElement.attr("name");
        String type = inputElement.attr("type");

        if("hidden".equals(type) || ("submit".equals(type) && "textButton$0".equals(name))) {
          requestBody += name + "=" + URLEncoder.encode(inputElement.attr("value"), "ASCII") + "&";
        }
      }

      requestBody = removeLastAmpersand(requestBody);

      extendAllBorrows(extendAllBorrowsUrl, requestBody, callback);
    } catch(Exception e) {
      log.error("Could not parse show all borrows page", e);
      callback.completed(new ExtendAllBorrowsResult("Konnte Seite mit geliehenen Medien nicht parsen: " + e.getLocalizedMessage()));
    }
  }

  protected void extendAllBorrows(String extendAllBorrowsUrl, String requestBody, final ExtendAllBorrowsCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(extendAllBorrowsUrl, requestBody);

    webClient.postAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new ExtendAllBorrowsResult(response.getError()));
        }
        else {
          parseResponseToExtendAllBorrows(response, callback);
        }
      }
    });
  }

  protected void parseResponseToExtendAllBorrows(WebClientResponse response, ExtendAllBorrowsCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());
      Elements tableRowElements = document.body().select("tr.rTable_tr_even, tr.rTable_tr_odd");
      MediaBorrows borrows = new MediaBorrows();

      for(Element tableRowElement : tableRowElements) {
        MediaBorrow borrow = parseBorrowTableRow(tableRowElement);
        if(borrow != null) {
          borrows.addBorrow(borrow);
        }
      }

      borrows.setExpirations(borrowExpirationCalculator.calculateExpirations(borrows, userSettings));

      callback.completed(new ExtendAllBorrowsResult(borrows));
    } catch(Exception e) {
      log.error("Could not parse response to extend all borrows", e);
      callback.completed(new ExtendAllBorrowsResult("Konnte die Antwort auf 'Alle Medien verlängern' nicht parsen: " + e.getLocalizedMessage()));
    }
  }

  protected MediaBorrow parseBorrowTableRow(Element tableRowElement) {
    MediaBorrow borrow = new MediaBorrow();
    Elements tableDataElements = tableRowElement.select("td");

    for(int i = 0; i < tableDataElements.size(); i++) {
      Element tableDataElement = tableDataElements.get(i);

      if(i == BORROW_TABLE_DATA_INDEX_DUE_ON) {
        borrow.setDueOn(parseDueOnDate(tableDataElement.text()));
      }
      else if(i == BORROW_TABLE_DATA_INDEX_LIBRARY) {
        borrow.setLibrary(tableDataElement.text());
      }
      else if(i == BORROW_TABLE_DATA_INDEX_TITLE) {
        String mediaInfo = tableDataElement.html();
        borrow.setTitle(getTitleFromMediaInfo(mediaInfo));
        borrow.setAuthor(getAuthorFromMediaInfo(mediaInfo));
        borrow.setSignature(getSignatureFromMediaInfo(mediaInfo));
        borrow.setMediaNumber(getMediaNumberFromMediaInfo(mediaInfo));
      }
      else if(i == BORROW_TABLE_DATA_INDEX_NOTE) {
        borrow.setNote(tableDataElement.text());
        borrow.setCannotBeExtendedAnymore(getIfBorrowCannotBeExtendedAnymore(borrow.getNote()));
      }
    }

    if(borrow.areNecessaryInformationSet()) {
      return borrow;
    }
    return null;
  }

  protected String getTitleFromMediaInfo(String mediaInfo) {
    int indexOfSlash = mediaInfo.indexOf('/');

    if(indexOfSlash >= 0) {
      return urlDecodeString(mediaInfo.substring(0, indexOfSlash).trim());
    }

    return null;
  }

  protected String getAuthorFromMediaInfo(String mediaInfo) {
    int indexOfSlash = mediaInfo.indexOf('/') + 1;
    int indexOfBreakElement = mediaInfo.indexOf("<br>", indexOfSlash + 1);

    if(indexOfSlash >= 0 && indexOfBreakElement > indexOfSlash) {
      return urlDecodeString(mediaInfo.substring(indexOfSlash, indexOfBreakElement).trim());
    }

    return null;
  }

  protected String getSignatureFromMediaInfo(String mediaInfo) {
    int indexOfFirstBreakElement = mediaInfo.indexOf("<br>") + "<br>".length();
    int indexOfSecondBreakElement = mediaInfo.indexOf("<br>", indexOfFirstBreakElement + 1);

    if(indexOfFirstBreakElement >= 0 && indexOfSecondBreakElement > indexOfFirstBreakElement) {
      return mediaInfo.substring(indexOfFirstBreakElement, indexOfSecondBreakElement).trim();
    }

    return null;
  }

  protected String getMediaNumberFromMediaInfo(String mediaInfo) {
    int indexOfSecondBreakElement = mediaInfo.lastIndexOf("<br>") + "<br>".length();

    if(indexOfSecondBreakElement >= 0) {
      return mediaInfo.substring(indexOfSecondBreakElement).trim();
    }

    return null;
  }

  protected Date parseDueOnDate(String dueOnString) {
    try {
      return DueOnDateParser.parse(dueOnString);
    } catch(Exception e) {
      log.error("Could not parse due on date string " + dueOnString + " to a java.util.Date instance", e);
    }

    return null;
  }

  protected boolean getIfBorrowCannotBeExtendedAnymore(String note) {
    return note != null && note.startsWith("Max. "); // TODO: get real text, don't have an example available right nox
  }

  protected String urlDecodeString(String stringToDecode) {
    try {
      return Jsoup.parse(stringToDecode).text();
    } catch(Exception e) {
      log.error("Could not URL decode string " + stringToDecode, e);
    }

    return stringToDecode;
  }


  public void doSimpleSearchAsync(final String searchTerm, final SimpleSearchCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(HOMEPAGE_URL);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new SimpleSearchResponse(response.getError()));
        }
        else {
          parseHomepageAndNavigateToSimpleSearch(searchTerm, response, callback);
        }
      }
    });
  }

  protected void parseHomepageAndNavigateToSimpleSearch(String searchTerm, WebClientResponse response, SimpleSearchCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());
      Element simpleSearchTextInputElement = document.body().select("#schnellsuche").first();

      if(simpleSearchTextInputElement != null) { // we are already on simple search page
        executeSimpleSearch(searchTerm, response, callback);
      }
      else {
        Element simpleSearchNavigationItemElement = findNavigationItemWithText(response.getBody(), EINFACHE_SUCHE_NAVIGATION_ITEM_TEXT);
        if(simpleSearchNavigationItemElement == null) {
          callback.completed(new SimpleSearchResponse("Konnte Link mit '" + EINFACHE_SUCHE_NAVIGATION_ITEM_TEXT + "' nicht finden."));
        }
        else {
          navigateToAndExecuteSimpleSearch(searchTerm, simpleSearchNavigationItemElement, callback);
        }
      }
    } catch(Exception e) {
      log.error("Could not parse Homepage and navigate to simple search", e);
      callback.completed(new SimpleSearchResponse("Konnte nicht zur einfachen Suche navigieren: " + e.getLocalizedMessage()));
    }
  }

  protected void navigateToAndExecuteSimpleSearch(final String searchTerm, Element simpleSearchNavigationItemElement, final SimpleSearchCallback callback) {
    String simpleSearchUrl = makeLinkAbsolute(simpleSearchNavigationItemElement.attr("href"));
    RequestParameters parameters = new RequestParameters(simpleSearchUrl);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new SimpleSearchResponse(response.getError()));
        }
        else {
          executeSimpleSearch(searchTerm, response, callback);
        }
      }
    });
  }

  protected void executeSimpleSearch(String searchTerm, WebClientResponse response, SimpleSearchCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());

      Element formElement = getPageFormElement(document);
      String executeSimpleSearchUrl = makeLinkAbsolute(formElement.attr("action"));

      String requestBody = "";
      Elements inputElements = document.body().select("input");
      for(Element inputElement : inputElements) {
        String type = inputElement.attr("type");
        String name = inputElement.attr("name");

        if("hidden".equals(type) || ("submit".equals(type) && "textButton".equals(name)) ) {
          requestBody += name + "=" + URLEncoder.encode(inputElement.attr("value"), "ASCII") + "&";
        }
        else if("text".equals(type)) {
          requestBody += name + "=" + URLEncoder.encode(searchTerm, "ASCII") + "&";
        }
      }

      requestBody = removeLastAmpersand(requestBody);

      executeSimpleSearch(searchTerm, executeSimpleSearchUrl, requestBody, callback);
    } catch(Exception e) {
      log.error("Could not execute simple search on simple search page", e);
      callback.completed(new SimpleSearchResponse("Konnte Einfache Suche nicht ausführen: " + e.getLocalizedMessage()));
    }
  }

  protected void executeSimpleSearch(final String searchTerm, String executeSimpleSearchUrl, String requestBody, final SimpleSearchCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(executeSimpleSearchUrl, requestBody);

    webClient.postAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new SimpleSearchResponse(response.getError()));
        }
        else {
          parseSimpleSearchResults(searchTerm, response, callback);
        }
      }
    });
  }

  protected void parseSimpleSearchResults(String searchTerm, WebClientResponse response, SimpleSearchCallback callback) {
    try {
      SearchResults searchResults = new SearchResults();
      Document document = Jsoup.parse(response.getBody());
      Elements tableRowElements = document.body().select("tr.rTable_tr_even, tr.rTable_tr_odd");

      for(Element tableRowElement : tableRowElements) {
        SearchResult searchResult = parseSearchResultTableRow(tableRowElement);

        searchResults.addSearchResult(searchResult);
      }

      callback.completed(new SimpleSearchResponse(searchTerm, searchResults));
    } catch(Exception e) {
      log.error("Could not parse Simple Search result page", e);
      callback.completed(new SimpleSearchResponse("Konnte Ergebnisse der einfachen Suche nicht parsen: " + e.getLocalizedMessage()));
    }
  }

  protected SearchResult parseSearchResultTableRow(Element tableRowElement) {
    SearchResult searchResult = new SearchResult();

    for(int i = 0; i < tableRowElement.children().size(); i++) {
      Element tableDataElement = tableRowElement.child(i);

      if(i == SEARCH_RESULT_TABLE_DATA_INDEX_MEDIA_TYPE) {
        if(tableDataElement.children().size() > 0 && "img".equals(tableDataElement.child(0).nodeName())) {
          searchResult.setMediaTypeIconUrl(makeLinkAbsolute(tableDataElement.child(0).attr("src")));
        }
      }
      else if(i == SEARCH_RESULT_TABLE_DATA_INDEX_MEDIA_INFO) {
        if (tableDataElement.children().size() > 0 && "a".equals(tableDataElement.child(0).nodeName())) {
          searchResult.setMediaInfo(tableDataElement.child(0).text());
          searchResult.setMediaDetailsToken(getMediaDetailsUrl(tableDataElement.child(0)));
          searchResult.setSearchResultsDocument(tableDataElement.ownerDocument());
        }
      }
      else if(i == SEARCH_RESULT_TABLE_DATA_INDEX_AVAILABILITY) {
        if (tableDataElement.children().size() > 0 && "span".equals(tableDataElement.child(0).nodeName())) {
          Element spanElement = tableDataElement.child(0);
          if (spanElement.children().size() > 0 && "img".equals(spanElement.child(0).nodeName())) {
            searchResult.setAvailabilityIconUrl(makeLinkAbsolute(spanElement.child(0).attr("src")));
          }
        }
      }
      else if(i == SEARCH_RESULT_TABLE_DATA_INDEX_YEAR) {
        searchResult.setYear(tableDataElement.text());
      }
    }

    return searchResult;
  }

  protected String getMediaDetailsUrl(Element searchResultAnchorElement) {
    String mediaDetailsUrl = searchResultAnchorElement.attr("href");

    mediaDetailsUrl = mediaDetailsUrl.replace("javascript:htmlOnLink('", "");
    mediaDetailsUrl = mediaDetailsUrl.replace("')", "");

    return mediaDetailsUrl;
  }


  protected Element getPageFormElement(Document document) {
    return document.body().select("form[name='" + FORM_NAME + "'").first();
  }

  protected Element findNavigationItemWithText(String frontPageHtml, String navigationItemText) {
    Document document = Jsoup.parse(frontPageHtml);
    Elements treeLeafAnchors = document.body().select("a.tree_leaf_a");

    for(Element treeLeafAnchor : treeLeafAnchors) {
      if(navigationItemText.equals(treeLeafAnchor.text())) {
        return treeLeafAnchor;
      }
    }

    return null;
  }

  protected String makeLinkAbsolute(String url) {
    if(url.startsWith("http") == false) {
      url = HOMEPAGE_URL + url;
    }

    return url;
  }

  protected String removeLastAmpersand(String requestBody) {
    if(requestBody.length() > 0) {
      requestBody = requestBody.substring(0, requestBody.length() - 1); // cut final '&'
    }
    return requestBody;
  }

  protected RequestParameters createRequestParametersWithDefaultValues(String url) {
    RequestParameters parameters = new RequestParameters(url);

    parameters.setConnectionTimeoutMillis(DOWNLOAD_CONNECTION_TIMEOUT_MILLIS);
    parameters.setCountConnectionRetries(COUNT_CONNECTION_RETRIES);

//    parameters.addHeader("Accept-Language", "en-US,en;q=0.8");

    return parameters;
  }

  protected RequestParameters createRequestParametersWithDefaultValues(String url, String requestBody) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(url);

    parameters.setBody(requestBody);

    return parameters;
  }

}
