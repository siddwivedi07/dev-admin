package com.dams.report;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List; 

/**
 * CustomHtmlReporter – generates the DAMS pixel-perfect Selenium HTML report.
 * Output: target/Custom-Reports/DAMS_Custom_Report_<timestamp>.html
 * Uploaded to GitHub Actions as artifact: "DAMS-Custom-Report-<run_number>"
 */
public class CustomHtmlReporter {

    private static String reportPath;
    private static final List<TestStep> steps = new ArrayList<>();

    // ── Inner model ──────────────────────────────────────────────────────────
    static class TestStep {
        String tc, phase, stepDesc, status, screenshotPath;
        TestStep(String tc, String phase, String stepDesc, String status, String screenshotPath) {
            this.tc = tc; this.phase = phase; this.stepDesc = stepDesc;
            this.status = status; this.screenshotPath = screenshotPath;
        }
    }

    // ── Public API ───────────────────────────────────────────────────────────
    public static void initializeReport() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File dir = new File(System.getProperty("user.dir") + "/target/Custom-Reports/");
        dir.mkdirs();
        reportPath = dir.getAbsolutePath() + "/DAMS_Custom_Report_" + timestamp + ".html";
        steps.clear();
    }

    public static void logStep(String tc, String phase, String stepDesc, String status, String screenshotLink) {
        steps.add(new TestStep(tc, phase, stepDesc, status, screenshotLink));
    }

    public static void generateReport() {
        // Derive counts dynamically from logged steps
        long passSteps = steps.stream().filter(s -> s.status.equalsIgnoreCase("PASS")).count();
        long failSteps = steps.stream().filter(s -> s.status.equalsIgnoreCase("FAIL")).count();

        // Top-level test counts: 1 suite = 1 test. Passed if zero failures.
        int totalTests = 1;
        int passTests  = failSteps == 0 ? 1 : 0;
        int failTests  = failSteps > 0  ? 1 : 0;

        String overallBadge = failTests == 0 ? "PASS" : "FAIL";
        String runDate      = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(new Date());

        // ── Build the sidebar module list ────────────────────────────────────
        StringBuilder modulesHtml = new StringBuilder();
        String lastModule = "";
        for (TestStep s : steps) {
            String module = s.phase;
            if (!module.equals(lastModule)) {
                String dotColor = s.status.equalsIgnoreCase("PASS") ? "#12B76A" : "#F04438";
                modulesHtml.append("<div class='sidebar-item'>")
                           .append("<span class='dot' style='background:").append(dotColor).append("'></span>")
                           .append(s.phase).append("</div>");
                lastModule = module;
            }
        }

        // ── Build table rows ─────────────────────────────────────────────────
        StringBuilder rowsHtml = new StringBuilder();
        for (TestStep s : steps) {
            boolean passed = s.status.equalsIgnoreCase("PASS");
            String badgeCls = passed ? "badge-pass" : "badge-fail";
            String screenshot = s.screenshotPath.equals("-")
                    ? "<span class='dash'>&ndash;</span>"
                    : "<a href='" + s.screenshotPath + "' target='_blank' class='view-btn'>View</a>";

            rowsHtml.append("<tr>")
                    .append("<td><span class='mono'>").append(s.tc).append("</span></td>")
                    .append("<td><span class='phase-tag'>").append(s.phase).append("</span></td>")
                    .append("<td>").append(s.stepDesc).append("</td>")
                    .append("<td><span class='badge ").append(badgeCls).append("'>").append(s.status).append("</span></td>")
                    .append("<td>").append(screenshot).append("</td>")
                    .append("</tr>\n");
        }

        // ── Assemble full HTML ───────────────────────────────────────────────
        String html = "<!DOCTYPE html>\n" +
        "<html lang='en'>\n" +
        "<head>\n" +
        "<meta charset='UTF-8'/>\n" +
        "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>\n" +
        "<title>DAMS Admin Selenium Report</title>\n" +
        "<link href='https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;600&family=DM+Mono:wght@400;500&display=swap' rel='stylesheet'/>\n" +
        "<style>\n" +
        "  *,*::before,*::after{box-sizing:border-box;margin:0;padding:0}\n" +
        "  :root{\n" +
        "    --blue:#2F54EB;--blue-lt:#EEF2FF;--blue-dk:#1D3DBF;\n" +
        "    --green:#12B76A;--green-lt:#ECFDF5;\n" +
        "    --red:#F04438;--red-lt:#FEF3F2;\n" +
        "    --sb-bg:#ffffff;--page-bg:#F5F7FF;--card-bg:#ffffff;\n" +
        "    --txt:#101828;--txt2:#667085;--txt3:#98A2B3;\n" +
        "    --border:#E4E7EC;--row-hover:#F9FAFB;\n" +
        "    --radius:10px;--sh:0 1px 3px rgba(16,24,40,.07),0 1px 2px rgba(16,24,40,.06);\n" +
        "  }\n" +
        "  [data-dark]{\n" +
        "    --sb-bg:#0F1117;--page-bg:#161B27;--card-bg:#1C2333;\n" +
        "    --txt:#F0F4FF;--txt2:#9BA8C4;--txt3:#5C6A8A;\n" +
        "    --border:#2A3352;--row-hover:#212B42;\n" +
        "    --blue-lt:#1A2550;--green-lt:#0D2F1F;--red-lt:#2E0F0F;\n" +
        "  }\n" +
        "  body{font-family:'DM Sans',sans-serif;background:var(--page-bg);color:var(--txt);display:flex;min-height:100vh;transition:background .25s,color .25s}\n" +

        // Sidebar
        "  .sidebar{width:220px;min-height:100vh;background:var(--sb-bg);border-right:1px solid var(--border);position:fixed;top:0;left:0;display:flex;flex-direction:column;padding:28px 0 0;z-index:10;transition:background .25s,border-color .25s}\n" +
        "  .sidebar-logo{font-size:18px;font-weight:600;color:var(--blue);letter-spacing:2px;padding:0 22px 20px;border-bottom:1px solid var(--border)}\n" +
        "  .sidebar-section-label{font-size:10px;font-weight:600;letter-spacing:1.5px;color:var(--txt3);padding:20px 22px 10px;text-transform:uppercase}\n" +
        "  .sidebar-item{display:flex;align-items:center;gap:8px;padding:9px 22px;font-size:13px;color:var(--txt2);cursor:default}\n" +
        "  .dot{width:8px;height:8px;border-radius:50%;flex-shrink:0}\n" +
        "  .sidebar-bottom{margin-top:auto;padding:20px 22px 28px;border-top:1px solid var(--border)}\n" +
        "  .toggle-row{display:flex;align-items:center;gap:10px;font-size:12px;color:var(--txt2)}\n" +
        "  .toggle-track{width:36px;height:20px;background:var(--border);border-radius:999px;position:relative;cursor:pointer;transition:background .2s}\n" +
        "  .toggle-track.on{background:#F79009}\n" +
        "  .toggle-thumb{position:absolute;top:3px;left:3px;width:14px;height:14px;border-radius:50%;background:#fff;transition:left .2s;box-shadow:0 1px 3px rgba(0,0,0,.2)}\n" +
        "  .toggle-track.on .toggle-thumb{left:19px}\n" +

        // Main
        "  .main{margin-left:220px;flex:1;padding:40px 44px 60px;max-width:calc(100vw - 220px)}\n" +
        "  .page-header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:32px}\n" +
        "  .page-title{font-size:24px;font-weight:600;color:var(--txt);letter-spacing:-.3px}\n" +
        "  .run-meta{font-size:11px;color:var(--txt3);margin-top:4px}\n" +
        "  .dams-badge{text-align:right;line-height:1}\n" +
        "  .dams-badge .top{font-size:13px;font-weight:600;letter-spacing:2px;color:var(--txt)}\n" +
        "  .dams-badge .alpha{font-size:28px;font-weight:300;color:var(--txt);font-style:italic;line-height:1}\n" +

        // Metric cards
        "  .metrics{display:grid;grid-template-columns:repeat(3,1fr);gap:18px;margin-bottom:32px}\n" +
        "  .metric-card{background:var(--card-bg);border:1px solid var(--border);border-radius:var(--radius);padding:22px 30px;text-align:center;box-shadow:var(--sh);transition:transform .15s,box-shadow .15s,background .25s,border-color .25s}\n" +
        "  .metric-card:hover{transform:translateY(-2px);box-shadow:0 4px 12px rgba(16,24,40,.1)}\n" +
        "  .metric-label{font-size:10px;font-weight:600;letter-spacing:1.5px;color:var(--txt3);text-transform:uppercase;margin-bottom:14px}\n" +
        "  .metric-value{font-size:32px;font-weight:600;font-family:'DM Mono',monospace}\n" +
        "  .mv-total{color:var(--txt)}.mv-pass{color:var(--green)}.mv-fail{color:var(--red)}\n" +

        // Test block
        "  .test-block{background:var(--card-bg);border:1px solid var(--border);border-radius:var(--radius);box-shadow:var(--sh);overflow:hidden;margin-bottom:24px;transition:background .25s,border-color .25s}\n" +
        "  .test-block-header{display:flex;align-items:center;gap:12px;padding:20px 24px 18px;border-bottom:1px solid var(--border)}\n" +
        "  .test-name{font-size:15px;font-weight:500;color:var(--txt)}\n" +
        "  .test-duration{font-size:13px;color:var(--txt3);font-family:'DM Mono',monospace}\n" +

        // Badges
        "  .badge{display:inline-flex;align-items:center;padding:3px 12px;border-radius:999px;font-size:11px;font-weight:600;letter-spacing:.5px;text-transform:uppercase}\n" +
        "  .badge-pass{background:var(--green-lt);color:#027A48}\n" +
        "  .badge-fail{background:var(--red-lt);color:#B42318}\n" +

        // Table
        "  table{width:100%;border-collapse:collapse}\n" +
        "  thead tr{background:var(--blue)}\n" +
        "  th{padding:13px 20px;font-size:10px;font-weight:600;letter-spacing:1.5px;color:rgba(255,255,255,.9);text-align:left;text-transform:uppercase;white-space:nowrap}\n" +
        "  td{padding:14px 20px;font-size:13px;color:var(--txt2);border-bottom:1px solid var(--border);vertical-align:middle;transition:color .25s}\n" +
        "  tr:last-child td{border-bottom:none}\n" +
        "  tbody tr{transition:background .12s}\n" +
        "  tbody tr:hover{background:var(--row-hover)}\n" +
        "  .mono{font-family:'DM Mono',monospace;font-size:12px;font-weight:500;color:var(--txt)}\n" +
        "  .phase-tag{display:inline-block;background:var(--blue-lt);color:var(--blue);font-size:11px;font-weight:500;padding:3px 10px;border-radius:6px}\n" +
        "  .view-btn{background:var(--blue);color:#fff;border:none;padding:6px 16px;border-radius:6px;font-size:12px;font-weight:500;cursor:pointer;text-decoration:none;display:inline-block;transition:background .15s;font-family:'DM Sans',sans-serif}\n" +
        "  .view-btn:hover{background:var(--blue-dk)}\n" +
        "  .dash{color:var(--txt3)}\n" +

        // Footer
        "  .footer{text-align:center;margin-top:48px;font-size:12px;color:var(--txt3);letter-spacing:.3px}\n" +

        // Animations
        "  @keyframes fadeUp{from{opacity:0;transform:translateY(12px)}to{opacity:1;transform:translateY(0)}}\n" +
        "  .metric-card{animation:fadeUp .4s ease both}\n" +
        "  .metric-card:nth-child(1){animation-delay:.05s}\n" +
        "  .metric-card:nth-child(2){animation-delay:.12s}\n" +
        "  .metric-card:nth-child(3){animation-delay:.19s}\n" +
        "  .test-block{animation:fadeUp .4s .25s ease both}\n" +
        "  tbody tr{animation:fadeUp .3s ease both}\n" +
        "</style>\n" +
        "</head>\n" +
        "<body>\n" +

        // ── Sidebar ──
        "<aside class='sidebar'>\n" +
        "  <div class='sidebar-logo'>DAMS</div>\n" +
        "  <div class='sidebar-section-label'>Modules</div>\n" +
        modulesHtml +
        "  <div class='sidebar-bottom'>\n" +
        "    <div class='toggle-row'>\n" +
        "      <div class='toggle-track' id='themeTrack'><div class='toggle-thumb'></div></div>\n" +
        "      <span id='themeLabel' style='color:#F79009;font-weight:500'>&#9679; Light</span>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "</aside>\n" +

        // ── Main ──
        "<main class='main'>\n" +
        "  <div class='page-header'>\n" +
        "    <div>\n" +
        "      <h1 class='page-title'>DAMS Admin Selenium Report</h1>\n" +
        "      <div class='run-meta'>Generated on " + runDate + "</div>\n" +
        "    </div>\n" +
        "    <div class='dams-badge'><div class='top'>DAMS</div><div class='alpha'>&alpha;</div></div>\n" +
        "  </div>\n" +

        // Metric cards
        "  <div class='metrics'>\n" +
        "    <div class='metric-card'><div class='metric-label'>Total Tests</div><div class='metric-value mv-total'>" + totalTests + "</div></div>\n" +
        "    <div class='metric-card'><div class='metric-label'>Passed</div><div class='metric-value mv-pass'>" + passTests + "</div></div>\n" +
        "    <div class='metric-card'><div class='metric-label'>Failed</div><div class='metric-value mv-fail'>" + failTests + "</div></div>\n" +
        "  </div>\n" +

        // Test block
        "  <div class='test-block'>\n" +
        "    <div class='test-block-header'>\n" +
        "      <span class='test-name'>adminLoginTest</span>\n" +
        "      <span class='test-duration'>(" + steps.size() + " steps)</span>\n" +
        "      <span class='badge " + (failTests == 0 ? "badge-pass" : "badge-fail") + "'>" + overallBadge + "</span>\n" +
        "    </div>\n" +
        "    <table>\n" +
        "      <thead><tr><th>TC</th><th>Phase</th><th>Step</th><th>Status</th><th>Screenshot</th></tr></thead>\n" +
        "      <tbody>\n" +
        rowsHtml +
        "      </tbody>\n" +
        "    </table>\n" +
        "  </div>\n" +

        "  <div class='footer'>Created by Ashutosh Mago &mdash; Junior AWS DevOps Engineer</div>\n" +
        "</main>\n" +

        // ── Dark-mode toggle script ──
        "<script>\n" +
        "  var track = document.getElementById('themeTrack');\n" +
        "  var label = document.getElementById('themeLabel');\n" +
        "  var dark  = false;\n" +
        "  track.addEventListener('click', function(){\n" +
        "    dark = !dark;\n" +
        "    document.body.toggleAttribute('data-dark', dark);\n" +
        "    track.classList.toggle('on', dark);\n" +
        "    label.innerHTML = dark ? '&#9679; Dark' : '&#9679; Light';\n" +
        "    label.style.color = dark ? '#9BA8C4' : '#F79009';\n" +
        "  });\n" +
        "</script>\n" +
        "</body></html>";

        try (FileWriter writer = new FileWriter(reportPath)) {
            writer.write(html);
            System.out.println("✅ DAMS Custom HTML Report → " + reportPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
