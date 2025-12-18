package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.*;
import fr.mrqsdf.jprofiler.component.TextComponent;
import fr.mrqsdf.jprofiler.css.CSS;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.css.CSSPropertyName;
import fr.mrqsdf.jprofiler.page.Page;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JProfilerDynamicComponentsDemo {

    public static void main(String[] args) {
        try {
            // Setup CSS
            CSS.setupDefaultStyles();
            setupStyles();

            // Create page with all dynamic component types
            Page page = JProfiler.createPage("dynamic", "Dynamic Components Demo");

            // Header
            page.addToHeader(new TextComponent("Dynamic Components System", "header-title"));

            // Body content
            PageComponent content = new PageComponent("main-content");

            // Text Component
            TextComponent textComp = new TextComponent("Initial text content", "section-text", "text-demo");
            content.addToBody(new TextComponent("Text Component", "section-title"));
            content.addToBody(textComp);

            // Button Component
            ButtonComponent buttonComp = new ButtonComponent("Click Me (0)", "primary", null, null, "button-demo");
            content.addToBody(new TextComponent("Button Component", "section-title"));
            content.addToBody(buttonComp);

            // Text Field Component
            TextFieldComponent fieldComp = new TextFieldComponent("field-demo", "Enter value...", "input-field", "text", "");
            content.addToBody(new TextComponent("Text Field Component", "section-title"));
            content.addToBody(fieldComp);

            // Checkbox Component
            CheckboxComponent checkboxComp = new CheckboxComponent("checkbox-demo", "Enable feature", "feature-checkbox", false);
            content.addToBody(new TextComponent("Checkbox Component", "section-title"));
            content.addToBody(checkboxComp);

            // Image Component - Create a simple test image
            BufferedImage testImage = createTestImage(200, 150, Color.BLUE, "Initial");
            ImageComponent imageComp = new ImageComponent(testImage, "Test Image", "demo-image", "image-demo");
            content.addToBody(new TextComponent("Image Component", "section-title"));
            content.addToBody(imageComp);

            // Chart Component
            List<Double> chartData = new ArrayList<>();
            List<String> chartLabels = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                chartData.add(Math.random() * 100);
                chartLabels.add("Item " + (i + 1));
            }
            ChartComponent chartComp = new ChartComponent(chartData, chartLabels, ChartComponent.ChartType.BAR, "Sample Chart", "demo-chart", "chart-demo");
            content.addToBody(new TextComponent("Chart Component", "section-title"));
            content.addToBody(chartComp);

            // Dynamic text with placeholders
            DynamicTextComponent dynamicComp = new DynamicTextComponent("dynamic-demo", "Counter: {count}, Status: {status}", "demo-dynamic");
            content.addToBody(new TextComponent("Dynamic Text Component", "section-title"));
            content.addToBody(dynamicComp);

            page.addToBody(content);

            // Footer
            page.addToFooter(new TextComponent("JProfiler © 2025 - Complete Dynamic System", "footer-text"));

            // Start server
            System.out.println("Starting JProfiler Server with Dynamic Components Demo...");
            JProfiler.startServerAsync(8080);

            TimeUnit.SECONDS.sleep(2);
            System.out.println("Server started. Visit http://localhost:8080");
            System.out.println("\nStarting component updates in 2 seconds...\n");
            TimeUnit.SECONDS.sleep(2);

            // Run updates for each component type
            runDynamicUpdates();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runDynamicUpdates() throws InterruptedException {
        int counter = 0;
        int iterations = 50;

        for (int i = 0; i < iterations; i++) {
            counter++;

            // Text Component
            JProfiler.updateTextContent("text-demo", "Text updated: iteration " + counter);

            // Button Component
            JProfiler.updateButtonText("button-demo", "Clicked " + counter + " times");

            // Text Field Component
            JProfiler.updateFieldValue("field-demo", "Value: " + counter);

            // Checkbox Component - Toggle every 5 iterations
            boolean isChecked = (counter % 5 == 0);
            JProfiler.updateCheckboxState("checkbox-demo", isChecked);

            // Image Component - Update with different colors
            Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
            BufferedImage newImage = createTestImage(200, 150, colors[counter % colors.length], "Iteration " + counter);
            String base64Image = imageToBase64(newImage);
            if (base64Image != null) {
                JProfiler.updateImage("image-demo", base64Image);
            }

            // Chart Component - Update with new random data
            List<Double> newChartData = new ArrayList<>();
            List<String> newChartLabels = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                newChartData.add(Math.random() * 100);
                newChartLabels.add("Item " + (j + 1));
            }
            String svgChart = generateBarChartSVG(newChartData, newChartLabels, "Updated Chart " + counter);
            JProfiler.updateChart("chart-demo", svgChart);

            // Dynamic Text Component with placeholders
            JProfiler.updatePlaceholder("dynamic-demo", "count", String.valueOf(counter));
            JProfiler.updatePlaceholder("dynamic-demo", "status", counter % 2 == 0 ? "even" : "odd");

            System.out.printf("Iteration %d: All components updated%n", counter);

            TimeUnit.MILLISECONDS.sleep(500);
        }

        System.out.println("\nComponent updates complete!");
        System.out.println("Server will continue running. Press Ctrl+C to stop.");

        // Keep running
        while (true) {
            TimeUnit.SECONDS.sleep(10);
        }
    }

    private static BufferedImage createTestImage(int width, int height, Color color, String text) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);

        g2d.dispose();
        return image;
    }

    private static String imageToBase64(BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            System.err.println("Error converting image to base64: " + e.getMessage());
            return null;
        }
    }

    private static String generateBarChartSVG(List<Double> data, List<String> labels, String title) {
        int width = 600;
        int height = 400;
        int padding = 60;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        double maxValue = data.stream().max(Double::compareTo).orElse(1.0);
        int barWidth = chartWidth / data.size();

        StringBuilder svg = new StringBuilder();
        svg.append(String.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">", width, height));

        // Title
        svg.append(String.format("<text x=\"%d\" y=\"30\" text-anchor=\"middle\" font-size=\"16\" font-weight=\"bold\">%s</text>",
            width / 2, title));

        // Axes
        svg.append(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"black\" stroke-width=\"2\"/>",
            padding, padding, padding, height - padding));
        svg.append(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"black\" stroke-width=\"2\"/>",
            padding, height - padding, width - padding, height - padding));

        // Bars
        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            int barHeight = (int) ((value / maxValue) * chartHeight);
            int x = padding + i * barWidth + barWidth / 4;
            int y = height - padding - barHeight;

            svg.append(String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"steelblue\" opacity=\"0.8\"/>",
                x, y, barWidth / 2, barHeight));

            if (i < labels.size()) {
                svg.append(String.format("<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-size=\"12\">%s</text>",
                    x + barWidth / 4, height - padding + 20, labels.get(i)));
            }
        }

        svg.append("</svg>");
        return svg.toString();
    }

    private static void setupStyles() {
        CSSManager.addRule(".section-title")
            .addProperty(CSSPropertyName.FONT_SIZE.getValue(), "18px")
            .addProperty(CSSPropertyName.FONT_WEIGHT.getValue(), "bold")
            .addProperty(CSSPropertyName.MARGIN_TOP.getValue(), "20px")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "10px");

        CSSManager.addRule(".section-text")
            .addProperty(CSSPropertyName.PADDING.getValue(), "10px")
            .addProperty(CSSPropertyName.BACKGROUND_COLOR.getValue(), "#f0f0f0")
            .addProperty(CSSPropertyName.BORDER_RADIUS.getValue(), "4px")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "10px");

        CSSManager.addRule(".demo-image, .demo-chart, .demo-dynamic")
            .addProperty(CSSPropertyName.PADDING.getValue(), "10px")
            .addProperty(CSSPropertyName.MARGIN_BOTTOM.getValue(), "15px");
    }
}
