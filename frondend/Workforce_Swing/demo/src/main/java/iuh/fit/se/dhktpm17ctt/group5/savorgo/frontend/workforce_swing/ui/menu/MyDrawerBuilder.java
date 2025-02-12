package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.menu;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.Demo;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.AuthenticationController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.*;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.AllForms;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.TokenManager;
import raven.modal.drawer.DrawerPanel;
import raven.modal.drawer.data.Item;
import raven.modal.drawer.data.MenuItem;
import raven.modal.drawer.menu.*;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.header.SimpleProfileData;
import raven.extras.AvatarIcon;
import raven.modal.option.Option;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.Token;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

	private final int SHADOW_SIZE = 12;

	public MyDrawerBuilder() {
		super(createSimpleMenuOption());
	}

	@Override
	public SimpleProfileData getSimpleHeaderData() {
		AvatarIcon icon = new AvatarIcon(getClass().getResource("/logos/png/SavorGO.png"), 50, 50, 3.5f);
		icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
		icon.setBorder(2, 2);

		changeAvatarIconBorderColor(icon);

		UIManager.addPropertyChangeListener(evt -> {
			if (evt.getPropertyName().equals("lookAndFeel")) {
				changeAvatarIconBorderColor(icon);
			}
		});

		return new SimpleProfileData().setIcon(icon).setTitle("SavorGO").setDescription("Book Fast, Savor More!");
	}

	private void changeAvatarIconBorderColor(AvatarIcon icon) {
		icon.setBorderColor(new AvatarIcon.BorderColor(UIManager.getColor("Component.accentColor"), 0.7f));
	}

	private AuthenticationController authenticationController;
	private UserController userController;
	private User currentUser;

	private void login() {
	    try {
	        authenticationController = new AuthenticationController();
	        userController = new UserController();

	        // Gọi phương thức verifyJwtToken mà không cần phải truyền token
	        currentUser = authenticationController.verifyJwtToken();

	        if (currentUser == null) {
	            return;
	        }

	        // Nếu xác thực thành công, bạn có thể tiếp tục với đăng nhập
	    } catch (SecurityException e) {
	        System.err.println("Lỗi bảo mật khi giải mã token: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("Lỗi không xác định khi đăng nhập: " + e.getMessage());
	    }
	}


	@Override
	public SimpleProfileData getSimpleFooterData() {
		System.out.println("debug 1");
		login();
		if (currentUser == null) {
			return new SimpleProfileData().setTitle("Guest").setDescription("Book Fast, Savor More!");
		}
		AvatarIcon icon;
		try {
			icon = new AvatarIcon(userController.getImage(currentUser, 50, 50, 30).getIcon(), 50, 50, 3.5f);
			if (icon == null)
				throw new IOException("User avatar is null");
		} catch (IOException e) {
			e.printStackTrace();
			icon = new AvatarIcon(getClass().getResource("/logos/png/default-avatar.png"), 50, 50, 3.5f);
		}

		icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
		icon.setBorder(2, 2);
		changeAvatarIconBorderColor(icon);
		final AvatarIcon finalIcon = icon;
		UIManager.addPropertyChangeListener(evt -> {
			if ("lookAndFeel".equals(evt.getPropertyName())) {
				changeAvatarIconBorderColor(finalIcon);
			}
		});

		return new SimpleProfileData().setIcon(icon).setTitle(currentUser.getRole().getDisplayName() + ": "
				+ currentUser.getFirstName() + " " + currentUser.getLastName()).setDescription(currentUser.getEmail());
	}

	public static MenuOption createSimpleMenuOption() {

		// create simple menu option
		MenuOption simpleMenuOption = new MenuOption();

		MenuItem items[] = new MenuItem[] { new Item.Label("MAIN"),
				new Item("Dashboard", "dashboard.svg", FormDashboard.class), new Item("About", "about.svg"),
				new Item.Label("Management"), new Item("Manage Tables", "table.svg", TableFormUI.class),
				new Item("Manage Users", "user.svg", UserFormUI.class),
				new Item("Manage Menus", "menu.svg", MenuFormUI.class),
				new Item("Manage Promotions", "promotion.svg", PromotionFormUI.class), new Item.Label("OTHER"),
				new Item("General Setting", "setting.svg").subMenu("Theme",SettingFormUI.class).subMenu("Language").subMenu("Account",AccountFormUI.class),
		};

		simpleMenuOption.setMenuStyle(new MenuStyle() {

			@Override
			public void styleMenu(JComponent component) {
				component.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
			}
		});

		simpleMenuOption.getMenuStyle().setDrawerLineStyleRenderer(new DrawerStraightDotLineStyle());
		simpleMenuOption.setMenuItemAutoSelectionMode(MenuOption.MenuItemAutoSelectionMode.SELECT_SUB_MENU_LEVEL);
		simpleMenuOption.addMenuEvent(new MenuEvent() {
			@Override
			public void selected(MenuAction action, int[] index) {
				System.out.println("Drawer menu selected " + Arrays.toString(index));
				Class<?> itemClass = action.getItem().getItemClass();
				int i = index[0];
				if (i == 1) {
					action.consume();
					FormManager.showAbout();
					return;
				} else if (i == 9) {
					action.consume();
					FormManager.logout();
					return;
				}
				if (itemClass == null || !Form.class.isAssignableFrom(itemClass)) {
					action.consume();
					return;
				}
				Class<? extends Form> formClass = (Class<? extends Form>) itemClass;
				FormManager.showForm(AllForms.getForm(formClass));
			}
		});

		simpleMenuOption.setMenus(items).setBaseIconPath("icons/svg").setIconScale(0.7f);

		return simpleMenuOption;
	}

	@Override
	public int getDrawerWidth() {
		return 270 + SHADOW_SIZE;
	}

	@Override
	public int getDrawerCompactWidth() {
		return 80 + SHADOW_SIZE;
	}

	@Override
	public int getOpenDrawerAt() {
		return 1000;
	}

	@Override
	public Option getOption() {
		Option option = super.getOption();
		option.setOpacity(0.3f);
		option.getBorderOption().setShadowSize(new Insets(0, 0, 0, SHADOW_SIZE));
		return option;
	}

	@Override
	public boolean openDrawerAtScale() {
		return false;
	}

	@Override
	public void build(DrawerPanel drawerPanel) {
		drawerPanel.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
	}

	private static String getDrawerBackgroundStyle() {
		return "" + "[light]background:tint($Panel.background,20%);" + "[dark]background:tint($Panel.background,5%);";
	}
}
