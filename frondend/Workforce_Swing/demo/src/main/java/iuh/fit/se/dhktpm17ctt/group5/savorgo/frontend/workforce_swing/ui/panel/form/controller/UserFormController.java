package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardUser;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.UserFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateMenuInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateUserInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.UserInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.UpdateUserInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFormController {
	private UserFormUI formUserUI;
	private UserController userController = new UserController();
	private static final int DEBOUNCE_DELAY = 1000;
	private Timer debounceTimer;
	private volatile boolean isLoading = false;

	/**
	 * Constructs a UserFormController with the specified UserFormUI.
	 * 
	 * @param formUser The UserFormUI instance to control.
	 */
	public UserFormController(UserFormUI formUser) {
		this.formUserUI = formUser;
	}

	/**
	 * Loads data based on the search term.
	 * 
	 * @param searchTerm the term to search for users.
	 */
	public void loadData(String searchTerm) {
		if (isLoading) {
			return;
		}
		isLoading = true;
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        executor.submit(() -> {
			try {
				List<User> users = fetchUsers(searchTerm);
				if (users == null || users.isEmpty()) {
					Toast.show(formUserUI, Toast.Type.INFO, "No users found in the database or in search");
					return;
				}
				SwingUtilities.invokeLater(() -> {
                    formUserUI.getPanelCard().removeAll();
                    try {
                        populateCardUser(users);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				isLoading = false;
			}
		});
        
        executor.submit(() -> {
            try {
                List<User> users = fetchUsers(searchTerm);
                SwingUtilities.invokeLater(() -> {
                    formUserUI.getTableModel().setRowCount(0);
                    populateBasicMenu(users);
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    executor.shutdown();
                    System.gc();
                    isLoading = false;
                });
            }
        }).start();
	}
	
	/** 
     * Populates the card menu with the fetched Menu objects.
     * 
     * @param menus The list of Menu objects to populate.
     * @throws IOException If an I/O error occurs during population.
     */
    private void populateCardUser(List<User> users) throws IOException {
        List<List<User>> groupedUsers = sortAndGroupUsers(users);
        for (List<User> group : groupedUsers) {
            for (User user : group) {
            	CardUser cardUser = new CardUser(user);
                cardUser.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showPopup(e);
                    }

                    private void showPopup(MouseEvent e) {
                        if (e.getComponent() instanceof CardUser) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                cardUser.setSelected(!cardUser.isSelected());
                                if (cardUser.isSelected()) {
                                    cardUser.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                                } else {
                                    cardUser.setBorder(BorderFactory.createEmptyBorder());
                                }
                            } else if (e.isPopupTrigger()) {
                                cardUser.setSelected(true);
                                cardUser.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                                formUserUI.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                });
                SwingUtilities.invokeLater(() -> formUserUI.getPanelCard().add(cardUser));
            }
        }
        SwingUtilities.invokeLater(() -> {
            formUserUI.getPanelCard().revalidate();
            formUserUI.getPanelCard().repaint();
        });
    }
    
    /** 
     * Sorts and groups the users based on their status.
     * 
     * @param user The list of Menu objects to sort and group.
     * @return A list containing two lists: available users and other users.
     */
    private List<List<User>> sortAndGroupUsers(List<User> users) {
        List<User> availableUsers = new ArrayList<>();
        List<User> otherUsers = new ArrayList<>();

        for (User user : users) {
            if ("AVAILABLE".equalsIgnoreCase(user.getStatus().getDisplayName())) {
                availableUsers.add(user);
            } else {
                otherUsers.add(user);
            }
        }

        availableUsers.sort(Comparator.comparing(User::getFirstName));
        otherUsers.sort(Comparator.comparing(User::getFirstName));

        List<List<User>> groupedUsers = new ArrayList<>();
        groupedUsers.add(availableUsers);
        groupedUsers.add(otherUsers);
        return groupedUsers;
    }
    
    /** 
     * Populates the basic table with the fetched User objects.
     * 
     * @param users The list of User objects to populate.
     */
    private void populateBasicMenu(List<User> users) {
        List<List<User>> groupedUsers = sortAndGroupUsers(users);
        SwingUtilities.invokeLater(() -> formUserUI.getTableModel().setRowCount(0));
        groupedUsers.forEach(group -> {
            group.forEach(user -> {
                SwingUtilities.invokeLater(() -> {
                    formUserUI.getTableModel().addRow(userController.toTableRow(user));
                });
            });
        });
    }


	/**
	 * Fetches the list of users based on the search term.
	 * 
	 * @param searchTerm The term to search for in the user.
	 * @return The list of User objects.
	 * @throws IOException If an I/O error occurs.
	 */
	private List<User> fetchUsers(String searchTerm) throws IOException {
		if (searchTerm != null && !searchTerm.isEmpty()) {
			return userController.searchUsers(searchTerm);
		} else {
			return userController.getAllUsers();
		}
	}

	/**
	 * Handles the search text change with debounce functionality.
	 * 
	 * @param txtSearch The JTextField containing the search text.
	 */
	public void handleSearchTextChange(JTextField txtSearch) {
		if (debounceTimer != null && debounceTimer.isRunning()) {
			debounceTimer.stop();
		}
		debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData(txtSearch.getText()));
		debounceTimer.setRepeats(false);
		debounceTimer.start();
	}

	/**
	 * Displays a modal dialog for creating, editing, or deleting a user.
	 * 
	 * @param userAction The action to perform (create, edit, delete).
	 */
	public void showModal(String userAction) {
		switch (userAction) {
		case "details":
			showDetailsModal();
			break;
		case "create":
			showCreateModal();
			break;
		case "edit":
			showEditModal();
			break;
		case "delete":
			showDeleteModal();
			break;
		default:
			break;
		}
	}

	/**
	 * Displays a modal dialog for showing details of a selected user.
	 */
	private void showDetailsModal() {
		String userId = getSelectedUserId();
		if (userId == null)
			return;
		UserInfoForm infoFormUser = createInfoFormUser(userId);
		ModalDialog.showModal(formUserUI, new AdaptSimpleModalBorder(infoFormUser, "User details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	/**
	 * Displays a modal dialog for creating a new user.
	 */
	private void showCreateModal() {
		CreateUserInputForm inputFormCreateUser = new CreateUserInputForm();
		ModalDialog.showModal(formUserUI, new AdaptSimpleModalBorder(inputFormCreateUser, "Create user",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleCreateUser(inputFormCreateUser);
					}
				}), DefaultComponent.getInputForm());
	}

	/**
	 * Handles the creation of a new user.
	 * 
	 * @param inputFormCreateUser The input form containing the user data.
	 */
	private void handleCreateUser(CreateUserInputForm inputFormCreateUser) {
		Object[] userData;
		userData = inputFormCreateUser.getData();
		System.out.println(userData);
		try {
			userController.createUser(userData);
			Toast.show(formUserUI, Toast.Type.SUCCESS, "User  created successfully");
			loadData("");
		} catch (IOException e) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to create user: " + e.getMessage());
		}
	}

	/**
	 * Displays a modal dialog for editing an existing user.
	 */
	private void showEditModal() {
		String userId = getSelectedUserId();
		if (userId == null)
			return;

		User user = null;
		try {
			user = userController.getUserById(userId);
		} catch (IOException e) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to find user to edit: " + e.getMessage());
		}
		UpdateUserInputForm updateUserInputForm = createInputFormUpdateUser(user);
		ModalDialog.showModal(formUserUI, new AdaptSimpleModalBorder(updateUserInputForm, "Update user",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleUpdateUser(updateUserInputForm);
					}
				}), DefaultComponent.getInputForm());
	}

	/**
	 * Handles the update of an existing user.
	 * 
	 * @param inputFormUpdateUser The input form containing the updated user
	 *                            information.
	 */
	private void handleUpdateUser(UpdateUserInputForm inputFormUpdateUser) {
		Object[] userData = inputFormUpdateUser.getData();
		try {
			userController.updateUser(userData);
			Toast.show(formUserUI, Toast.Type.SUCCESS, "User  updated successfully");
			loadData("");
		} catch (IOException | BusinessException e) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to update user: " + e.getMessage());
		}
	}

	/**
	 * Creates an input form for updating a user.
	 * 
	 * @param user The User object to update.
	 * @return The created UpdateUser InputForm, or null if an error occurs.
	 */
	private UpdateUserInputForm createInputFormUpdateUser(User user) {
		try {
			return new UpdateUserInputForm(user);
		} catch (Exception e) { // Catching a general Exception since we don't know the specific exceptions
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to find user to edit: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Displays a modal dialog for deleting selected users.
	 */
	private void showDeleteModal() {
		List<String> selectedUserIds = getSelectedUserIds();
		if (selectedUserIds.isEmpty()) {
			Toast.show(formUserUI, Toast.Type.ERROR, "You must select at least one user to delete");
			return;
		}
		confirmDeletion(selectedUserIds);
	}

	/**
	 * Gets the IDs of the selected users for deletion.
	 * 
	 * @return The list of selected user IDs.
	 */
	private List<String> getSelectedUserIds() {
		List<String> selectedUserIds = new ArrayList<>();
		int rowCount = formUserUI.getTable().getRowCount();
		for (int i = 0; i < rowCount; i++) {
			if ((Boolean) formUserUI.getTable().getValueAt(i, 0)) { // Assuming the first column is a checkbox
				selectedUserIds.add(formUserUI.getTable().getValueAt(i, 1).toString()); // Assuming the second column is
																						// the user ID
			}
		}
		return selectedUserIds;
	}

	/**
	 * Confirms the deletion of the selected users.
	 * 
	 * @param selectedUser Ids The list of user IDs to delete.
	 */
	private void confirmDeletion(List<String> selectedUserIds) {
		String message = selectedUserIds.size() == 1
				? "Are you sure you want to delete this user: " + selectedUserIds.get(0) + "?"
				: "Are you sure you want to delete these users: " + selectedUserIds + "?";
		ModalDialog.showModal(formUserUI, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, message,
				"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
					if (action == SimpleMessageModal.YES_OPTION) {
						if (selectedUserIds.size() == 1) {
							deleteUser(selectedUserIds.get(0));
						}
						else {
							deleteUsers(selectedUserIds);
						}
							
					}
				}), DefaultComponent.getChoiceModal());
	}

	/**
	 * Deletes the selected users by their IDs.
	 * 
	 * @param selectedUser Ids The list of user IDs to delete.
	 */
	private void deleteUsers(List<String> selectedUserIds) {
		try {
			userController.deleteUsers(selectedUserIds);
			loadData(""); // Reload user data after successful deletion
			Toast.show(formUserUI, Toast.Type.SUCCESS, "Users deleted successfully");
		} catch (IOException e) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to delete users: " + e.getMessage());
		}
	}

	private void deleteUser(String id) {
		try {
			userController.deleteUser(id);
			loadData("");
			Toast.show(formUserUI, Toast.Type.SUCCESS, "User deleted successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to delete user: " + e.getMessage());
		}
	}

	/**
	 * Gets the ID of the selected user.
	 * 
	 * @return The ID of the selected user, or null if no user is selected.
	 */
	private String getSelectedUserId() {
		int selectedRow = formUserUI.getTable().getSelectedRow();
		if (selectedRow == -1) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Please select a user to view details");
			return null;
		}
		return formUserUI.getTable().getValueAt(selectedRow, 1).toString(); // Assuming the second column is the user ID
	}

	/**
	 * Creates an InfoFormUser for displaying user details.
	 * 
	 * @param userId The ID of the user to display.
	 * @return The created InfoFormUser , or null if an error occurs.
	 */
	private UserInfoForm createInfoFormUser(String userId) {
		try {
			return new UserInfoForm(userId);
		} catch (IOException e) {
			Toast.show(formUserUI, Toast.Type.ERROR, "Failed to find user to view details: " + e.getMessage());
			return null;
		}
	}
}