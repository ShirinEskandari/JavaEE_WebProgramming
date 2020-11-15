package com.ga4w20.selenium.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Selenium test for all parts of index page
 *
 * @author Shirin
 */
public class IndexTestIT {

    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new ChromeDriver();
    }

    /**
     * click on login button and goes to login.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testLogin() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("seleniumtest"));

        driver.findElements(By.className("ui simple dropdown icon item"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("dropdown")).click();

        driver.findElement(By.linkText("Login")).click();

        wait.until(ExpectedConditions.titleIs("Bookazon Login"));

    }

    /**
     * click on register button and goes to registration.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testRegister() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("seleniumtest"));

        driver.findElements(By.className("ui simple dropdown icon item"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("dropdown")).click();

        driver.findElement(By.linkText("Register")).click();

        wait.until(ExpectedConditions.titleIs("Bookazon Register"));

    }

    /**
     * click on Category button and goes to Genre.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void tesBookazonCategory() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("seleniumtest"));

        driver.findElements(By.className("ui simple dropdown icon item"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("bookazondropdown")).click();

        driver.findElement(By.id("cat")).click();
        driver.findElement(By.id("sci")).click();
        wait.until(ExpectedConditions.titleIs("Bookazon: Genre"));

    }

    /**
     * click on language button and it changes the language to french
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void tesBookazonLanguage() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("seleniumtest"));

        driver.findElements(By.className("ui simple dropdown icon item"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("bookazondropdown")).click();

        driver.findElement(By.id("lang")).click();
        driver.findElement(By.id("french")).click();
        wait.until(ExpectedConditions.titleIs("Bookazon"));

    }

    /**
     * click on Faq button and goes to faq.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void tesBookazonFaq() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("seleniumtest"));

        driver.findElements(By.className("ui simple dropdown icon item"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.findElement(By.id("bookazondropdown")).click();

        driver.findElement(By.linkText("FAQ")).click();

        wait.until(ExpectedConditions.titleIs("Bookazon: FAQ"));

    }

    /**
     * click on checkout button and goes to checkout.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testCheckout() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(By.id("seleniumtest"));
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.titleIs("Bookazon Checkout"));
    }

    /**
     * it chooses radio button for the search then writes Ken in the search box,
     * result of search will shown in genre.xhtml
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testSearch() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(By.id("seleniumtest"));
        driver.findElement(By.id("rightselenium"));
        driver.findElement(By.id("sel"));
        driver.findElement(By.id("txtinput:radio:0")).click();
        WebElement inputElement = driver.findElement(By.id("txtinput:search"));
        inputElement.clear();
        inputElement.sendKeys("ken", Keys.ENTER);
        wait.until(ExpectedConditions.titleIs("Bookazon: Genre"));
    }

    /**
     * click on view button and it opens page to view a book
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testViewBook() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElements(By.className("card"));
        driver.findElements(By.className("extra content"));
        driver.findElement(By.id("viewbook")).click();

    }

    /**
     * click on add button and goes to dawscon link
     *
     * @throws Exception
     * @author Shirin
     */
    @Test
    public void testAdd() throws Exception {

        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ga4w20/");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.titleIs("Bookazon"));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(By.id("dawscon")).click();
        wait.until(ExpectedConditions.titleIs("Bookazon"));

    }

    @After
    public void shutdownTest() {

        driver.quit();
    }
}
