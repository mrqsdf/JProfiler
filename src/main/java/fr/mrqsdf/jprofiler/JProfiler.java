package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.Component;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.server.JProfilerServer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JProfiler {

    private static JProfiler instance;
    private final List<Component> components;
    private static JProfilerServer server;
    private static int serverPort = 8080;

    private JProfiler() {
        this.components = new ArrayList<>();
    }

    public static JProfiler getInstance() {
        if (instance == null) {
            instance = new JProfiler();
        }
        return instance;
    }

    public static void addComponent(Component component) {
        getInstance().components.add(component);
    }

    public static void clearComponents() {
        getInstance().components.clear();
    }

    public static List<Component> getComponents() {
        return new ArrayList<>(getInstance().components);
    }

    public static String generateHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>JProfiler Report</title>\n");
        html.append("<style>\n");
        html.append(CSSManager.generateCSS());
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        for (Component component : getInstance().components) {
            html.append(component.getContent().render()).append("\n");
        }
        
        html.append("</body>\n</html>");
        return html.toString();
    }

    public static void saveHTMLToFile(String filePath) throws IOException {
        String html = generateHTML();
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html);
        }
        System.out.println("HTML file saved to: " + filePath);
    }

    public static void setServerPort(int port) {
        if (server != null && server.isRunning()) {
            throw new IllegalStateException("Cannot change port while server is running");
        }
        serverPort = port;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void startServer() throws Exception {
        if (server != null && server.isRunning()) {
            throw new IllegalStateException("Server is already running");
        }
        server = new JProfilerServer(serverPort);
        server.start(JProfiler::generateHTML);
        server.join();
    }

    public static void startServer(int port) throws Exception {
        setServerPort(port);
        startServer();
    }

    public static void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public static boolean isServerRunning() {
        return server != null && server.isRunning();
    }

    public static void reset() {
        instance = null;
    }

}
