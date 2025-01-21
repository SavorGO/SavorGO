package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.component.ModalBorderAction;
import raven.modal.component.SimpleModalBorder;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class InputFormBasic<ModelBasic> extends JPanel {
    public InputFormBasic() {
    }
    protected abstract void init();
    protected abstract void createTitle();

}