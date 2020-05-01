package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.ApiUtils;
import data.Card;
import data.DataGenerator;
import data.DbUtils;

import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.CreditPage;
import page.PaymentPage;
import page.StartPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;


public class BuyingTripTest {

    Card validCard = DataGenerator.getValidCard();
    Card declinedCard = DataGenerator.getDeclinedCard();
    Card fakeCard = DataGenerator.getFakeCard();
    Card invalidHolderCard = DataGenerator.getInvalidHolderCard();

    @BeforeEach
    public void openPage() {
        open("http://localhost:8080/");
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Должен подтверждать покупку по карте со статусом APPROVED")
    void shouldConfirmPaymentWithValidCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(validCard);
        paymentPage.notificationOkIsVisible();
        assertEquals("APPROVED", DbUtils.findPaymentStatus());
    }

    @Test
    @DisplayName("Должен подтверждать кредит по карте со статусом APPROVED")
    void shouldConfirmCreditWithValidCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(validCard);
        creditPage.notificationOkIsVisible();
        assertEquals("APPROVED", DbUtils.findCreditStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку по карте со статусом DECLINED")
    void shouldNotConfirmPaymentWithDeclinedCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(declinedCard);
        paymentPage.notificationErrorIsVisible();
        assertEquals("DECLINED", DbUtils.findPaymentStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит по карте со статусом DECLINED")
    void shouldNotConfirmCreditWithDeclinedCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(declinedCard);
        creditPage.notificationErrorIsVisible();
        assertEquals("DECLINED", DbUtils.findCreditStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку по несуществующей карте")
    void shouldNotConfirmPaymentWithFakeCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(fakeCard);
        paymentPage.notificationErrorIsVisible();
        assertEquals("0", DbUtils.countRecords());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит по несуществующей карте")
    void shouldNotConfirmCreditWithFakeCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(fakeCard);
        creditPage.notificationErrorIsVisible();
        assertEquals("0", DbUtils.countRecords());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/incorrectValues.cvs", numLinesToSkip = 1)
    @DisplayName("Должен показывать сообщение об ошибке при заполнении полей невалидными значениями")
    void shouldShowWarningIfValueIsIncorrectForPayment(String number, String month, String year, String owner, String cvc, String message) {
        Card incorrectValuesCard = new Card(number, month, year, owner, cvc);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(incorrectValuesCard);
        assertTrue(paymentPage.inputInvalidIsVisible(), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/incorrectValues.cvs", numLinesToSkip = 1)
    @DisplayName("Должен показывать сообщение об ошибке при заполнении полей невалидными значениями")
    void shouldShowWarningIfValueIsIncorrectForCredit(String number, String month, String year, String owner, String cvc, String message) {
        Card incorrectValues = new Card(number, month, year, owner, cvc);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(incorrectValues);
        assertTrue(creditPage.inputInvalidIsVisible(), message);
    }

    @Test
    @DisplayName("Не должен отправлять запрос на оплату с некорректным именем владельца")
    void shouldNotSendPaymentRequestWithIncorrectName() {
        int statusCode = ApiUtils.getRequestStatusCode(invalidHolderCard, "/api/v1/pay");
        assertNotEquals(200, statusCode);
        // Request URL: http://localhost:8080/api/v1/pay
    }

    @Test
    @DisplayName("Не должен отправлять запрос на кредит с некорректным именем владельца")
    void shouldNotSendCreditRequestWithIncorrectName() {
        int statusCode = ApiUtils.getRequestStatusCode(invalidHolderCard, "/api/v1/credit");
        assertNotEquals(200, statusCode);
        // Request URL: http://localhost:8080/api/v1/credit
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок карты истек")
    void shouldShowWarningIfCardIsExpiredForPayment() {
        Card expiredCard = DataGenerator.getInvalidExpDateCard(-1);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(expiredCard);
        assertTrue(paymentPage.inputInvalidIsVisible());
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок карты истек")
    void shouldShowWarningIfCardIsExpiredForCredit() {
        Card expiredCard = DataGenerator.getInvalidExpDateCard(-1);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(expiredCard);
        assertTrue(creditPage.inputInvalidIsVisible());
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок действия карты более 5 лет")
    void shouldShowWarningIfExpirationDateMoreThan5YearsForPayment() {
        Card invalidExpDateCard = DataGenerator.getInvalidExpDateCard(61);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(invalidExpDateCard);
        assertTrue(paymentPage.inputInvalidIsVisible());
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок действия карты более 5 лет")
    void shouldShowWarningIfExpirationDateMoreThan5YearsForCredit() {
        Card invalidExpDateCard = DataGenerator.getInvalidExpDateCard(61);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(invalidExpDateCard);
        assertTrue(creditPage.inputInvalidIsVisible());
    }
}
