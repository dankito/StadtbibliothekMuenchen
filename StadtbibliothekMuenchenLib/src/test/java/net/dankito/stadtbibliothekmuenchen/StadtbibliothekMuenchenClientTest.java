package net.dankito.stadtbibliothekmuenchen;

import net.dankito.stadtbibliothekmuenchen.util.web.OkHttpWebClient;
import net.dankito.stadtbibliothekmuenchen.util.web.callbacks.LoginCallback;
import net.dankito.stadtbibliothekmuenchen.util.web.responses.LoginResult;

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

  protected static final String TEST_DATA_FILENAME = "testdata.properties";

  protected static final String TEST_DATA_KEY_USER_IDENTITY_CARD_NUMBER = "test.user.identity.card.number";

  protected static final String TEST_DATA_KEY_USER_PASSWORD = "test.user.password";


  protected StadtbibliothekMuenchenClient underTest;

  protected Properties testDataProperties = null;


  @Before
  public void setUp() throws Exception {
    this.testDataProperties = loadTestDataProperties();

    this.underTest = new StadtbibliothekMuenchenClient(new OkHttpWebClient());
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void loginAsync() throws Exception {
    final List<LoginResult> resultList = new ArrayList<>();
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    underTest.loginAsync(getTestUserIdentityCardNumber(), getTestUserPassword(), new LoginCallback() {
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