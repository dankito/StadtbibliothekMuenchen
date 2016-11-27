package net.dankito.stadtbibliothekmuenchen.services;

import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;
import net.dankito.stadtbibliothekmuenchen.model.SearchResult;
import net.dankito.stadtbibliothekmuenchen.model.SearchResults;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;
import net.dankito.stadtbibliothekmuenchen.util.StringUtils;
import net.dankito.stadtbibliothekmuenchen.util.web.OkHttpWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.ExtendAllBorrowsCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.SimpleSearchCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.ExtendAllBorrowsResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.SimpleSearchResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ganymed on 24/11/16.
 */
public class StadtbibliothekMuenchenClientTest {

  protected static final String TEST_DATA_FILENAME = "test_data.properties";

  protected static final String TEST_DATA_KEY_USER_IDENTITY_CARD_NUMBER = "test.user.identity.card.number";

  protected static final String TEST_DATA_KEY_USER_PASSWORD = "test.user.password";


  protected static final String TEST_SIMPLE_SEARCH_SEARCH_TERM = "goethe faust";


  protected StadtbibliothekMuenchenClient underTest;

  protected Properties testDataProperties = null;


  @Before
  public void setUp() throws Exception {
    this.testDataProperties = loadTestDataProperties();

    UserSettings userSettings = new UserSettings();
    userSettings.setIdentityCardNumber(getTestUserIdentityCardNumber());
    userSettings.setPassword(getTestUserPassword());

    this.underTest = new StadtbibliothekMuenchenClient(new OkHttpWebClient(), new BorrowExpirationCalculator(), userSettings);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void loginAsync() throws Exception {
    final List<LoginResult> resultList = new ArrayList<>();
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    underTest.loginAsync(new LoginCallback() {
      @Override
      public void completed(LoginResult result) {
        resultList.add(result);
        countDownLatch.countDown();
      }
    });

    try { countDownLatch.await(5, TimeUnit.MINUTES); } catch(Exception ignored) { }

    Assert.assertEquals(1, resultList.size());

    LoginResult result = resultList.get(0);
    Assert.assertTrue(result.isSuccessful());
  }

  @Test
  public void extendAllBorrowsAsync_WithoutLoggingInBefore_Succeeds() throws Exception {
    final List<ExtendAllBorrowsResult> resultList = new ArrayList<>();
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    underTest.extendAllBorrowsAndGetBorrowsStateAsync(new ExtendAllBorrowsCallback() {
      @Override
      public void completed(ExtendAllBorrowsResult result) {
        resultList.add(result);
        countDownLatch.countDown();
      }
    });

    try { countDownLatch.await(5, TimeUnit.MINUTES); } catch(Exception ignored) { }

    Assert.assertEquals(1, resultList.size());

    ExtendAllBorrowsResult result = resultList.get(0);
    Assert.assertTrue(result.isSuccessful());
    Assert.assertNotNull(result.getBorrows());

    MediaBorrows borrows = result.getBorrows();
    Assert.assertTrue(borrows.getBorrows().size() > 0); // TODO: this cannot always be satisfied (i don't have always borrowed books or other media)

    for(MediaBorrow borrow : borrows.getBorrows()) {
      Assert.assertTrue(borrow.areNecessaryInformationSet());
      Assert.assertNotNull(borrow.getTitle());
      Assert.assertNotNull(borrow.getDueOn());
      Assert.assertNotNull(borrow.getLibrary());
      Assert.assertNotNull(borrow.getMediaNumber());
    }
  }


  @Test
  public void doSimpleSearchAsync() {
    final List<SimpleSearchResponse> responseList = new ArrayList<>();
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    underTest.doSimpleSearchAsync(TEST_SIMPLE_SEARCH_SEARCH_TERM, new SimpleSearchCallback() {
      @Override
      public void completed(SimpleSearchResponse response) {
        responseList.add(response);
        countDownLatch.countDown();
      }
    });

    try { countDownLatch.await(5, TimeUnit.MINUTES); } catch(Exception ignored) { }

    Assert.assertEquals(1, responseList.size());

    SimpleSearchResponse response = responseList.get(0);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(TEST_SIMPLE_SEARCH_SEARCH_TERM, response.getSearchTerm());
    Assert.assertNotNull(response.getSearchResults());

    SearchResults searchResults = response.getSearchResults();
    Assert.assertEquals(StadtbibliothekMuenchenClient.COUNT_SEARCH_RESULTS_PER_PAGE, searchResults.getResults().size());

    for(SearchResult searchResult : searchResults.getResults()) {
      Assert.assertTrue(StringUtils.isNotNullOrEmpty(searchResult.getMediaInfo()));
      Assert.assertTrue(StringUtils.isNotNullOrEmpty(searchResult.getMediaDetailsToken()));
      Assert.assertNotNull(searchResult.getSearchResultsDocument());
      Assert.assertTrue(StringUtils.isNotNullOrEmpty(searchResult.getYear()));
      Assert.assertTrue(StringUtils.isNotNullOrEmpty(searchResult.getMediaTypeIconUrl()));
      Assert.assertTrue(StringUtils.isNotNullOrEmpty(searchResult.getAvailabilityIconUrl()));
    }
  }


  protected Properties loadTestDataProperties() throws IOException {
    InputStream testDataInputStream = getClass().getClassLoader().getResourceAsStream(TEST_DATA_FILENAME);
    Properties testDataProperties = new Properties();
    testDataProperties.load(testDataInputStream);
    return testDataProperties;
  }

  protected String getTestUserIdentityCardNumber() {
    return testDataProperties.getProperty(TEST_DATA_KEY_USER_IDENTITY_CARD_NUMBER);
  }

  protected String getTestUserPassword() {
    return testDataProperties.getProperty(TEST_DATA_KEY_USER_PASSWORD);
  }

}