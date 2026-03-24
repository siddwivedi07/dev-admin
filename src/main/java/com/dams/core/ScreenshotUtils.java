package com.dams.core;

import com.dams.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    
    /**
     * Captures a screenshot of the current WebDriver session and saves it locally.
     * @param testName Name of the test method to append to the image file.
     * @return The relative path to the screenshot (to be used by ExtentReports).
     */
    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) return null;

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String screenshotName = testName + "_" + timestamp + ".png";
        
        // Save inside the Extent-Reports/screenshots directory so it bundles with the HTML
        String reportDir = System.getProperty("user.dir") + File.separator + "target" + File.separator + "Extent-Reports";
        String screenshotFullPath = reportDir + File.separator + "screenshots" + File.separator + screenshotName;
        
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(screenshotFullPath);
            
            // Create directories if they don't exist
            Files.createDirectories(destination.getParent());
            
            // Copy file using raw Java NIO (No dependencies required)
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path so the HTML file can load it natively
            return "screenshots/" + screenshotName;
            
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
