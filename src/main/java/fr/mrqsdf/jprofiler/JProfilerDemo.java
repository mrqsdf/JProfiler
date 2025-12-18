package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.*;
import fr.mrqsdf.jprofiler.css.CSS;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.css.CSSPropertyName;
import fr.mrqsdf.jprofiler.page.Page;

import java.util.ArrayList;
import java.util.List;

public class JProfilerDemo {

    public static void run() {

        try {
            // Setup default CSS styles
            CSS.setupDefaultStyles();
            setupLayoutStyles();

            // Create pages
            Page dashboardPage = JProfiler.createPage("dashboard", "Dashboard");
            Page analyticsPage = JProfiler.createPage("analytics", "Analytics");
            Page settingsPage = JProfiler.createPage("settings", "Settings");

            // Setup sidebar (common to all pages)
            TextComponent navTitle = new TextComponent("Navigation", "nav-title");
            ButtonComponent dashboardBtn = new ButtonComponent("📊 Dashboard", "nav-button", ActionType.NAVIGATE_PAGE, "dashboard");
            ButtonComponent analyticsBtn = new ButtonComponent("📈 Analytics", "nav-button", ActionType.NAVIGATE_PAGE, "analytics");
            ButtonComponent settingsBtn = new ButtonComponent("⚙️ Settings", "nav-button", ActionType.NAVIGATE_PAGE, "settings");

            // Dashboard Page
            setupDashboardPage(dashboardPage, navTitle, dashboardBtn, analyticsBtn, settingsBtn);

            // Analytics Page
            setupAnalyticsPage(analyticsPage, navTitle, dashboardBtn, analyticsBtn, settingsBtn);

            // Settings Page
            setupSettingsPage(settingsPage, navTitle, dashboardBtn, analyticsBtn, settingsBtn);

            // Start the server (async to keep main thread free)
            System.out.println("Starting JProfiler Server on port 25685 (async)...");
            JProfiler.startServerAsync(25685);

        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupDashboardPage(Page page, TextComponent navTitle, 
                                          ButtonComponent dashboardBtn, ButtonComponent analyticsBtn, ButtonComponent settingsBtn) {
        // Header
        page.addToHeader(new TextComponent("JProfiler", "header-title"))
            .addToHeader(new TextComponent("Professional Reporting", "header-subtitle"));

        // Sidebar
        page.addToBody(createSidebar(navTitle, dashboardBtn, analyticsBtn, settingsBtn));
        // Content
        page.addToBody(createDashboardContent());

        // Footer
        page.addToFooter(new TextComponent("JProfiler © 2025 - All rights reserved", "footer-text"));
    }

    private static void setupAnalyticsPage(Page page, TextComponent navTitle,
                                          ButtonComponent dashboardBtn, ButtonComponent analyticsBtn, ButtonComponent settingsBtn) {
        // Header
        page.addToHeader(new TextComponent("JProfiler", "header-title"))
            .addToHeader(new TextComponent("Professional Reporting", "header-subtitle"));

        // Sidebar
        page.addToBody(createSidebar(navTitle, dashboardBtn, analyticsBtn, settingsBtn));
        // Content
        page.addToBody(createAnalyticsContent());

        // Footer
        page.addToFooter(new TextComponent("JProfiler © 2025 - All rights reserved", "footer-text"));
    }

    private static void setupSettingsPage(Page page, TextComponent navTitle,
                                         ButtonComponent dashboardBtn, ButtonComponent analyticsBtn, ButtonComponent settingsBtn) {
        // Header
        page.addToHeader(new TextComponent("JProfiler", "header-title"))
            .addToHeader(new TextComponent("Professional Reporting", "header-subtitle"));

        // Sidebar
        page.addToBody(createSidebar(navTitle, dashboardBtn, analyticsBtn, settingsBtn));
        // Content
        page.addToBody(createSettingsContent());

        // Footer
        page.addToFooter(new TextComponent("JProfiler © 2025 - All rights reserved", "footer-text"));
    }

    private static PageComponent createSidebar(TextComponent title, ButtonComponent... buttons) {
        PageComponent sidebar = new PageComponent("sidebar");
        sidebar.addToBody(title);
        for (ButtonComponent btn : buttons) {
            sidebar.addToBody(btn);
        }
        return sidebar;
    }

    private static PageComponent createDashboardContent() {
        PageComponent content = new PageComponent("main-content");

        content.addToBody(new TextComponent("Dashboard", "page-title"));
        content.addToBody(new TextComponent("Welcome to your dashboard", "page-subtitle"));

        // Add some metrics
        List<Component> metrics = new ArrayList<>();
        metrics.add(new TextComponent("Key Metrics", "section-title"));
        metrics.add(new MultiTextComponent(
            List.of(
                new TextComponent("• Total Users: 1,234", "metric-item"),
                new TextComponent("• Active Sessions: 456", "metric-item"),
                new TextComponent("• Page Views: 12,345", "metric-item"),
                new TextComponent("• Conversion Rate: 3.2%", "metric-item")
            ),
            "metrics-list"
        ));

        // Add a bar chart
        metrics.add(new TextComponent("This Week's Activity", "section-title"));
        metrics.add(new ChartComponent(
            List.of(150.0, 200.0, 175.0, 225.0, 300.0, 250.0, 180.0),
            List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
            ChartComponent.ChartType.BAR,
            "Daily Users",
            "activity-chart"
        ));

        content.addAllToBody(metrics);
        return content;
    }

    private static PageComponent createAnalyticsContent() {
        PageComponent content = new PageComponent("main-content");

        content.addToBody(new TextComponent("Analytics", "page-title"));
        content.addToBody(new TextComponent("Detailed performance analysis", "page-subtitle"));

        List<Component> analytics = new ArrayList<>();

        // Line chart for growth
        analytics.add(new TextComponent("Growth Trend", "section-title"));
        analytics.add(new ChartComponent(
            List.of(1000.0, 1200.0, 1100.0, 1400.0, 1600.0, 1800.0),
            List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
            ChartComponent.ChartType.LINE,
            "Revenue Growth",
            "growth-chart"
        ));

        // Pie chart for distribution
        analytics.add(new TextComponent("Traffic Distribution", "section-title"));
        analytics.add(new ChartComponent(
            List.of(40.0, 30.0, 20.0, 10.0),
            List.of("Organic", "Direct", "Social", "Referral"),
            ChartComponent.ChartType.PIE,
            "Traffic Sources",
            "traffic-chart"
        ));

        content.addAllToBody(analytics);
        return content;
    }

    private static PageComponent createSettingsContent() {
        PageComponent content = new PageComponent("main-content");

        content.addToBody(new TextComponent("Settings", "page-title"));
        content.addToBody(new TextComponent("Manage your preferences", "page-subtitle"));

        List<Component> settings = new ArrayList<>();

        settings.add(new TextComponent("General Settings", "section-title"));
        settings.add(new TextFieldComponent("site-name", "Site Name", "input-field", "text", "My Site"));
        settings.add(new TextFieldComponent("admin-email", "Admin Email", "input-field", "email", "admin@example.com"));

        settings.add(new TextComponent("Notifications", "section-title"));
        settings.add(new CheckboxComponent("email-notifications", "Enable email notifications", "checkbox", true));
        settings.add(new CheckboxComponent("sms-notifications", "Enable SMS notifications", "checkbox", false));
        settings.add(new CheckboxComponent("push-notifications", "Enable push notifications", "checkbox", true));

        settings.add(new TextComponent("Preferences", "section-title"));
        settings.add(new TextFieldComponent("theme", "Theme", "input-field", "text", "Light"));
        settings.add(new TextFieldComponent("language", "Language", "input-field", "text", "English"));

        settings.add(new ButtonComponent("Save Settings", "save-button"));

        content.addAllToBody(settings);
        return content;
    }

    private static void setupLayoutStyles() {
        // Root layout grid
        CSSManager.addRule("html, body")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0")
            .addProperty(CSSPropertyName.PADDING.getValue(), "0")
            .addProperty(CSSPropertyName.HEIGHT.getValue(), "100%")
            .addProperty(CSSPropertyName.WIDTH.getValue(), "100%");

        CSSManager.addRule(".page")
            .addProperty(CSSPropertyName.DISPLAY.getValue(), "grid")
            .addProperty(CSSPropertyName.GRID_TEMPLATE_COLUMNS.getValue(), "200px 1fr")
            .addProperty(CSSPropertyName.GRID_TEMPLATE_ROWS.getValue(), "auto 1fr auto")
            .addProperty("grid-template-areas", "\"header header\" \"sidebar content\" \"footer footer\"")
            .addProperty(CSSPropertyName.MIN_HEIGHT.getValue(), "100vh")
            .addProperty(CSSPropertyName.WIDTH.getValue(), "100%")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0")
            .addProperty(CSSPropertyName.PADDING.getValue(), "0");

        // Header spanning full width
        CSSManager.addRule(".page_header")
            .addProperty("grid-area", "header")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#2c3e50")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#ecf0f1")
            .addProperty(CSSPropertyName.PADDING.getValue(), "20px")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center");

        CSSManager.addRule(".header-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "28px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0");

        CSSManager.addRule(".header-subtitle")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "14px")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#bdc3c7");

        // Sidebar in left column
        CSSManager.addRule(".sidebar")
            .addProperty("grid-area", "sidebar")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#ecf0f1")
            .addProperty(CSSPropertyName.PADDING.getValue(), "20px")
            .addProperty(CSSPropertyName.BORDER_RIGHT.getValue(), "1px solid #bdc3c7")
            .addProperty(CSSPropertyName.OVERFLOW_Y.getValue(), "auto");

        CSSManager.addRule(".nav-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "16px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "15px")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#2c3e50");

        CSSManager.addRule(".nav-button")
            .addProperty(CSSPropertyName.WIDTH.getValue(), "100%")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "8px 0");

        CSSManager.addRule(".nav-button .button_element")
            .addProperty(CSSPropertyName.WIDTH.getValue(), "100%")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#3498db")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "left");

        // Main content in right column
        CSSManager.addRule(".main-content")
            .addProperty("grid-area", "content")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#ffffff")
            .addProperty(CSSPropertyName.PADDING.getValue(), "20px")
            .addProperty(CSSPropertyName.OVERFLOW_Y.getValue(), "auto");

        CSSManager.addRule(".page-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "24px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#2c3e50")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0 0 10px 0");

        CSSManager.addRule(".page-subtitle")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "14px")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#7f8c8d")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "20px");

        CSSManager.addRule(".section-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "18px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#34495e")
            .addProperty(CSSPropertyName.MARGIN_TOP.getValue(), "20px")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "10px");

        // Footer spanning full width
        CSSManager.addRule(".page_footer")
            .addProperty("grid-area", "footer")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#2c3e50")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#ecf0f1")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center")
            .addProperty(CSSPropertyName.PADDING.getValue(), "15px");

        CSSManager.addRule(".footer-text")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "12px");
    }

    public static void main(String... args) {
        run();
        
    }
}
