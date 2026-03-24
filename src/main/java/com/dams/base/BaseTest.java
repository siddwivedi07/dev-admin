package com.dams.base;

import com.dams.driver.DriverFactory;
import com.dams.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod; 
import org.testng.annotations.Listeners;

@Listeners(com.dams.report.ExtentListener.class)
public class BaseTest {
    protected WebDriver driver;
    protected LoginPage loginPage;

    @org.testng.annotations.BeforeSuite
    public void setupReport() {
        com.dams.report.CustomHtmlReporter.initializeReport();
    }

    @BeforeMethod
    public void setUp() {
        // Read credentials from GitHub Secrets (injected as environment variables)
        String baseUrl  = System.getenv("BASE_URL")       != null ? System.getenv("BASE_URL")       : "https://devadmin.damsdelhi.com/";
        String username = System.getenv("ADMIN_USERNAME")  != null ? System.getenv("ADMIN_USERNAME")  : "07siddwivedi@gmail.com";
        String password = System.getenv("ADMIN_PASSWORD")  != null ? System.getenv("ADMIN_PASSWORD")  : "Siddarth@123";
        String otp      = System.getenv("ADMIN_OTP")       != null ? System.getenv("ADMIN_OTP")       : "1980";

        driver = DriverFactory.initDriver();
        System.out.println("Navigating to: " + baseUrl);
        driver.get(baseUrl);
        com.dams.report.CustomHtmlReporter.logStep("TC_0", "Init", "STEP 1 – Open Application URL", "PASS", "-");

        loginPage = new LoginPage(driver);
        loginPage.login(username, password, otp);

        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @org.testng.annotations.AfterSuite
    public void finalizeReport() {
        com.dams.report.CustomHtmlReporter.generateReport();
    }
}
