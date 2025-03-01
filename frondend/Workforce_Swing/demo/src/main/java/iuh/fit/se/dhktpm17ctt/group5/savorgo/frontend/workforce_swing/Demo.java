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

public class Demo extends JFrame {

    public static final String DEMO_VERSION = "2.3.0-SNAPSHOT";

    public Demo() {
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true); // Enable full window content
        Drawer.installDrawer(this, new MyDrawerBuilder()); // Install the drawer for navigation
        FormManager.install(this); // Install the form manager for handling forms
        setSize(new Dimension(1366, 768)); // Set the size of the window
        setLocationRelativeTo(null); // Center the window on the screen
    }

    public static void launchApplication() {
        DemoPreferences.init(); // Initialize demo preferences
        FlatRobotoFont.install(); // Install the Roboto font
        FlatLaf.registerCustomDefaultsSource("iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.themes"); // Register custom look and feel defaults
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13)); // Set the default font for the UI
        DemoPreferences.setupLaf(); // Set up the look and feel based on preferences
        SwingUtilities.invokeLater(() -> new Demo().setVisible(true)); // Run the UI on the Event Dispatch Thread
    }

    public static void main(String[] args) {
        launchApplication();
    }
}
