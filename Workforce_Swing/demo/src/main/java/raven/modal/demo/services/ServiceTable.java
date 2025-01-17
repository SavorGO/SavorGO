package raven.modal.demo.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import raven.modal.demo.models.ModelTable;

public interface ServiceTable {
	public List<ModelTable> getAllTables() throws IOException;
	public ModelTable getTableById(Long id) throws IOException;
	public List<ModelTable> searchTables(String search) throws IOException;
	public void createTable(String name) throws IOException;
	public void updateTable(ModelTable table) throws IOException;
	public void removeTable(long id) throws IOException;
	public void removeTables(List<Long> ids) throws IOException;
}
