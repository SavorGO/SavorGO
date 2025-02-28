package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.PromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionDiscountTypeEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a card for a promotion item.
 */
public class CardPromotion extends CardBasic<Promotion> {
	private Menu menu;
	private MenuController menuController;
	private PromotionController promotionController;
	public CardPromotion(Promotion modelPromotion) {
		super(modelPromotion);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		menuController = new MenuController();
		promotionController = new PromotionController();
		try {
			menu = promotionController.getMenuByPromotion(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		setupCardStyle();
		setLayout(new MigLayout("", "fill"));
		panelHeader = createHeader();
		panelBody = createBody();
		add(panelHeader);
		add(panelBody);
	}

	@Override
	protected JPanel createHeader() {
		
		JPanel header = new JPanel(new MigLayout("fill,insets 0", "[fill]", "[top]"));
		header.putClientProperty(FlatClientProperties.STYLE, "background:null");

		JLabel label = new JLabel();
		try {
			label.setIcon(menuController.getImage(menu,130, 130, 20));
		} catch (IOException e) {
			label.setIcon(null);
			e.printStackTrace();
		}
		header.add(label);
		return header;
	}

	@Override
	protected JPanel createBody() {
	    JPanel body = new JPanel(new MigLayout("wrap, align left", "[left]", "[][][][][grow]"));
		body.putClientProperty(FlatClientProperties.STYLE, "background:null;");
		addNameLabel(body);
		addStatusLabel(body);
		addOriginalPriceLabel(body);
		addSalePriceLabel(body);
		addDiscountValueLabel(body);
		addDiscountedPriceLabel(body);
		addMenuIdLabel(body);
		addStartDateLabel(body);
		addEndDateLabel(body);
		addCreatedDateLabel(body);
		addModifiedDateLabel(body);

		return body;
	}

	private void addNameLabel(JPanel body) {
		JLabel nameLabel = new JLabel(model.getName());
		nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
		body.add(nameLabel, "align center");
	}

	private void addOriginalPriceLabel(JPanel body) {
		JLabel originalPriceLabel = new JLabel("Original Price: " + menu.getOriginalPrice());
		originalPriceLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(originalPriceLabel);
	}

	private void addSalePriceLabel(JPanel body) {
		JLabel originalPriceLabel = new JLabel("Sale Price: " + menu.getSalePrice());
		originalPriceLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(originalPriceLabel);
	}

	private void addDiscountValueLabel(JPanel body) {
		JLabel discountValueLabel = new JLabel(
				"Discount: " + (model.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.PERCENTAGE ? model.getDiscountValue() + "%"
						: model.getDiscountValue()));
		discountValueLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(discountValueLabel);
	}
	
	private void addDiscountedPriceLabel(JPanel body) {
		double discountedPrice = 0;
		double profit = menu.getSalePrice() - menu.getOriginalPrice();
		
		if(model.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.PERCENTAGE) {
			double discountAmount = profit * model.getDiscountValue() / 100;
			discountedPrice = menu.getSalePrice() - discountAmount;
		}else if(model.getPromotionDiscountTypeEnum() == PromotionDiscountTypeEnum.FIXED_AMOUNT) {
			double discountAmount = model.getDiscountValue();
			discountedPrice = menu.getSalePrice() - discountAmount;
		}
		if(discountedPrice < menu.getOriginalPrice()) discountedPrice = menu.getOriginalPrice();
		JLabel discountedPriceLabel = new JLabel("Discounted Price: " + discountedPrice);
		discountedPriceLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(discountedPriceLabel);
	}

	private void addMenuIdLabel(JPanel body) {
		JLabel menuIdLabel = new JLabel("Menu ID: " + model.getMenuId());
		menuIdLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(menuIdLabel);
	}

	private void addStartDateLabel(JPanel body) {
		if (model.getStartDate() == null)
			return;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		JLabel startDateLabel = new JLabel("Start Date: " + model.getStartDate().format(formatter));
		startDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(startDateLabel);
	}

	private void addEndDateLabel(JPanel body) {
		if (model.getEndDate() == null)
			return;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		JLabel endDateLabel = new JLabel("End Date: " + model.getEndDate().format(formatter));
		endDateLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(endDateLabel);
	}

	private void addStatusLabel(JPanel body) {
		JLabel statusLabel = new JLabel(model.getStatus().getDisplayName());
		statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
		statusLabel.setForeground(getStatusColor(model.getStatus().getDisplayName()));
		body.add(statusLabel);
	}

	private void addCreatedDateLabel(JPanel body) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		JLabel createdLabel = new JLabel("Created At: " + model.getCreatedTime().format(formatter));
		createdLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(createdLabel);
	}

	private void addModifiedDateLabel(JPanel body) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		JLabel modifiedLabel = new JLabel("Modified At: " + model.getModifiedTime().format(formatter));
		modifiedLabel.putClientProperty(FlatClientProperties.STYLE, "font:medium;");
		body.add(modifiedLabel);
	}
}