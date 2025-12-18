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

        context.addServlet(mainHolder, "/");
        context.addServlet(jsHolder, "/jprofiler-dynamic.js");
        context.addServlet(sseHolder, "/updates");
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
