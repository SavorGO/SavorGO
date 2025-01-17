package raven.modal.demo.forms;

import net.miginfocom.swing.MigLayout;
import raven.modal.demo.system.Form;
import raven.modal.demo.utils.SystemForm;

import javax.swing.*;

@SystemForm(name = "Form Input2", description = "input form not yet update")
public class FormInput2 extends Form {

    public FormInput2() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel text = new JLabel("Input 2");
        add(text);
    }
}
