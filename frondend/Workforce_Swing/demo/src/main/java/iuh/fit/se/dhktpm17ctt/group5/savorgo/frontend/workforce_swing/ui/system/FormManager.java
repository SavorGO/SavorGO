package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system;

import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.About;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.auth.Login;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.Form;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.FormDashboard;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.MenuFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.PromotionFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.TableFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.UserFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.UndoRedo;

public class FormManager {

    protected static final UndoRedo<Form> FORMS = new UndoRedo<>();
    private static JFrame frame;
    private static MainForm mainForm;
    private static Login login;

    public static void install(JFrame f) {
        frame = f;
        install();
        logout();
        // xóa cái này để hiện login FormManager.login();
        FormManager.login();
    }

    private static void install() {
        FormSearch.getInstance().installKeyMap(getMainForm());
    }

    public static void showForm(Form form) {
        if (form != FORMS.getCurrent()) {
            FORMS.add(form);
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            mainForm.refresh();
        }
    }

    public static void undo() {
        if (FORMS.isUndoAble()) {
            Form form = FORMS.undo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void redo() {
        if (FORMS.isRedoAble()) {
            Form form = FORMS.redo();
            form.formCheck();
            form.formOpen();
            mainForm.setForm(form);
            Drawer.setSelectedItemClass(form.getClass());
        }
    }

    public static void refresh() {
        if (FORMS.getCurrent() != null) {
            FORMS.getCurrent().formRefresh();
            mainForm.refresh();
        }
    }

    public static void login() {
        Drawer.setVisible(true);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(getMainForm());

        Drawer.setSelectedItemClass(UserFormUI.class);
        frame.repaint();
        frame.revalidate();
    }

    public static void logout() {
        Drawer.setVisible(false);
        frame.getContentPane().removeAll();
        Form login = getLogin();
        login.formCheck();
        frame.getContentPane().add(login);
        FORMS.clear();
        frame.repaint();
        frame.revalidate();
    }

    public static JFrame getFrame() {
        return frame;
    }

    private static MainForm getMainForm() {
        if (mainForm == null) {
            mainForm = new MainForm();
        }
        return mainForm;
    }

    private static Login getLogin() {
        if (login == null) {
            login = new Login();
        }
        return login;
    }

    public static void showAbout() {
        ModalDialog.showModal(frame, new AdaptSimpleModalBorder(new About(), "About"),
                ModalDialog.createOption().setAnimationEnabled(false)
        );
    }
}
