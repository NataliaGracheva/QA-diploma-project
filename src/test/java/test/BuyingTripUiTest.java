package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.Card;
import data.DataGenerator;

import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.CreditPage;
import page.PaymentPage;
import page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;


public class BuyingTripUiTest {

    @BeforeEach
    public void openPage() {
        String url = System.getProperty("sut.url");
        open(url);
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
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
    void shouldShowWarningIfValueIsIncorrectForCredit(String number, String month, String year, String owner, String cvc, String warning, String message) {
        Card incorrectValues = new Card(number, month, year, owner, cvc);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(incorrectValues);
        assertTrue(creditPage.inputInvalidIsVisible(), message);
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок карты истек, страница оплаты")
    void shouldShowWarningIfCardIsExpiredForPayment() {
        Card expiredCard = DataGenerator.getInvalidExpDateCard(-1);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(expiredCard);
        assertTrue(paymentPage.inputInvalidIsVisible(),"Должен показывать сообщение об ошибке, если срок карты истек, страница оплаты");
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок карты истек, страница кредита")
    void shouldShowWarningIfCardIsExpiredForCredit() {
        Card expiredCard = DataGenerator.getInvalidExpDateCard(-1);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(expiredCard);
        assertTrue(creditPage.inputInvalidIsVisible(),"Должен показывать сообщение об ошибке, если срок карты истек, страница кредита");
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок действия карты более 5 лет, страница оплаты")
    void shouldShowWarningIfExpirationDateMoreThan5YearsForPayment() {
        Card invalidExpDateCard = DataGenerator.getInvalidExpDateCard(61);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(invalidExpDateCard);
        assertTrue(paymentPage.inputInvalidIsVisible(),"Должен показывать сообщение об ошибке, если срок действия карты более 5 лет, страница оплаты");
    }

    @Test
    @DisplayName("Должен показывать сообщение об ошибке, если срок действия карты более 5 лет, страница кредита")
    void shouldShowWarningIfExpirationDateMoreThan5YearsForCredit() {
        Card invalidExpDateCard = DataGenerator.getInvalidExpDateCard(61);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(invalidExpDateCard);
        assertTrue(creditPage.inputInvalidIsVisible(),"Должен показывать сообщение об ошибке, если срок действия карты более 5 лет, страница кредита");
    }
}
