package fr.mrqsdf.jprofiler;

import fr.mrqsdf.jprofiler.component.Component;
import fr.mrqsdf.jprofiler.css.CSSManager;
import fr.mrqsdf.jprofiler.page.Page;
import fr.mrqsdf.jprofiler.page.PageManager;
import fr.mrqsdf.jprofiler.server.JProfilerServer;
import fr.mrqsdf.jprofiler.server.UpdateManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JProfiler {

    private static JProfiler instance;
    private final List<Component> components;
    private static JProfilerServer server;
    private static int serverPort = 8080;
    private static Thread serverThread;

    private JProfiler() {
        this.components = new ArrayList<>();
    }

    public static JProfiler getInstance() {
        if (instance == null) {
            instance = new JProfiler();
        }
        return instance;
    }

    // ===== Component methods (legacy support) =====
    public static void addComponent(Component component) {
        getInstance().components.add(component);
    }

    public static void clearComponents() {
        getInstance().components.clear();
    }

    public static List<Component> getComponents() {
        return new ArrayList<>(getInstance().components);
    }

    // ===== Page methods =====
    public static Page createPage(String id, String title) {
        return PageManager.createPage(id, title);
    }

    public static Page getPage(String id) {
        return PageManager.getPage(id);
    }

    public static Page getCurrentPage() {
        return PageManager.getCurrentPage();
    }

    public static void setCurrentPage(String id) {
        PageManager.setCurrentPage(id);
    }

    public static String getCurrentPageId() {
        return PageManager.getCurrentPageId();
    }

    public static boolean pageExists(String id) {
        return PageManager.pageExists(id);
    }

    public static List<Page> getAllPages() {
        return PageManager.getAllPages();
    }

    public static void removePage(String id) {
        PageManager.removePage(id);
    }

    public static void clearPages() {
        PageManager.clearAll();
    }

    // ===== Dynamic updates =====
    public static void updateComponent(String componentId, String text) {
        UpdateManager.getInstance().updateTextComponent(componentId, text);
    }

    public static void updatePlaceholder(String componentId, String placeholder, String value) {
        UpdateManager.getInstance().updatePlaceholder(componentId, placeholder, value);
    }

    public static void updateHTML(String componentId, String html) {
        UpdateManager.getInstance().updateHTML(componentId, html);
    }

    // ===== HTML generation =====
    public static String generateHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        
        Page currentPage = getCurrentPage();
        String title = currentPage != null ? currentPage.getTitle() : "JProfiler Report";
        html.append("<title>").append(title).append("</title>\n");
        
        html.append("<style>\n");
        html.append(CSSManager.generateCSS());
        html.append("</style>\n");
        
        html.append("<script>\n");
        html.append(getJavaScriptCode());
        html.append("</script>\n");
        html.append("<script src=\"/jprofiler-dynamic.js\"></script>\n");
        
        html.append("</head>\n<body>\n");
        
        if (currentPage != null) {
            // Generate from current page
            html.append(currentPage.render().render()).append("\n");
        } else if (!getInstance().components.isEmpty()) {
            // Fallback to legacy components
            for (Component component : getInstance().components) {
                html.append(component.getContent().render()).append("\n");
            }
        }
        
        html.append("</body>\n</html>");
        return html.toString();
    }

    private static String getJavaScriptCode() {
        return """
            function navigateToPage(pageId) {
                fetch('/page?id=' + pageId, {
                    method: 'GET'
                })
                .then(response => response.text())
                .then(html => {
                    document.body.innerHTML = html;
                })
                .catch(error => console.error('Error navigating to page:', error));
            }

            function submitForm(formId) {
                const form = document.getElementById(formId);
                if (form) {
                    form.submit();
                } else {
                    console.error('Form with id ' + formId + ' not found');
                }
            }
            """;
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
        startServerAsyncInternal(serverPort);
        if (serverThread != null) {
            serverThread.join();
        }
    }

    public static void startServer(int port) throws Exception {
        setServerPort(port);
        startServer();
    }

    public static void startServerAsync() throws Exception {
        startServerAsyncInternal(serverPort);
    }

    public static void startServerAsync(int port) throws Exception {
        setServerPort(port);
        startServerAsyncInternal(serverPort);
    }

    private static void startServerAsyncInternal(int port) throws Exception {
        if (server != null && server.isRunning()) {
            throw new IllegalStateException("Server is already running");
        }

        server = new JProfilerServer(port);
        serverThread = new Thread(() -> {
            try {
                server.start(JProfiler::generateHTML);
            } catch (Exception e) {
                throw new RuntimeException("Failed to start JProfiler server", e);
            }
        }, "jprofiler-server-thread");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.join();
            }
            server = null;
            serverThread = null;
        }
    }

    public static boolean isServerRunning() {
        return server != null && server.isRunning();
    }

    public static void reset() {
        instance = null;
        server = null;
        serverThread = null;
    }

}
