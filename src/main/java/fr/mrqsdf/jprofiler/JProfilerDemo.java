package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.ChartComponent;
import fr.mrqsdf.jprofiler.component.Component;
import fr.mrqsdf.jprofiler.component.MultiTextComponent;
import fr.mrqsdf.jprofiler.component.PageComponent;
import fr.mrqsdf.jprofiler.component.TextComponent;
import fr.mrqsdf.jprofiler.css.CSS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JProfilerDemo {

    public static void run() {
        try {
            // Setup default CSS styles
            CSS.setupDefaultStyles();

            // Create a demo page
            PageComponent page = new PageComponent("demo-page");

            // Add header components
            page.addToHeader(new TextComponent("JProfiler Demo", "title"));
            page.addToHeader(new TextComponent("A comprehensive HTML generation library", "subtitle"));

            // Create body components
            List<Component> bodyComponents = new ArrayList<>();

            // Add text components
            bodyComponents.add(new TextComponent("Welcome to JProfiler", "section-title"));
            bodyComponents.add(new TextComponent("This is a demonstration of all available components in JProfiler library.", "description"));

            // Add a bar chart
            bodyComponents.add(new TextComponent("Sales Chart - Bar", "chart-title"));
            bodyComponents.add(new ChartComponent(
                List.of(100.0, 250.0, 180.0, 320.0, 200.0),
                List.of("Jan", "Feb", "Mar", "Apr", "May"),
                ChartComponent.ChartType.BAR,
                "Monthly Sales",
                "bar-chart"
            ));

            // Add a line chart
            bodyComponents.add(new TextComponent("Growth Trend - Line", "chart-title"));
            bodyComponents.add(new ChartComponent(
                List.of(10.0, 15.0, 12.0, 25.0, 30.0, 28.0),
                List.of("W1", "W2", "W3", "W4", "W5", "W6"),
                ChartComponent.ChartType.LINE,
                "Weekly Growth",
                "line-chart"
            ));

            // Add a pie chart
            bodyComponents.add(new TextComponent("Market Distribution - Pie", "chart-title"));
            bodyComponents.add(new ChartComponent(
                List.of(35.0, 25.0, 20.0, 20.0),
                List.of("Product A", "Product B", "Product C", "Product D"),
                ChartComponent.ChartType.PIE,
                "Market Share",
                "pie-chart"
            ));

            // Add multi-text component
            bodyComponents.add(new TextComponent("Multi-Text Example", "chart-title"));
            bodyComponents.add(new MultiTextComponent(
                List.of(
                    new TextComponent("• Feature 1: Easy to use", "feature"),
                    new TextComponent("• Feature 2: Type-safe CSS", "feature"),
                    new TextComponent("• Feature 3: Beautiful HTML generation", "feature"),
                    new TextComponent("• Feature 4: Flexible components", "feature")
                ),
                "features-list"
            ));

            // Add all body components to page
            page.addAllToBody(bodyComponents);

            // Add footer components
            page.addToFooter(new TextComponent("JProfiler © 2025 - All rights reserved", "footer-text"));
            page.addToFooter(new TextComponent("Version 1.0.0", "footer-version"));

            // Add the page to JProfiler
            JProfiler.addComponent(page);

            // Generate and save HTML
            String filePath = "output.html";
            JProfiler.saveHTMLToFile(filePath);
            System.out.println("Demo HTML generated successfully!");

        } catch (IOException e) {
            System.err.println("Error generating HTML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        run();
    }
}
