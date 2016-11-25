package net.dankito.stadtbibliothekmuenchen.util.web;

/**
 * Created by ganymed on 03/11/16.
 */

public interface IWebClient {

  WebClientResponse get(RequestParameters parameters);

  void getAsync(RequestParameters parameters, final RequestCallback callback);

  WebClientResponse post(RequestParameters parameters);

  void postAsync(RequestParameters parameters, final RequestCallback callback);

  WebClientResponse head(RequestParameters parameters);

  void headAsync(RequestParameters parameters, final RequestCallback callback);

}
