package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuCategoryEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.MenuService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.MenuServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;

public class MenuController {
    private final MenuService menuService = new MenuServiceImpl();

    public ApiResponse list(String keyword, String sortBy, String sortDirection, int page, int size, String categoryFilter) {
        return menuService.list(keyword, sortBy, sortDirection, page, size, categoryFilter);
    }

    public ApiResponse getMenuById(String id) {
        return menuService.getMenuById(id);
    }

    public ApiResponse createMenu(Object[] menuData) {
        String publicId = null;
        try {
            publicId = MyImageIcon.updateImageToCloud("Menus", new File(menuData[6].toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = Menu.builder()
                .name(menuData[0].toString())
                .category((MenuCategoryEnum) menuData[1])
                .originalPrice((double) menuData[2])
                .salePrice((double) menuData[3])
                .sizes((List) menuData[4])
                .options((List) menuData[5])
                .publicId(publicId)
                .description(menuData[7].toString())
                .reservedTime(LocalDateTime.now())
                .build();
        return menuService.createMenu(menu);
    }

    public ApiResponse updateMenu(Object[] menuData) {
        ApiResponse response = menuService.getMenuById(menuData[0].toString());
        if (response.getStatus() != 200) {
            return response;
        }

        Menu menu = (Menu) response.getData();
        menu.setName(menuData[1].toString());
        menu.setStatus((MenuStatusEnum) menuData[2]);
        menu.setCategory((MenuCategoryEnum) menuData[3]);
        menu.setOriginalPrice((double) menuData[4]);
        menu.setSalePrice((double) menuData[5]);
        menu.setSizes((List) menuData[6]);
        menu.setOptions((List) menuData[7]);

        if (menuData[8] != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> MyImageIcon.updateImageToCloud("Menus", new File(menuData[8].toString())));
            try {
                menu.setPublicId(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }
        
        menu.setDescription(menuData[9].toString());
        return menuService.updateMenu(menu);
    }

    public ApiResponse deleteMenu(String id) {
        return menuService.removeMenu(id);
    }

    public ApiResponse deleteMenus(List<String> ids) {
        return menuService.removeMenus(ids);
    }

    public Object[] toTableRow(Menu menu) {
        return new Object[]{
            false,
            menu.getId(),
            new ThumbnailCell(menu.getPublicId() == null ? null : "SavorGO/Menus/" + menu.getPublicId(),
                              menu.getName(), menu.getStatus().getDisplayName(), null),
            (menu.getCategory() == null) ? "OTHER" : menu.getCategory().getDisplayName(),
            menu.getOriginalPrice(),
            menu.getSalePrice(),
            menu.getDiscountedPrice(),
            menu.getCreatedTime(),
            menu.getModifiedTime()
        };
    }
    public MyImageIcon getImage(Menu menu, int height, int width, int round) throws IOException {
        if (height == 0 || width == 0) {
            height = 50;
            width = 50;
            round = 0;
        }
        try {
            return MyImageIcon.getMyImageIconFromCloudinaryImageTag("SavorGO/Menus/" + menu.getPublicId(), height, width, round);
        } catch (URISyntaxException e) {
            return new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55, 55, 10);
        }
    }
    public ThumbnailCell getThumbnailCell(Menu menu) {
        return new ThumbnailCell(
            menu.getPublicId() == null ? null : "SavorGO/Menus/" + menu.getPublicId(),
            menu.getName(),
            menu.getStatus().getDisplayName(),
            null
        );
    }
}
