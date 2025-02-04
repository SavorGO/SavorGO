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

/**
 * The Demo class represents the main application window for the Savorgo workforce management system.
 * It initializes the user interface and sets up the application environment.
 */
public class Demo extends JFrame {

    /** The version of the demo application. */
    public static final String DEMO_VERSION = "2.3.0-SNAPSHOT";

    /**
     * Constructor for the Demo class.
     * Initializes the application window.
     */
    public Demo() {
        init();
    }

    /**
     * Initializes the main application window settings.
     * Sets default close operation, installs the drawer, and sets the size and location of the window.
     */
    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true); // Enable full window content
        Drawer.installDrawer(this, new MyDrawerBuilder()); // Install the drawer for navigation
        FormManager.install(this); // Install the form manager for handling forms
        setSize(new Dimension(1366, 768)); // Set the size of the window
        setLocationRelativeTo(null); // Center the window on the screen
    }

    /**
     * The main method that serves as the entry point for the application.
     * Initializes preferences, sets up the look and feel, and makes the main window visible.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        DemoPreferences.init(); // Initialize demo preferences
        FlatRobotoFont.install(); // Install the Roboto font
        FlatLaf.registerCustomDefaultsSource("iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.themes"); // Register custom look and feel defaults
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13)); // Set the default font for the UI
        DemoPreferences.setupLaf(); // Set up the look and feel based on preferences
        EventQueue.invokeLater(() -> new Demo().setVisible(true)); // Create and show the main application window on the Event Dispatch Thread
    }
}