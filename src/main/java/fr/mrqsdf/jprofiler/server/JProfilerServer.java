package fr.mrqsdf.jprofiler.server;

import fr.mrqsdf.jprofiler.JProfiler;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.AsyncContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class JProfilerServer {

    private Server server;
    private int port = 8080;
    private boolean isRunning = false;

    public JProfilerServer() {
    }

    public JProfilerServer(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        if (isRunning) {
            throw new IllegalStateException("Cannot change port while server is running");
        }
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void start(Supplier<String> htmlSupplier) throws Exception {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Main servlet that serves the HTML
        ServletHolder mainHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                String pageParam = request.getParameter("id");

                // If page parameter is provided, change the current page
                if (pageParam != null && !pageParam.isEmpty()) {
                    if (JProfiler.pageExists(pageParam)) {
                        JProfiler.setCurrentPage(pageParam);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("Page not found: " + pageParam);
                        return;
                    }
                }

                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(htmlSupplier.get());
            }
        });

        // JavaScript resource servlet
        ServletHolder jsHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                response.setContentType("application/javascript;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("jprofiler-dynamic.js")) {
                    if (is != null) {
                        response.getWriter().write(new String(is.readAllBytes(), StandardCharsets.UTF_8));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("// JS file not found");
                    }
                }
            }
        });

        // SSE Updates servlet
        ServletHolder sseHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                response.setContentType("text/event-stream");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Connection", "keep-alive");
                response.setStatus(HttpServletResponse.SC_OK);

                final AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);

                final PrintWriter writer = response.getWriter();

                UpdateManager.UpdateListener listener = json -> {
                    try {
                        writer.write("data: " + json + "\n\n");
                        writer.flush();
                    } catch (Exception e) {
                        System.err.println("Error sending SSE update: " + e.getMessage());
                    }
                };

                UpdateManager.getInstance().addListener(listener);

                // Send initial connection message
                writer.write("data: {\"type\":\"connected\"}\n\n");
                writer.flush();

                asyncContext.addListener(new jakarta.servlet.AsyncListener() {
                    @Override
                    public void onComplete(jakarta.servlet.AsyncEvent event) {
                        UpdateManager.getInstance().removeListener(listener);
                    }

                    @Override
                    public void onTimeout(jakarta.servlet.AsyncEvent event) {
                        UpdateManager.getInstance().removeListener(listener);
                        asyncContext.complete();
                    }

                    @Override
                    public void onError(jakarta.servlet.AsyncEvent event) {
                        UpdateManager.getInstance().removeListener(listener);
                    }

                    @Override
                    public void onStartAsync(jakarta.servlet.AsyncEvent event) {
                    }
                });
            }
        });

        // Event receiver servlet
        ServletHolder eventHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                response.setContentType("application/json;charset=utf-8");
                response.setHeader("Access-Control-Allow-Origin", "*");
                
                try {
                    // Read JSON body
                    String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    com.google.gson.JsonObject json = new com.google.gson.Gson().fromJson(body, com.google.gson.JsonObject.class);
                    
                    String componentId = json.get("componentId").getAsString();
                    String eventType = json.get("type").getAsString();
                    
                    // Parse event data
                    java.util.Map<String, Object> eventData = new java.util.HashMap<>();
                    if (json.has("data")) {
                        com.google.gson.JsonObject data = json.getAsJsonObject("data");
                        for (String key : data.keySet()) {
                            com.google.gson.JsonElement element = data.get(key);
                            if (element.isJsonPrimitive()) {
                                com.google.gson.JsonPrimitive primitive = element.getAsJsonPrimitive();
                                if (primitive.isString()) {
                                    eventData.put(key, primitive.getAsString());
                                } else if (primitive.isBoolean()) {
                                    eventData.put(key, primitive.getAsBoolean());
                                } else if (primitive.isNumber()) {
                                    eventData.put(key, primitive.getAsDouble());
                                }
                            }
                        }
                    }
                    
                    // Create and dispatch event
                    fr.mrqsdf.jprofiler.event.ComponentEvent.EventType type = 
                        fr.mrqsdf.jprofiler.event.ComponentEvent.EventType.valueOf(eventType);
                    fr.mrqsdf.jprofiler.event.ComponentEvent event = 
                        new fr.mrqsdf.jprofiler.event.ComponentEvent(componentId, type, eventData);
                    
                    fr.mrqsdf.jprofiler.event.ComponentEventManager.getInstance().dispatchEvent(event);
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\":\"ok\"}");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
                    e.printStackTrace();
                }
            }
        });

        context.addServlet(mainHolder, "/");
        context.addServlet(jsHolder, "/jprofiler-dynamic.js");
        context.addServlet(sseHolder, "/updates");
        context.addServlet(eventHolder, "/event");
        server.setHandler(context);

        server.start();
        isRunning = true;

        System.out.println("JProfiler Server started on http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop the server");
    }

    public void stop() throws Exception {
        if (!isRunning) {
            throw new IllegalStateException("Server is not running");

        }

        server.stop();
        isRunning = false;
        System.out.println("JProfiler Server stopped");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void join() throws InterruptedException {
        if (server != null) {
            server.join();
        }
    }
}
