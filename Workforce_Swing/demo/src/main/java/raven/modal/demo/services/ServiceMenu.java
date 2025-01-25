package raven.modal.demo.services;

import java.io.IOException;
import java.util.List;

import raven.modal.demo.models.ModelMenu;

public interface ServiceMenu {
    public List<ModelMenu> getAllMenus() throws IOException;
    public ModelMenu getMenuById(String id) throws IOException; // ID kiểu String
    public List<ModelMenu> searchMenus(String search) throws IOException;
    public void createMenu(ModelMenu menu) throws IOException; // Tham số là ModelMenu
    public void updateMenu(ModelMenu menu) throws IOException;
    public void deleteMenu(String id) throws IOException; // ID kiểu String
    public void deleteMenus(List<String> ids) throws IOException; // ID kiểu String
}
