package raven.modal.demo.forms.other;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.modal.demo.models.ModelEmployee;

import javax.swing.*;
import java.util.function.Consumer;

public abstract class CardBasic<ModelBasic> extends JPanel {

	protected final ModelBasic modelBasic;
	protected final Consumer<ModelBasic> event;

    public CardBasic(ModelBasic modelBasic, Consumer<ModelBasic> event) {
        this.modelBasic = modelBasic;
        this.event = event;
    }

    protected abstract void init();
    protected abstract JPanel createHeader();
    protected abstract JPanel createBody();
    protected JPanel panelHeader;
    protected JPanel panelBody;
}
