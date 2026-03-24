package com.dams.report;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomHtmlReporter {

    private static String reportPath;
    private static Map<String, List<TestStep>> tableGroups = new LinkedHashMap<>();
    private static int totalTests = 1;
    private static int passCount = 1;
    private static int failCount = 0;

    static class TestStep {
        String tc;
        String phase;
        String stepDesc;
        String status;
        String screenshotPath;

        public TestStep(String tc, String phase, String stepDesc, String status, String screenshotPath) {
            this.tc = tc;
            this.phase = phase;
            this.stepDesc = stepDesc;
            this.status = status;
            this.screenshotPath = screenshotPath;
        }
    }

    public static void initializeReport() {
        new File(System.getProperty("user.dir") + "/target/Custom-Reports/").mkdirs();
        reportPath = System.getProperty("user.dir") + "/target/Custom-Reports/index.html";
        tableGroups.clear();
    }

    public static void logStep(String tableName, String tc, String phase, String stepDesc, String status, String screenshotLink) {
        tableGroups.putIfAbsent(tableName, new ArrayList<>());
        tableGroups.get(tableName).add(new TestStep(tc, phase, stepDesc, status, screenshotLink));
        if (status.equalsIgnoreCase("FAIL")) {
            failCount++;
            passCount = Math.max(0, passCount - 1);
        }
    }

    public static void generateReport() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>DAMS Admin Selenium Report</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f8f9fb; margin: 0; padding: 0; }");
        html.append(".sidebar { width: 220px; background: white; height: 100vh; position: fixed; box-shadow: 2px 0 5px rgba(0,0,0,0.05); }");
        html.append(".content { margin-left: 240px; padding: 40px; max-width: 1200px; }");
        html.append(".metrics { display: flex; gap: 20px; margin-bottom: 30px; }");
        html.append(".card { background: white; padding: 20px 40px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.02); text-align: center; font-weight: bold; flex: 1; border: 1px solid #f0f0f0; }");
        html.append(".card-title { color: #888; font-size: 11px; letter-spacing: 1px; margin-bottom: 15px; }");
        html.append(".card-value { font-size: 28px; }");
        html.append(".green { color: #2ecc71; } .red { color: #e74c3c; } .black { color: #333; }");
        html.append("table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.02); border: 1px solid #f0f0f0; }");
        html.append("th { background: #3b5bdb; color: white; padding: 15px; font-size: 11px; text-align: left; letter-spacing: 1px; }");
        html.append("td { padding: 15px; border-bottom: 1px solid #eee; font-size: 13px; color: #444; }");
        html.append(".status-badge { padding: 5px 12px; border-radius: 20px; font-size: 11px; font-weight: bold; }");
        html.append(".pass-badge { background: #eaffea; color: #2ecc71; }");
        html.append(".fail-badge { background: #fdedec; color: #e74c3c; }");
        html.append(".view-btn { background: #3b5bdb; color: white; border: none; padding: 6px 15px; border-radius: 4px; cursor: pointer; text-decoration: none; font-size: 12px; }");
        html.append("</style></head><body>");
        
        // Sidebar Content
        html.append("<div class='sidebar'>");
        html.append("<h2 style='color: #3b5bdb; margin: 30px 20px 10px 20px; font-size: 16px;'>DAMS</h2>");
        html.append("<p style='color: #999; font-size: 10px; margin-left: 20px; letter-spacing: 1px;'>MODULES</p>");
        for (String tableName : tableGroups.keySet()) {
            html.append("<p style='font-size: 12px; color: #555; margin-left: 20px;'><span style='color:#2ecc71;'>&bull;</span> ").append(tableName).append("</p>");
        }
        html.append("</div>");
        
        // Main Header
        html.append("<div class='content'>");
        html.append("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;'>");
        html.append("<h1 style='color: #2c3e50;'>DAMS Admin Selenium Report</h1>");
        html.append("<div><h2 style='margin: 0; color: #333;'>DAMS &alpha;</h2></div>");
        html.append("</div>");
        
        // Score Cards
        html.append("<div class='metrics'>");
        html.append("<div class='card'><div class='card-title'>TOTAL TESTS</div><div class='card-value black'>").append(totalTests).append("</div></div>");
        html.append("<div class='card'><div class='card-title'>PASSED</div><div class='card-value green'>").append(passCount).append("</div></div>");
        html.append("<div class='card'><div class='card-title'>FAILED</div><div class='card-value red'>").append(failCount).append("</div></div>");
        html.append("</div>");
        
        // Generate separate tables for each group
        for (String tableName : tableGroups.keySet()) {
            boolean hasFail = tableGroups.get(tableName).stream().anyMatch(step -> step.status.equalsIgnoreCase("FAIL"));
            String badge = hasFail ? "<span class='status-badge fail-badge' style='margin-left: 10px;'>FAIL</span>" 
                                   : "<span class='status-badge pass-badge' style='margin-left: 10px;'>PASS</span>";
                                   
            html.append("<div style='background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.02); margin-bottom: 25px;'>");
            html.append("<h3 style='margin-top: 0; font-size: 15px; color: #333;'>").append(tableName).append(badge).append("</h3>");
            
            // Data Table
            html.append("<table><tr><th>TC</th><th>PHASE</th><th>STEP</th><th>STATUS</th><th>SCREENSHOT</th></tr>");
            
            for (TestStep step : tableGroups.get(tableName)) {
                String rowBadge = step.status.equalsIgnoreCase("PASS") ? "pass-badge" : "fail-badge";
                String screenshotHtml = step.screenshotPath.equals("-") ? "-" : "<a href='" + step.screenshotPath + "' target='_blank' class='view-btn'>View</a>";
                
                html.append("<tr>");
                html.append("<td>").append(step.tc).append("</td>");
                html.append("<td>").append(step.phase).append("</td>");
                html.append("<td>").append(step.stepDesc).append("</td>");
                html.append("<td><span class='status-badge ").append(rowBadge).append("'>").append(step.status).append("</span></td>");
                html.append("<td>").append(screenshotHtml).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            html.append("</div>");
        }
        
        html.append("<p style='text-align: center; margin-top: 50px; color: #aaa; font-size: 11px;'>Created by Ashutosh Mago - Junior AWS DevOps Engineer</p>");
        html.append("</div></body></html>");

        try {
            FileWriter writer = new FileWriter(reportPath);
            writer.write(html.toString());
            writer.close();
            System.out.println("Pixel-Perfect Custom HTML Report Generated: " + reportPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
