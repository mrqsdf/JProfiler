package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;
import static j2html.TagCreator.rawHtml;

import java.util.List;
import j2html.tags.specialized.DivTag;

public class ChartComponent extends Component {

    public enum ChartType {
        BAR, LINE, PIE
    }

    private List<Double> data;
    private List<String> labels;
    private final ChartType type;
    private final String title;
    private final String className;
    private final String id;

    public ChartComponent(List<Double> data, List<String> labels, ChartType type, String title, String className) {
        this(data, labels, type, title, className, null);
    }

    public ChartComponent(List<Double> data, List<String> labels, ChartType type, String title, String className, String id) {
        super(createChartDiv(data, labels, type, title, className, id));
        this.data = data;
        this.labels = labels;
        this.type = type;
        this.title = title;
        this.className = className;
        this.id = id;
        if (id != null) {
            String chartSvg = switch (type) {
                case BAR -> generateBarChart(data, labels, title);
                case LINE -> generateLineChart(data, labels, title);
                case PIE -> generatePieChart(data, labels, title);
            };
            addDataAttribute("chart-svg", chartSvg);
        }
    }

    private static DivTag createChartDiv(List<Double> data, List<String> labels, ChartType type, String title, String className, String id) {
        String chartSvg = switch (type) {
            case BAR -> generateBarChart(data, labels, title);
            case LINE -> generateLineChart(data, labels, title);
            case PIE -> generatePieChart(data, labels, title);
        };

        DivTag div = div(
            rawHtml(chartSvg)
        ).withClass("chart_component " + className);
        
        if (id != null) {
            div = div.withId(id).attr("data-chart-svg", chartSvg);
        }
        
        return div;
    }

    private static String generateBarChart(List<Double> data, List<String> labels, String title) {
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
            
            // Label
            if (i < labels.size()) {
                svg.append(String.format("<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-size=\"12\">%s</text>", 
                    x + barWidth / 4, height - padding + 20, labels.get(i)));
            }
            
            // Value
            svg.append(String.format("<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-size=\"10\">%.1f</text>", 
                x + barWidth / 4, y - 5, value));
        }

        svg.append("</svg>");
        return svg.toString();
    }

    private static String generateLineChart(List<Double> data, List<String> labels, String title) {
        int width = 600;
        int height = 400;
        int padding = 60;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        double maxValue = data.stream().max(Double::compareTo).orElse(1.0);
        double minValue = data.stream().min(Double::compareTo).orElse(0.0);
        double range = maxValue - minValue;

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

        // Line path
        StringBuilder path = new StringBuilder("M");
        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            int x = padding + (i * chartWidth / (data.size() - 1));
            int y = height - padding - (int) (((value - minValue) / range) * chartHeight);
            
            if (i == 0) {
                path.append(String.format("%d,%d", x, y));
            } else {
                path.append(String.format(" L%d,%d", x, y));
            }

            // Points
            svg.append(String.format("<circle cx=\"%d\" cy=\"%d\" r=\"4\" fill=\"steelblue\"/>", x, y));
            
            // Labels
            if (i < labels.size()) {
                svg.append(String.format("<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-size=\"10\">%s</text>", 
                    x, height - padding + 20, labels.get(i)));
            }
        }

        svg.append(String.format("<path d=\"%s\" stroke=\"steelblue\" stroke-width=\"2\" fill=\"none\"/>", path));
        svg.append("</svg>");
        return svg.toString();
    }

    private static String generatePieChart(List<Double> data, List<String> labels, String title) {
        int width = 600;
        int height = 400;
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 120;

        double total = data.stream().mapToDouble(Double::doubleValue).sum();
        
        StringBuilder svg = new StringBuilder();
        svg.append(String.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">", width, height));
        
        // Title
        svg.append(String.format("<text x=\"%d\" y=\"30\" text-anchor=\"middle\" font-size=\"16\" font-weight=\"bold\">%s</text>", 
            width / 2, title));

        String[] colors = {"#4285F4", "#DB4437", "#F4B400", "#0F9D58", "#AB47BC", "#00ACC1", "#FF7043", "#9E9D24"};
        
        double currentAngle = -Math.PI / 2;
        
        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            double percentage = value / total;
            double sliceAngle = 2 * Math.PI * percentage;
            
            double x1 = centerX + radius * Math.cos(currentAngle);
            double y1 = centerY + radius * Math.sin(currentAngle);
            
            currentAngle += sliceAngle;
            
            double x2 = centerX + radius * Math.cos(currentAngle);
            double y2 = centerY + radius * Math.sin(currentAngle);
            
            int largeArcFlag = sliceAngle > Math.PI ? 1 : 0;
            
            String color = colors[i % colors.length];
            
            svg.append(String.format("<path d=\"M%d,%d L%.2f,%.2f A%d,%d 0 %d,1 %.2f,%.2f Z\" fill=\"%s\" opacity=\"0.8\" stroke=\"white\" stroke-width=\"2\"/>",
                centerX, centerY, x1, y1, radius, radius, largeArcFlag, x2, y2, color));
            
            // Legend
            int legendX = width - 150;
            int legendY = 80 + i * 25;
            svg.append(String.format("<rect x=\"%d\" y=\"%d\" width=\"15\" height=\"15\" fill=\"%s\"/>", 
                legendX, legendY, color));
            
            String label = i < labels.size() ? labels.get(i) : "Item " + (i + 1);
            svg.append(String.format("<text x=\"%d\" y=\"%d\" font-size=\"12\">%s (%.1f%%)</text>", 
                legendX + 20, legendY + 12, label, percentage * 100));
        }

        svg.append("</svg>");
        return svg.toString();
    }

    public List<Double> getData() {
        return data;
    }

    public List<String> getLabels() {
        return labels;
    }

    public ChartType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public void updateChart(List<Double> newData, List<String> newLabels) {
        this.data = newData;
        this.labels = newLabels;
        this.componentContent = createChartDiv(newData, newLabels, type, title, className, id);
        String chartSvg = switch (type) {
            case BAR -> generateBarChart(newData, newLabels, title);
            case LINE -> generateLineChart(newData, newLabels, title);
            case PIE -> generatePieChart(newData, newLabels, title);
        };
        addDataAttribute("chart-svg", chartSvg);
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
