package fr.mrqsdf.jprofiler.server;

import fr.mrqsdf.jprofiler.JProfiler;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        context.addServlet(mainHolder, "/*");
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
