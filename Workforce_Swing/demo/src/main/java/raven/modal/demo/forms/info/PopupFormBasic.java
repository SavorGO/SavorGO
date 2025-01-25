package raven.modal.demo.forms.info;

import net.miginfocom.swing.MigLayout;

import java.io.IOException;

import javax.swing.*;

public abstract class PopupFormBasic<ModelBasic> extends JScrollPane {
	protected JPanel contentPanel = new JPanel(new MigLayout("fillx,wrap,insets 5 30 5 30,width 400", "[fill]", ""));
    public PopupFormBasic() {
    }
    protected abstract void init() throws IOException;
    protected abstract void createTitle();
    protected abstract void createFields();
}