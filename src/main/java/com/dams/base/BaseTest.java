package com.dams.base;

import com.dams.driver.DriverFactory;
import com.dams.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners(com.dams.report.ExtentListener.class)
public class BaseTest {
    protected WebDriver driver;
    protected LoginPage loginPage;

    @org.testng.annotations.BeforeSuite
    public void setupReport() {
        com.dams.report.CustomHtmlReporter.initializeReport();
    }

    @BeforeClass
    public void setUp() {
        // Initialize the browser
        driver = DriverFactory.initDriver();
        System.out.println("Navigating to https://devadmin.damsdelhi.com/");
        driver.get("https://devadmin.damsdelhi.com/");
        com.dams.report.CustomHtmlReporter.logStep("Login Phase", "TC_0", "Init", "STEP 1 - Open Dashboard", "PASS", "-");
        
        // Initialize the Page Objects
        loginPage = new LoginPage(driver);
        
        // Execute the login flow (happens before every test inheriting BaseTest)
        loginPage.login("07siddwivedi@gmail.com", "Siddarth@123", "1980");
        
        // Pause for 1 minute (60,000 milliseconds) as requested to allow heavy dashboard elements to become visible
        System.out.println("Login completed. Waiting 1 minute for dashboard to fully render...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @org.testng.annotations.AfterSuite
    public void finalizeReport() {
        com.dams.report.CustomHtmlReporter.generateReport();
    }
}
