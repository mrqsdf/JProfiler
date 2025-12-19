package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.binding.BindableProperty;
import fr.mrqsdf.jprofiler.component.*;
import fr.mrqsdf.jprofiler.css.CSS;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.event.ComponentEvent;
import fr.mrqsdf.jprofiler.page.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point for JProfiler with a menu to access all demos
 */
public class JProfilerMain {

    public static void main(String[] args) {
        try {
            // Debug is disabled by default (production mode)
            // Uncomment to enable debug during development:
            // JProfiler.enableDebug();

            // Setup CSS
            CSS.setupDefaultStyles();
            setupStyles();

            // Create all demo pages
            createHomePage();
            createSimpleFormDemo();
            createInteractiveDemo();
            createConfigBindingDemo();
            createFibonacciDemo();
            createDynamicComponentsDemo();
            createChartDemo();

            // Set home page as current
            JProfiler.setCurrentPage("home");

            // Start server
            JProfiler.startServerAsync(8080);
            System.out.println("🚀 JProfiler Main started on http://localhost:8080");
            System.out.println("📋 Choose a demo from the home page");
            while (true) {
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupStyles() {
        // Home page styles
        CSSManager.addRule(".home-container")
                .addProperty("display", "grid")
                .addProperty("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))")
                .addProperty("gap", "20px")
                .addProperty("padding", "20px")
                .addProperty("max-width", "1200px")
                .addProperty("margin", "0 auto");

        CSSManager.addRule(".demo-card")
                .addProperty("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .addProperty("border-radius", "15px")
                .addProperty("padding", "30px")
                .addProperty("color", "white")
                .addProperty("box-shadow", "0 10px 30px rgba(0,0,0,0.3)")
                .addProperty("transition", "transform 0.3s ease, box-shadow 0.3s ease");

        CSSManager.addRule(".demo-card:hover")
                .addProperty("transform", "translateY(-5px)")
                .addProperty("box-shadow", "0 15px 40px rgba(0,0,0,0.4)");

        CSSManager.addRule(".demo-card h2")
                .addProperty("margin-top", "0")
                .addProperty("font-size", "24px")
                .addProperty("margin-bottom", "10px");

        CSSManager.addRule(".demo-card p")
                .addProperty("opacity", "0.9")
                .addProperty("margin-bottom", "20px")
                .addProperty("line-height", "1.6");

        CSSManager.addRule(".demo-button")
                .addProperty("background", "rgba(255,255,255,0.2)")
                .addProperty("border", "2px solid white")
                .addProperty("color", "white")
                .addProperty("padding", "12px 30px")
                .addProperty("border-radius", "25px")
                .addProperty("font-size", "16px")
                .addProperty("font-weight", "bold")
                .addProperty("cursor", "pointer")
                .addProperty("transition", "all 0.3s ease")
                .addProperty("text-transform", "uppercase");

        CSSManager.addRule(".demo-button:hover")
                .addProperty("background", "white")
                .addProperty("color", "#667eea")
                .addProperty("transform", "scale(1.05)");

        CSSManager.addRule(".back-button")
                .addProperty("margin", "20px")
                .addProperty("padding", "10px 20px")
                .addProperty("background", "#667eea")
                .addProperty("color", "white")
                .addProperty("border", "none")
                .addProperty("border-radius", "5px")
                .addProperty("cursor", "pointer")
                .addProperty("font-size", "14px");

        CSSManager.addRule(".back-button:hover")
                .addProperty("background", "#764ba2");

        CSSManager.addRule(".title")
                .addProperty("font-size", "32px")
                .addProperty("font-weight", "bold")
                .addProperty("margin-bottom", "20px");
    }

    private static void createHomePage() {
        Page homePage = JProfiler.createPage("home", "🎯 JProfiler Demo Center");

        TextComponent header = new TextComponent("Welcome to JProfiler", "header");
        TextComponent subtitle = new TextComponent("Choose a demo to explore the features of JProfiler", "subtitle");

        // Create demo cards as HTML components
        ButtonComponent cardButton1 = new ButtonComponent("Simple Form Demo", null, ActionType.NAVIGATE_PAGE, "simpleForm");
        ButtonComponent cardButton2 = new ButtonComponent("Interactive Demo", null, ActionType.NAVIGATE_PAGE, "interactive");
        ButtonComponent cardButton3 = new ButtonComponent("Config Binding Demo", null, ActionType.NAVIGATE_PAGE, "configBinding");
        ButtonComponent cardButton4 = new ButtonComponent("Fibonacci Demo", null, ActionType.NAVIGATE_PAGE, "fibonacci");
        ButtonComponent cardButton5 = new ButtonComponent("Dynamic Components Demo", null, ActionType.NAVIGATE_PAGE, "dynamicComponents");
        ButtonComponent cardButton6 = new ButtonComponent("Chart Demo", null, ActionType.NAVIGATE_PAGE, "chart");
        homePage.addToBody(header);
        homePage.addToBody(subtitle);
        homePage.addToBody(cardButton1);
        homePage.addToBody(cardButton2);
        homePage.addToBody(cardButton3);
        homePage.addToBody(cardButton4);
        homePage.addToBody(cardButton5);
        homePage.addToBody(cardButton6);
    }

    private static void createSimpleFormDemo() {
        Page page = JProfiler.createPage("simpleForm", "📝 Simple Form Demo");

        // Back button
        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");

        TextComponent title = new TextComponent("Simple Form Demo", "title");
        TextComponent description = new TextComponent("Enter a message and click 'Send Message' to see the counter increment.", "description");

        // Bindable properties
        BindableProperty<String> messageProp = new BindableProperty<>("messageProp", "", String.class);
        BindableProperty<Integer> counterProp = new BindableProperty<>("counterProp", 0, Integer.class);

        // Components
        TextFieldComponent messageField = new TextFieldComponent("messageField", "Enter your message", "input-field");
        ButtonComponent sendButton = new ButtonComponent("Send Message", "button", null, null, "sendButton");
        
        DynamicTextComponent counterDisplay = new DynamicTextComponent("counterDisplay", 
            "Message sent {counter} times: {message}", "result");

        // Bind components to properties
        JProfiler.bindComponent("messageField", messageProp);

        // Event listener: update property when field changes
        JProfiler.addEventListener("messageField", event -> {
            if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) {
                String message = event.getDataAsString("value");
                messageProp.setValue(message);
            }
        });

        // Event listener: increment counter on button click
        JProfiler.addEventListener("sendButton", event -> {
            if (event.getType() == ComponentEvent.EventType.BUTTON_CLICK) {
                String message = messageProp.getValue();
                if (message != null && !message.trim().isEmpty()) {
                    counterProp.setValue(counterProp.getValue() + 1);
                    JProfiler.updatePlaceholder("counterDisplay", "counter", String.valueOf(counterProp.getValue()));
                    JProfiler.updatePlaceholder("counterDisplay", "message", message);
                }
            }
        });

        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(description);
        page.addToBody(messageField);
        page.addToBody(sendButton);
        page.addToBody(counterDisplay);
    }

    private static void createInteractiveDemo() {
        Page page = JProfiler.createPage("interactive", "🎮 Interactive Demo");

        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");
        TextComponent title = new TextComponent("Interactive Configuration Demo", "title");

        // Bindable properties
        BindableProperty<String> usernameProp = new BindableProperty<>("usernameProp", "User", String.class);
        BindableProperty<Integer> counterProp = new BindableProperty<>("counterProp", 0, Integer.class);
        BindableProperty<Integer> multiplierProp = new BindableProperty<>("multiplierProp", 1, Integer.class);
        BindableProperty<Boolean> autoIncrementProp = new BindableProperty<>("autoIncrementProp", false, Boolean.class);

        // Components
        TextFieldComponent usernameField = new TextFieldComponent("username", "Username", "input-field");
        TextFieldComponent multiplierField = new TextFieldComponent("multiplier", "Multiplier", "input-field");
        CheckboxComponent autoIncrementCheckbox = new CheckboxComponent("autoIncrement", "Auto-increment counter", "checkbox", false);
        ButtonComponent incrementButton = new ButtonComponent("Increment Counter", "button", null, null, "incrementBtn");
        
        DynamicTextComponent display = new DynamicTextComponent("display",
                "Hello {username}! Counter: {counter} (x{multiplier})", "result");

        // Bind components
        JProfiler.bindComponent("username", usernameProp);
        JProfiler.bindComponent("multiplier", multiplierProp);
        JProfiler.bindComponent("autoIncrement", autoIncrementProp);

        // Event listeners
        JProfiler.addEventListener("incrementBtn", event -> {
            if (event.getType() == ComponentEvent.EventType.BUTTON_CLICK) {
                counterProp.setValue(counterProp.getValue() + multiplierProp.getValue());
                JProfiler.updatePlaceholder("display", "username", usernameProp.getValue());
                JProfiler.updatePlaceholder("display", "counter", String.valueOf(counterProp.getValue()));
                JProfiler.updatePlaceholder("display", "multiplier", String.valueOf(multiplierProp.getValue()));
            }
        });

        // Auto-increment thread
        Thread autoIncrementThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (autoIncrementProp.getValue()) {
                        counterProp.setValue(counterProp.getValue() + 1);
                        JProfiler.updatePlaceholder("display", "counter", String.valueOf(counterProp.getValue()));
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        autoIncrementThread.setDaemon(true);
        autoIncrementThread.start();

        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(usernameField);
        page.addToBody(multiplierField);
        page.addToBody(autoIncrementCheckbox);
        page.addToBody(incrementButton);
        page.addToBody(display);
    }

    private static void createConfigBindingDemo() {
        Page page = JProfiler.createPage("configBinding", "⚙️ Config Binding Demo");

        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");
        TextComponent title = new TextComponent("Server Configuration Binding", "title");
        TextComponent description = new TextComponent("Modify server configuration in real-time. Changes are immediately reflected in the ServerConfig object.", "description");

        // Server config object
        ServerConfig config = new ServerConfig();
        config.host = "localhost";
        config.port = 8080;
        config.maxConnections = 100;
        config.enableSSL = false;

        // Bindable properties
        BindableProperty<String> hostProp = new BindableProperty<>("hostProp", config.host, String.class);
        BindableProperty<Integer> portProp = new BindableProperty<>("portProp", config.port, Integer.class);
        BindableProperty<Integer> maxConnProp = new BindableProperty<>("maxConnProp", config.maxConnections, Integer.class);
        BindableProperty<Boolean> sslProp = new BindableProperty<>("sslProp", config.enableSSL, Boolean.class);

        // Components
        TextFieldComponent hostField = new TextFieldComponent("host", "Host", "input-field");
        TextFieldComponent portField = new TextFieldComponent("port", "Port", "input-field");
        TextFieldComponent maxConnField = new TextFieldComponent("maxConn", "Max Connections", "input-field");
        CheckboxComponent sslCheckbox = new CheckboxComponent("ssl", "Enable SSL", "checkbox", false);
        
        DynamicTextComponent configDisplay = new DynamicTextComponent("configDisplay",
                "Current Configuration:\nHost: {host}\nPort: {port}\nMax Connections: {maxConn}\nSSL Enabled: {ssl}", "result");

        // Bind components
        JProfiler.bindComponent("host", hostProp);
        JProfiler.bindComponent("port", portProp);
        JProfiler.bindComponent("maxConn", maxConnProp);
        JProfiler.bindComponent("ssl", sslProp);

        // Update display when any property changes
        Runnable updateDisplay = () -> {
            config.host = hostProp.getValue();
            config.port = portProp.getValue();
            config.maxConnections = maxConnProp.getValue();
            config.enableSSL = sslProp.getValue();
            
            JProfiler.updatePlaceholder("configDisplay", "host", hostProp.getValue());
            JProfiler.updatePlaceholder("configDisplay", "port", String.valueOf(portProp.getValue()));
            JProfiler.updatePlaceholder("configDisplay", "maxConn", String.valueOf(maxConnProp.getValue()));
            JProfiler.updatePlaceholder("configDisplay", "ssl", String.valueOf(sslProp.getValue()));
        };

        // Listen to field changes
        JProfiler.addEventListener("host", event -> { 
            if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) updateDisplay.run(); 
        });
        JProfiler.addEventListener("port", event -> { 
            if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) updateDisplay.run(); 
        });
        JProfiler.addEventListener("maxConn", event -> { 
            if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) updateDisplay.run(); 
        });
        JProfiler.addEventListener("ssl", event -> { 
            if (event.getType() == ComponentEvent.EventType.CHECKBOX_CHANGE) updateDisplay.run(); 
        });

        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(description);
        page.addToBody(hostField);
        page.addToBody(portField);
        page.addToBody(maxConnField);
        page.addToBody(sslCheckbox);
        page.addToBody(configDisplay);
    }

    private static void createFibonacciDemo() {
        Page page = JProfiler.createPage("fibonacci", "🔢 Fibonacci Demo");

        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");
        TextComponent title = new TextComponent("Fibonacci Calculator with Profiling", "title");

        // Bindable properties
        BindableProperty<Integer> nProp = new BindableProperty<>("nProp", 10, Integer.class);
        BindableProperty<Long> resultProp = new BindableProperty<>("resultProp", 0L, Long.class);
        BindableProperty<String> timeProp = new BindableProperty<>("timeProp", "0ms", String.class);

        // Components
        TextFieldComponent nField = new TextFieldComponent("fibN", "Enter n (Fibonacci index)", "input-field");
        ButtonComponent calculateButton = new ButtonComponent("Calculate", "button", null, null, "calculateBtn");
        
        DynamicTextComponent resultDisplay = new DynamicTextComponent("fibResult",
                "Fibonacci({n}) = {result} (calculated in {time})", "result");

        // Bind components
        JProfiler.bindComponent("fibN", nProp);

        // Event listener
        JProfiler.addEventListener("calculateBtn", event -> {
            if (event.getType() == ComponentEvent.EventType.BUTTON_CLICK) {
                int n = nProp.getValue();
                long startTime = System.nanoTime();
                long result = fibonacci(n);
                long endTime = System.nanoTime();
                long durationMs = (endTime - startTime) / 1_000_000;

                resultProp.setValue(result);
                timeProp.setValue(durationMs + "ms");

                JProfiler.updatePlaceholder("fibResult", "n", String.valueOf(n));
                JProfiler.updatePlaceholder("fibResult", "result", String.valueOf(result));
                JProfiler.updatePlaceholder("fibResult", "time", durationMs + "ms");
            }
        });

        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(nField);
        page.addToBody(calculateButton);
        page.addToBody(resultDisplay);
    }

    private static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    private static void createChartDemo() {
        Page page = JProfiler.createPage("chart", "📊 Chart Demo");

        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");
        TextComponent title = new TextComponent("<h1>Chart Components Showcase</h1>", "title");
        TextComponent description = new TextComponent("<p>Explore different chart types: Bar, Line, and Pie charts with sample data.</p>", "description");

        // Sample data
        List<Double> sampleData = Arrays.asList(45.0, 38.0, 52.0, 65.0, 48.0, 72.0);
        List<String> sampleLabels = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat");

        // Bar Chart
        ChartComponent barChart = new ChartComponent(
            sampleData,
            sampleLabels,
            ChartComponent.ChartType.BAR,
            "Sales by Day (Bar Chart)",
            "chart-item",
            "barChart"
        );

        barChart.setShowDetails(true);
        // Line Chart
        ChartComponent lineChart = new ChartComponent(
            sampleData,
            sampleLabels,
            ChartComponent.ChartType.LINE,
            "Sales Trend (Line Chart)",
            "chart-item",
            "lineChart"
        );

        lineChart.setShowDetails(false);

        // Pie Chart
        List<Double> pieData = Arrays.asList(30.0, 25.0, 20.0, 15.0, 10.0);
        List<String> pieLabels = Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E");
        
        ChartComponent pieChart = new ChartComponent(
            pieData,
            pieLabels,
            ChartComponent.ChartType.PIE,
            "Market Share (Pie Chart)",
            "chart-item",
            "pieChart"
        );

        // Add all components to page
        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(description);
        page.addToBody(barChart);
        page.addToBody(lineChart);
        page.addToBody(pieChart);
    }

    private static void createDynamicComponentsDemo() {
        Page page = JProfiler.createPage("dynamicComponents", "🎨 Dynamic Components Demo");

        ButtonComponent backButton = new ButtonComponent("← Back to Home", "back-button", ActionType.NAVIGATE_PAGE, "home");
        TextComponent title = new TextComponent("Dynamic Components Showcase", "title");
        TextComponent description = new TextComponent("Watch these components update in real-time via Server-Sent Events (SSE)", "description");

        // Dynamic text with timestamp
        DynamicTextComponent timestampText = new DynamicTextComponent("timestamp",
                "Current time: {timestamp}", "result");

        // Dynamic counter
        DynamicTextComponent counterText = new DynamicTextComponent("counter",
                "Auto-incrementing counter: {counter}", "result");

        // Dynamic random number
        DynamicTextComponent randomText = new DynamicTextComponent("random",
                "Random number: {random}", "result");

        // Update thread
        Thread updateThread = new Thread(() -> {
            int counter = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            while (true) {
                try {
                    Thread.sleep(1000);

                    String timestamp = LocalDateTime.now().format(formatter);
                    counter++;
                    int random = (int) (Math.random() * 1000);

                    JProfiler.updatePlaceholder("timestamp", "timestamp", timestamp);
                    JProfiler.updatePlaceholder("counter", "counter", String.valueOf(counter));
                    JProfiler.updatePlaceholder("random", "random", String.valueOf(random));

                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();

        page.addToBody(backButton);
        page.addToBody(title);
        page.addToBody(description);
        page.addToBody(timestampText);
        page.addToBody(counterText);
        page.addToBody(randomText);
    }

    // Config class for binding demo
    static class ServerConfig {
        public String host;
        public int port;
        public int maxConnections;
        public boolean enableSSL;
    }
}
