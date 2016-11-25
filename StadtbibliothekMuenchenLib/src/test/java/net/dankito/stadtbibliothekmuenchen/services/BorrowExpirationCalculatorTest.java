package net.dankito.stadtbibliothekmuenchen.services;

import net.dankito.stadtbibliothekmuenchen.model.BorrowExpirations;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrow;
import net.dankito.stadtbibliothekmuenchen.model.MediaBorrows;
import net.dankito.stadtbibliothekmuenchen.model.UserSettings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by ganymed on 25/11/16.
 */
public class BorrowExpirationCalculatorTest {

  protected BorrowExpirationCalculator underTest;

  protected UserSettings userSettings;


  @Before
  public void setUp() throws Exception {
    underTest = new BorrowExpirationCalculator();

    userSettings = createTestUserSettings();
  }


  @Test
  public void alreadyExpired_GetCorrectlyDetected() throws Exception {
    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(-1);
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(-2);
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(-3);
    borrows.addBorrow(borrow03);
    MediaBorrow borrow04 = createTestBorrow(1);
    borrows.addBorrow(borrow04);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertTrue(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(3, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(1, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForFirstWarning().size());
  }


  @Test
  public void expiringForThirdWarning_ThirdWarningsEnabled_GetCorrectlyDetected() throws Exception {
    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow03);
    MediaBorrow borrow04 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning() + 1);
    borrows.addBorrow(borrow04);
    MediaBorrow borrow05 = createTestBorrow(0);
    borrows.addBorrow(borrow05);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertTrue(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(1, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(3, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(1, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForFirstWarning().size());
  }

  @Test
  public void expiringForThirdWarning_ThirdWarningsDisabled_NothingGetsDetected() throws Exception {
    userSettings.setThirdWarningEnabled(false);

    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow03);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertFalse(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(0, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(3, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForFirstWarning().size());
  }


  @Test
  public void expiringForSecondWarning_SecondWarningsEnabled_GetCorrectlyDetected() throws Exception {
    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning() - 1);
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning() + 1);
    borrows.addBorrow(borrow03);
    MediaBorrow borrow04 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning());
    borrows.addBorrow(borrow04);
    MediaBorrow borrow05 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning() + 1);
    borrows.addBorrow(borrow05);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertFalse(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(0, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(1, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(3, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(1, expirations.getBorrowExpirationsForFirstWarning().size());
  }

  @Test
  public void expiringForSecondWarning_SecondWarningsDisabled_NothingGetsDetected() throws Exception {
    userSettings.setSecondWarningEnabled(false);

    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning() - 1);
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForThirdWarning() + 1);
    borrows.addBorrow(borrow03);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertFalse(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(0, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(3, expirations.getBorrowExpirationsForFirstWarning().size());
  }


  @Test
  public void expiringForFirstWarning_FirstWarningsEnabled_NothingGetsDetected() throws Exception {
    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning() - 1);
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning() - 2);
    borrows.addBorrow(borrow03);
    MediaBorrow borrow04 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForSecondWarning());
    borrows.addBorrow(borrow04);
    MediaBorrow borrow05 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning() + 1);
    borrows.addBorrow(borrow05);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertFalse(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(0, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(1, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertTrue(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(3, expirations.getBorrowExpirationsForFirstWarning().size());
  }


  @Test
  public void expiringForFirstWarning_FirstWarningsDisabled_NothingGetsDetected() throws Exception {
    userSettings.setFirstWarningEnabled(false);

    MediaBorrows borrows = new MediaBorrows();

    MediaBorrow borrow01 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning());
    borrows.addBorrow(borrow01);
    MediaBorrow borrow02 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning() - 1);
    borrows.addBorrow(borrow02);
    MediaBorrow borrow03 = createTestBorrow(userSettings.getCountDaysBeforeExpirationForFirstWarning() - 2);
    borrows.addBorrow(borrow03);

    BorrowExpirations expirations = underTest.calculateExpirations(borrows, userSettings);

    Assert.assertFalse(expirations.hasAlreadyExpiredBorrows());
    Assert.assertEquals(0, expirations.getAlreadyExpiredBorrows().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForThirdWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForThirdWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForSecondWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForSecondWarning().size());

    Assert.assertFalse(expirations.hasBorrowExpirationForFirstWarning());
    Assert.assertEquals(0, expirations.getBorrowExpirationsForFirstWarning().size());
  }


  protected UserSettings createTestUserSettings() {
    UserSettings userSettings = new UserSettings();

    userSettings.setFirstWarningEnabled(true);
    userSettings.setSecondWarningEnabled(true);

    return userSettings;
  }

  protected MediaBorrow createTestBorrow(int dueOnInCountDays) {
    MediaBorrow borrow = new MediaBorrow();
    Date todayAtMidnight = underTest.getTodayAtMidnightDate();

    borrow.setDueOn(new Date(todayAtMidnight.getTime() + dueOnInCountDays * 24 * 60 * 60 * 1000));

    borrow.setTitle("");
    borrow.setAuthor("");

    return borrow;
  }

}