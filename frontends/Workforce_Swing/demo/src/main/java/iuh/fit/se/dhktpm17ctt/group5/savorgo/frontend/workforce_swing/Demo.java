package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.menu.MyDrawerBuilder;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DemoPreferences;
import raven.modal.Drawer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Demo extends JFrame {

    public static final String DEMO_VERSION = "2.3.0-SNAPSHOT";
    private static Demo instance;

    public Demo() {
        init();
    }

    private void init() {
        try {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Đổi thành EXIT_ON_CLOSE
            getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
            Drawer.installDrawer(this, new MyDrawerBuilder());
            FormManager.install(this);
            setSize(new Dimension(1366, 768));
            setLocationRelativeTo(null);
            System.out.println("Demo initialized successfully");
        } catch (java.awt.HeadlessException e) {
            System.err.println("HeadlessException: Cannot initialize GUI. Ensure DISPLAY is set and X11 server is running.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error initializing Demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void prepareLookAndFeel() {
        DemoPreferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        DemoPreferences.setupLaf();
    }

    public static void reload() {
        System.out.println("Reloading Demo...");
        prepareLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            try {
                if (instance != null) {
                    System.out.println("Disposing old Demo instance");
                    instance.dispose();
                }
                System.out.println("Creating new Demo instance");
                instance = new Demo();
                System.out.println("Showing new Demo instance");
                instance.setVisible(true);
                FormManager.install(instance);
                System.out.println("Demo reloaded successfully");
            } catch (Exception e) {
                System.err.println("Error during reload: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static final int port = 8083;

    private static void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            if (instance == null) {
                                System.out.println("Creating new Demo instance");
                                instance = new Demo();
                            }
                            if (!instance.isVisible()) {
                                System.out.println("Showing Demo instance");
                                instance.setVisible(true);
                            } else {
                                System.out.println("Demo instance already visible");
                            }
                        } catch (Exception e) {
                            System.err.println("Error in HttpHandler: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });

                    String response = "Swing UI launched!";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server is listening on http://localhost:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        prepareLookAndFeel();
        startHttpServer();
    }
}