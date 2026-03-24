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
            String timestamp  = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            // target/Extent-Reports/ is uploaded as the "DAMS-Extent-Report" GitHub Actions artifact
            String reportDir  = System.getProperty("user.dir") + File.separator
                                + "target" + File.separator + "Extent-Reports";
            String reportPath = reportDir + File.separator
                                + "DAMS_Extent_Report_" + timestamp + ".html";

            new File(reportDir).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("DAMS Admin Automation Report");
            spark.config().setReportName("DAMS Dashboard UI Tests");
            spark.config().setTheme(Theme.DARK);

            extentReports = new ExtentReports();
            extentReports.attachReporter(spark);

            extentReports.setSystemInfo("Application", "DAMS Admin Panel");
            extentReports.setSystemInfo("OS",          System.getProperty("os.name"));
            extentReports.setSystemInfo("Environment", System.getProperty("env", "QA"));
            extentReports.setSystemInfo("Browser",     System.getProperty("browser", "Chrome"));
        }
        return extentReports;
    }
}
