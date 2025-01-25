package raven.modal.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import raven.modal.demo.component.MyImageIcon;
import raven.modal.demo.models.EnumMenuCategory;
import raven.modal.demo.models.EnumMenuStatus;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.services.ServiceMenu;
import raven.modal.demo.services.impls.ServiceImplMenu;
import raven.modal.demo.utils.BusinessException;

public class ControllerMenu {

	// Using a service implementation for handling menu operations
	private ServiceMenu serviceMenu = new ServiceImplMenu();

	/**
	 * Retrieves a list of all menus.
	 * 
	 * @return List of all menus.
	 * @throws IOException if there is an issue during the process.
	 */
	public List<ModelMenu> getAllMenus() throws IOException {
		return serviceMenu.getAllMenus();
	}

	/**
	 * Retrieves a menu by its ID.
	 * 
	 * @param id The ID of the menu to retrieve.
	 * @return The menu with the specified ID.
	 * @throws IOException if there is an issue during the process.
	 */
	public ModelMenu getMenuById(String id) throws IOException {
		return serviceMenu.getMenuById(id);
	}

	/**
	 * Creates a new menu with the specified details.
	 * 
	 * @param name        The name of the menu.
	 * @param category    The category of the menu.
	 * @param description A description of the menu.
	 * @param salePrice   The original price of the menu.
	 * @param sizes       The sale price of the menu.
	 * @param options     The image URL of the menu.
	 * @param imagePath   The status of the menu.
	 * @throws IOException if there is an issue during the process.
	 */
	public void createMenu(Object[] menuData) throws IOException {
		String publicId = null;
		try {
			publicId = MyImageIcon.updateImageToCloud("Menus",new File( menuData[6].toString()));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		ModelMenu menu = ModelMenu.builder().name(menuData[0].toString()).category((EnumMenuCategory) menuData[1])
				.originalPrice((double) menuData[2]).salePrice((double) menuData[3]).sizes((List) menuData[4])
				.options((List) menuData[5]).publicId(publicId).description(menuData[7].toString())
				.reservedTime(LocalDateTime.now()).build();
		serviceMenu.createMenu(menu);
	}

	public void updateMenu(Object[] menuData) throws IOException, BusinessException {
		ModelMenu menu = getMenuById(menuData[0].toString());
		menu.setName(menuData[1].toString());
		menu.setStatus((EnumMenuStatus) menuData[2]);
		menu.setCategory((EnumMenuCategory) menuData[3]);
		menu.setOriginalPrice((double) menuData[4]);
		menu.setSalePrice((double) menuData[5]);
		menu.setSizes((List) menuData[6]);
		menu.setOptions((List) menuData[7]);
		if(menuData[8] != null) {
			String publicId = null;
			try {
				publicId = MyImageIcon.updateImageToCloud("Menus",new File( menuData[6].toString()));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			menu.setPublicId(publicId);
		}
		menu.setDescription(menuData[9].toString());
		serviceMenu.updateMenu(menu);
	}

	/**
	 * Deletes a menu by its ID.
	 * 
	 * @param id The ID of the menu to delete.
	 * @throws IOException if there is an issue during the process.
	 */
	public void deleteMenu(String id) throws IOException {
		serviceMenu.deleteMenu(id);
	}

	/**
	 * Deletes multiple menus by their IDs.
	 * 
	 * @param modelIds The list of IDs of the menus to delete.
	 * @throws IOException if there is an issue during the process.
	 */
	public void deleteMenus(List<String> modelIds) throws IOException {
		serviceMenu.deleteMenus(modelIds);
	}

	/**
	 * Searches for menus based on a search term.
	 * 
	 * @param search The search term to filter menus.
	 * @return List of menus matching the search term.
	 * @throws IOException if there is an issue during the process.
	 */
	public List<ModelMenu> searchMenus(String search) throws IOException {
		return serviceMenu.searchMenus(search);
	}

}
