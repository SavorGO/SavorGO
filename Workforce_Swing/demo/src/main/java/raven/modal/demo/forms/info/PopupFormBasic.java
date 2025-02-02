package raven.modal.demo.forms.info;

import net.miginfocom.swing.MigLayout;

import java.io.IOException;

import javax.swing.*;

public abstract class PopupFormBasic<ModelBasic> extends JScrollPane {
	protected JPanel contentPanel = new JPanel(new MigLayout("fillx,wrap,insets 5 30 5 30,width 360", "[fill]", ""));
	public PopupFormBasic() {
	    // Đảm bảo thanh cuộn ngang luôn ẩn
	    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

    protected abstract void init() throws IOException;
    protected abstract void createTitle();
    protected abstract void createFields() throws IOException;
}