package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.auth.Login;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerHandler {
    private static HttpServer server;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Login loginForm;

    public static void startHttpServer(Login login) throws IOException {
        if (server != null) {
            return;
        }
        loginForm = login;
        server = HttpServer.create(new InetSocketAddress(5000), 0);
        server.createContext("/callback", new GoogleCallbackHandler());
        server.setExecutor(executor);
        server.start();
        System.out.println("HTTP Server started on port 5000");
    }

    public static void stopHttpServer() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("HTTP Server stopped");
        }
    }

    static class GoogleCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body><script>window.close();</script></body></html>";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();

            if (loginForm != null) {
                loginForm.handleGoogleCallback(exchange);
            }
        }
    }
}
