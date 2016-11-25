package net.dankito.stadtbibliothekmuenchen;

import net.dankito.stadtbibliothekmuenchen.util.web.IWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.RequestCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.RequestParameters;
import net.dankito.stadtbibliothekmuenchen.util.web.WebClientResponse;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created by ganymed on 24/11/16.
 */

public class StadtbibliothekMuenchenClient {

  protected static final String HOMEPAGE_URL = "https://ssl.muenchen.de";

  protected static final String ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT = "Anmelden / Login";

  protected static final String FORM_NAME = "Form0";


  public static final int DOWNLOAD_CONNECTION_TIMEOUT_MILLIS = 2000;

  public static final int COUNT_CONNECTION_RETRIES = 2;


  private static final Logger log = LoggerFactory.getLogger(StadtbibliothekMuenchenClient.class);


  protected IWebClient webClient;

  protected boolean isLoggedIn = false;


  public StadtbibliothekMuenchenClient(IWebClient webClient) {
    this.webClient = webClient;
  }


  public void loginAsync(final String idCardNumber, final String password, final LoginCallback callback) {
    RequestParameters parameters = createRequestParametersWithDefaultValues(HOMEPAGE_URL);

    webClient.getAsync(parameters, new RequestCallback() {
      @Override
      public void completed(WebClientResponse response) {
        if(response.isSuccessful() == false) {
          callback.completed(new LoginResult(response.getError()));
        }
        else {
          parseFrontPageAndLogin(idCardNumber, password, response, callback);
        }
      }
    });
  }

  protected void parseFrontPageAndLogin(String idCardNumber, String password, WebClientResponse response, LoginCallback callback) {
    try {
      Document document = Jsoup.parse(response.getBody());
      Elements debug = document.body().select("a.tree_leaf_a:contains('" + ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT + "')");
      Elements treeLeafAnchors = document.body().select("a.tree_leaf_a");

      for(Element treeLeafAnchor : treeLeafAnchors) {
        if(ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT.equals(treeLeafAnchor.text())) {
          String loginPageUrl = makeLinkAbsolute(treeLeafAnchor.attr("href"));
          // TODO: may extract jsession id
          loadLoginPageAndLogin(idCardNumber, password, loginPageUrl, callback);
          return;
        }
      }

      callback.completed(new LoginResult("Konnte Link mit '" + ANMELDEN_LOGIN_NAVIGATION_ITEM_TEXT + "' nicht finden."));
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
      Element formElement = document.body().select("form[name='" + FORM_NAME + "'").first();
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

        if(requestBody.length() > 0) {
          requestBody = requestBody.substring(0, requestBody.length() - 1); // cut final '&'
        }

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
        pressLoginSuccessPageOkButton(document);
      }

      this.isLoggedIn = loginSuccessful;

      callback.completed(new LoginResult(loginSuccessful));
    } catch(Exception e) {
      log.error("Could not parse login result", e);
      callback.completed(new LoginResult("Einloggen ist fehlgeschlagen: " + e.getLocalizedMessage()));
    }
  }

  protected void pressLoginSuccessPageOkButton(Document document) {
    try {
      Element formElement = document.body().select("form[name='" + FORM_NAME + "'").first();
      if(formElement != null) {
        String confirmLoginUrl = makeLinkAbsolute(formElement.attr("action"));

        Elements inputElements = formElement.select("input");
        String requestBody = "";

        for (Element inputElement : inputElements) {
          String name = inputElement.attr("name");
          String value = inputElement.attr("value");

          requestBody += name + "=" + URLEncoder.encode(value, "ASCII") + "&";
        }

        if(requestBody.length() > 0) {
          requestBody = requestBody.substring(0, requestBody.length() - 1); // cut final '&'
        }

        webClient.postAsync(createRequestParametersWithDefaultValues(confirmLoginUrl, requestBody), new RequestCallback() {
          @Override
          public void completed(WebClientResponse response) {
            // ignore for now // TODO: may save requestCount value
          }
        });
      }
    } catch (Exception e) {
      log.warn("Could not confirm log in, not severe", e);
    }
  }


  protected String makeLinkAbsolute(String url) {
    if(url.startsWith("http") == false) {
      url = HOMEPAGE_URL + url;
    }

    return url;
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
