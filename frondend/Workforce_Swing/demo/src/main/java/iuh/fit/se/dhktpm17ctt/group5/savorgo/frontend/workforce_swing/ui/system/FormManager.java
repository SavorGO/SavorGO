package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system;

import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import java.io.IOException;

import javax.swing.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.Demo;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.AuthenticationController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.About;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.auth.Login;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.Form;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.FormDashboard;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.TableFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.TokenManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.UndoRedo;

public class FormManager {

    protected static final UndoRedo<Form> FORMS = new UndoRedo<>();
    private static JFrame frame;
    private static MainForm mainForm;
    private static Login login;
    private static AuthenticationController authenticationController = new AuthenticationController();
    public static void install(JFrame f) {
        install();
        frame = f;
        frame.setState(JFrame.NORMAL);
        logout();
        
        try {
            // Đọc token đã được mã hóa từ file và giải mã
            String encryptedToken = TokenManager.readEncryptedTokenFromFile();
            if (encryptedToken == null) {
                Toast.show(frame, Toast.Type.INFO, "Please login working account");
                return;
            }
            
            // Kiểm tra token đã giải mã có hợp lệ không
            if (authenticationController.verifyJwtToken() != null) {
                FormManager.login();
            } else {
                Toast.show(frame, Toast.Type.ERROR, "Token không hợp lệ.");
            }
        } catch (Exception e) {
            Toast.show(frame, Toast.Type.ERROR, "Lỗi khi giải mã token: " + e.getMessage());
        }
    }
    
    public static void doLogout() {
    	TokenManager.deleteTokenFile();
    	logout();
    }


    
    public static void reloadFrame() {
    	frame.dispose();
    	Demo.launchApplication();
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

        Drawer.setSelectedItemClass(TableFormUI.class);
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
