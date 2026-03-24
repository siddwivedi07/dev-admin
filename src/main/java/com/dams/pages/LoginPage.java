package com.dams.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Exact Locators Automatically Extracted via Browser Agent
    private By emailInput = By.cssSelector("#email");
    private By passwordInput = By.cssSelector("#password");
    private By submitBtn = By.xpath("//button[contains(@class, 'ant-btn-primary') and contains(., 'Login')]");
    
    // OTP fields (it uses 4 separate input fields)
    private By otpChar1 = By.xpath("//input[@aria-label='Please enter OTP character 1']");
    private By otpChar2 = By.xpath("//input[@aria-label='Please enter OTP character 2']");
    private By otpChar3 = By.xpath("//input[@aria-label='Please enter OTP character 3']");
    private By otpChar4 = By.xpath("//input[@aria-label='Please enter OTP character 4']");
    
    // Final verify button after entering OTP
    private By verifyOtpBtn = By.xpath("//button[contains(@class, 'ant-btn-primary') and contains(., 'Submit')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void login(String email, String password, String otp) {
        // Step 1: Open Link (Assumed logged in BaseTest, but keeping tracking sequential)
        
        // Step 2: Enter Email
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).sendKeys(email);
        com.dams.report.CustomHtmlReporter.logStep("Login Module", "TC_1", "Login", "STEP 2 - Enter Email", "PASS", "-");
        
        // Step 2: Enter Password
        driver.findElement(passwordInput).sendKeys(password);
        com.dams.report.CustomHtmlReporter.logStep("Login Module", "TC_2", "Login", "STEP 2 - Enter Password", "PASS", "-");
        
        // Step 3: Click Login
        driver.findElement(submitBtn).click();
        com.dams.report.CustomHtmlReporter.logStep("Login Module", "TC_3", "Login", "STEP 3 - Click Login Button", "PASS", "-");
        
        // Step 4: Enter OTP
        if (otp != null && otp.length() >= 4) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(otpChar1)).sendKeys(otp);
            com.dams.report.CustomHtmlReporter.logStep("Login Module", "TC_4", "Login", "STEP 4 - Enter OTP", "PASS", "-");
            
            // Step 5: Click Submit/Verify OTP
            driver.findElement(verifyOtpBtn).click();
            com.dams.report.CustomHtmlReporter.logStep("Login Module", "TC_5", "Login", "STEP 5 - Click OTP Submit", "PASS", "-");
        }
    }
}
