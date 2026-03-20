package com.dams.test;

import com.dams.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test
    public void verifyDashboardNavigationAfterLogin() {
        System.out.println("Validating successful login...");
        System.out.println("Current Page URL: " + driver.getCurrentUrl());
        System.out.println("Current Page Title: " + driver.getTitle());
        
        // Assert that the URL contains devadmin or dashboard, confirming we moved past the login page
        Assert.assertTrue(driver.getCurrentUrl().contains("devadmin"), "We should be on the dashboard page of devadmin");
        
        com.dams.report.CustomHtmlReporter.logStep("TC_6", "Execution", "adminLoginTest", "PASS", "-");
    }
}
