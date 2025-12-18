package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.*;
import fr.mrqsdf.jprofiler.css.CSS;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.css.CSSPropertyName;
import fr.mrqsdf.jprofiler.page.Page;

import java.util.concurrent.TimeUnit;

public class JProfilerFibonacciDemo {

    public static void main(String[] args) {
        try {
            // Setup CSS
            CSS.setupDefaultStyles();
            setupStyles();

            // Create page
            Page page = JProfiler.createPage("fibonacci", "Fibonacci Calculator");

            // Header
            page.addToHeader(new TextComponent("Fibonacci Calculator", "header-title"));

            // Body - Dynamic components
            PageComponent content = new PageComponent("main-content");
            content.addToBody(new TextComponent("Fibonacci Sequence", "page-title"));
            content.addToBody(new DynamicTextComponent("fib-current", "Current: {value}", "fib-display"));
            content.addToBody(new DynamicTextComponent("fib-index", "Index: {index}", "fib-display"));
            content.addToBody(new DynamicTextComponent("fib-previous", "Previous: {prev1}, {prev2}", "fib-display"));
            content.addToBody(new DynamicTextComponent("fib-speed", "Speed: {speed} ms", "fib-display"));

            page.addToBody(content);

            // Footer
            page.addToFooter(new TextComponent("JProfiler © 2025 - Live Updates", "footer-text"));

            // Start server in async mode
            System.out.println("Starting JProfiler Server...");
            JProfiler.startServerAsync(8080);
            
            // Wait for server to start
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Server started. Visit http://localhost:8080");
            System.out.println("Starting Fibonacci calculation...\n");

            // Calculate Fibonacci with live updates
            calculateFibonacci();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void calculateFibonacci() throws InterruptedException {
        long prev1 = 0;
        long prev2 = 1;
        int speed = 1000; // milliseconds

        for (int i = 0; i < 100; i++) {
            long current = (i == 0) ? 0 : (i == 1) ? 1 : prev1 + prev2;

            // Update the UI
            JProfiler.updatePlaceholder("fib-current", "value", String.valueOf(current));
            JProfiler.updatePlaceholder("fib-index", "index", String.valueOf(i));
            JProfiler.updatePlaceholder("fib-previous", "prev1", String.valueOf(prev1));
            JProfiler.updatePlaceholder("fib-previous", "prev2", String.valueOf(prev2));
            JProfiler.updatePlaceholder("fib-speed", "speed", String.valueOf(speed));

            // Console output
            System.out.printf("Fibonacci[%d] = %d\n", i, current);

            // Prepare for next iteration
            prev1 = prev2;
            prev2 = current;

            // Wait
            TimeUnit.MILLISECONDS.sleep(speed);

            // Gradually speed up
            if (i % 10 == 0 && speed > 100) {
                speed -= 100;
            }
        }

        System.out.println("\nFibonacci calculation complete!");
        System.out.println("Server will continue running. Press Ctrl+C to stop.");

        // Keep the program running
        while (true) {
            TimeUnit.SECONDS.sleep(10);
        }
    }

    private static void setupStyles() {
        CSSManager.addRule("html, body")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "0")
            .addProperty(CSSPropertyName.PADDING.getValue(), "0")
            .addProperty(CSSPropertyName.HEIGHT.getValue(), "100%")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#1a1a2e");

        CSSManager.addRule(".page")
            .addProperty(CSSPropertyName.DISPLAY.getValue(), "flex")
            .addProperty(CSSPropertyName.FLEX_DIRECTION.getValue(), "column")
            .addProperty(CSSPropertyName.MIN_HEIGHT.getValue(), "100vh")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#eee");

        CSSManager.addRule(".page_header")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#16213e")
            .addProperty(CSSPropertyName.PADDING.getValue(), "30px")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center")
            .addProperty(CSSPropertyName.BOX_SHADOW.getValue(), "0 2px 10px rgba(0,0,0,0.3)");

        CSSManager.addRule(".header-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "32px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#0f3460")
            .addProperty(CSSPropertyName.TEXT_SHADOW.getValue(), "0 0 10px #e94560");

        CSSManager.addRule(".main-content")
            .addProperty(CSSPropertyName.FLEX.getValue(), "1")
            .addProperty(CSSPropertyName.DISPLAY.getValue(), "flex")
            .addProperty(CSSPropertyName.FLEX_DIRECTION.getValue(), "column")
            .addProperty(CSSPropertyName.ALIGN_ITEMS.getValue(), "center")
            .addProperty(CSSPropertyName.JUSTIFY_CONTENT.getValue(), "center")
            .addProperty(CSSPropertyName.PADDING.getValue(), "40px");

        CSSManager.addRule(".page-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "28px")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#e94560")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "40px")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center");

        CSSManager.addRule(".fib-display")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "24px")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#16213e")
            .addProperty(CSSPropertyName.PADDING.getValue(), "20px 40px")
            .addProperty(CSSPropertyName.MARGIN.getValue(), "10px")
            .addProperty(CSSPropertyName.BORDER_RADIUS.getValue(), "8px")
            .addProperty(CSSPropertyName.BORDER_LEFT.getValue(), "4px solid #e94560")
            .addProperty(CSSPropertyName.MIN_WIDTH.getValue(), "400px")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center")
            .addProperty(CSSPropertyName.BOX_SHADOW.getValue(), "0 4px 6px rgba(0,0,0,0.3)")
            .addProperty(CSSPropertyName.TRANSITION.getValue(), "all 0.3s ease");

        CSSManager.addRule(".page_footer")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#16213e")
            .addProperty(CSSPropertyName.PADDING.getValue(), "20px")
            .addProperty(CSSPropertyName.TEXT_ALIGN.getValue(), "center")
            .addProperty(CSSPropertyName.COLOR.getValue(), "#888");
    }
}
