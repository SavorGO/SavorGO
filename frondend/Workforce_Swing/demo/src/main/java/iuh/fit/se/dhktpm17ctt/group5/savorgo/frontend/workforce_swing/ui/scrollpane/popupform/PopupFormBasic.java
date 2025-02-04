package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform;

import net.miginfocom.swing.MigLayout;

import java.io.IOException;

import javax.swing.*;

/**
 * Abstract class representing a basic popup form with a scroll pane.
 *
 * @param <ModelBasic> the type of the model associated with the popup form
 */
public abstract class PopupFormBasic<ModelBasic> extends JScrollPane {
    protected JPanel contentPanel = new JPanel(new MigLayout("fillx,wrap,insets 5 30 5 30,width 360", "[fill]", ""));

    /**
     * Constructs a PopupFormBasic instance.
     * Ensures that the horizontal scroll bar is always hidden.
     */
    public PopupFormBasic() {
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Initializes the popup form.
     *
     * @throws IOException if an I/O error occurs during initialization
     */
    protected abstract void init() throws IOException;

    /**
     * Creates the title for the popup form.
     */
    protected abstract void createTitle();

    /**
     * Creates the fields for the popup form.
     *
     * @throws IOException if an I/O error occurs while creating fields
     */
    protected abstract void createFields() throws IOException;
}