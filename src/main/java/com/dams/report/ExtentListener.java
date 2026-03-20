package com.dams.report;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class ExtentListener implements ITestListener {
    private ExtentReports extent = ExtentManager.getReporter();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getTestClass().getName() + " -> " + result.getMethod().getMethodName());
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, "Test Passed!");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        
        try {
            // Automatically capture a screenshot of the browser on failure
            String screenshotPath = com.dams.core.ScreenshotUtils.captureScreenshot(result.getMethod().getMethodName());
            if (screenshotPath != null) {
                // Attach the screenshot seamlessly to the HTML Report UI
                extentTest.get().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            }
        } catch (Exception e) {
            System.out.println("Failed to attach screenshot to report: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }
}
