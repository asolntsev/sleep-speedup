package org.selenide.examples.sleepspeedup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static java.time.temporal.ChronoUnit.SECONDS;

@ExtendWith(TextReportExtension.class)
public class DeclarationFormTest {
  private static final Logger log = LoggerFactory.getLogger(DeclarationFormTest.class);
  private static final String HS_CODE_SHOES = "6402999800";
  private static final String HS_CODE_BOOKS = "4901990000";

  @BeforeAll
  static void beforeAll() {
    Configuration.baseUrl = "https://test.alldeclare.eurora.io";
    Configuration.fastSetValue = true;
    Configuration.reportsFolder = "target/test-results";
  }

  @BeforeEach
  void login() {
    open("/");
    $("#startLoginButton").click();
    $("#userEmail").val("andrei@codeborne.com");
    $("#userPassword").val("00000000");
    $("#loginButton").click();
    $("#new-declaration-button").shouldHave(text("new")).click();
  }

  @Test
  void userCanSubmitDeclaration() {
    $("#declaration-form-title").shouldBe(visible).shouldHave(text("Declare your parcel"));
    $("#trackingNumber").val("111-11-111-1111");
    $("#exporterName").val("Adidas");
    $("#exporterStreetAndNumber").val("Roosevelt avenue 43");
    $("#exporterCity").val("Tehran");
    $("#exporterPostcode").val("11415");
    $("#exporterCountry").val("CD");
    $("#courierSelect").val("Omniva");
    
    fillItem0();

    $("#add-item-button").shouldHave(text("add more goods")).click();

    fillItem1();

    $("#sendingCost").val("5.00");
    $("#transportationCurrency").val("EUR");

    $("#previousDocReference").val("previous doc 01");
    $("#purchaseDocumentReference").val("arve 002");

    // Ironically, this sleep makes test faster
    // sleep(300);

    WebDriver webDriver = run("get webdriver", WebDriverRunner::getWebDriver);
    WebElement checkbox = run("find element", () -> webDriver.findElement(By.cssSelector("#ioss-checkbox")));

    try {
      run("first click", () -> checkbox.click());
    }
    catch (ElementClickInterceptedException e) {
      log.error("First click failed", e);
      run("second click", () -> checkbox.click());
    }
    $("#vatIdentificationNumber").val("ioss 002");
    $("#fileUpload").uploadFromClasspath("invoice.txt");

    $("#receiverName").shouldHave(value("Andrei Solntsev").because("receiver name should be prefilled"));
    $("#importerStreetAndNumber").val("Liivakitsa 67");
    $("#importerCity").val("Lihula");
    $("#importerPostcode").val("99988");
    $("#importerCountry").shouldHave(value("EE"));

    $("#submit-new-declaration-button").shouldHave(text("submit to customs")).click();

    $("#overview-title").shouldBe(visible).shouldHave(text("Your declarations"));
  }

  private void fillItem0() {
    $("#goodsItem_0_description").val("Shoes");
    $("#goodsItem_0_hscode_radio_parcelHsCode_0").shouldHave(value(HS_CODE_SHOES), Duration.of(10, SECONDS));
    $("#item0numberOfItems").val("2");
    $("#item0netMass").val("1.1");
    $("#item0goodsPrice").val("100.99");
    $("#item0Currency").val("BGN");
  }

  private void fillItem1() {
    $("#goodsItem_1_description").val("Books");
    $("#goodsItem_1_hscode_radio_parcelHsCode_0").shouldHave(value(HS_CODE_BOOKS));
    $("#item1numberOfItems").val("3");
    $("#item1netMass").val("1");
    $("#item1goodsPrice").val("1.88");
    $("#item1Currency").val("PHP");
  }

  private void run(String name, Runnable block) {
    long start = System.currentTimeMillis();
    try {
      block.run();
    }
    finally {
      log.info("{}: {} ms.", name, System.currentTimeMillis() - start);
    }
  }

  private <T> T run(String name, Supplier<T> block) {
    long start = System.currentTimeMillis();
    try {
      return block.get();
    }
    finally {
      log.info("{}: {} ms.", name, System.currentTimeMillis() - start);
    }
  }
}
