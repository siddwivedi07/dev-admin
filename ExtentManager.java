package com.dams.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extentReports;

    public static ExtentReports getReporter() {
        if (extentReports == null) {
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String reportPath = System.getProperty("user.dir") + File.separator + "target" + File.separator 
                                + "Extent-Reports" + File.separator + "Admin-Test-Report-" + timestamp + ".html";
            
            // Ensures the directory exists
            new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "Extent-Reports").mkdirs();

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("DAMS Admin Automation Report");
            sparkReporter.config().setReportName("DAMS Dashboard UI Tests");
            sparkReporter.config().setTheme(Theme.DARK);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            
            // Add custom system information
            extentReports.setSystemInfo("Application", "DAMS Admin Panel");
            extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
            extentReports.setSystemInfo("Environment", System.getProperty("env", "QA"));
            extentReports.setSystemInfo("Browser", System.getProperty("browser", "Chrome"));
        }
        return extentReports;
    }
}
