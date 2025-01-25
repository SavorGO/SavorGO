package raven.modal.demo.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import raven.modal.demo.models.EnumTableStatus;
import raven.modal.demo.models.ModelTable;
import raven.modal.demo.services.ServiceTable;
import raven.modal.demo.services.impls.ServiceImplTable;
import raven.modal.demo.utils.BusinessException;

public class ControllerTable {
	private ServiceTable serviceTable = new ServiceImplTable();

	public List<ModelTable> getAllTables() throws IOException {
		return serviceTable.getAllTables();
	}

	public ModelTable getTableById(Long id) throws IOException {
		return serviceTable.getTableById(id);
	}

	public void createTable(Object[] tableData) throws IOException {
		ModelTable table = ModelTable.builder().name(tableData[0].toString()).build();
		serviceTable.createTable(table);
	}

	public void updateTable(Object[] tableData) throws IOException {
		ModelTable table = serviceTable.getTableById((long) tableData[0]);
		table.setName(tableData[1].toString());
		table.setStatus(EnumTableStatus.fromDisplayName(tableData[2].toString()));
		table.setReservedTime((LocalDateTime) tableData[3]);
		serviceTable.updateTable(table);
	}

	public void deleteTable(long id) throws IOException {
		serviceTable.removeTable(id);
	}

	public void deleteTables(List<Long> ids) throws IOException {
		serviceTable.removeTables(ids);
	}

	public List<ModelTable> searchTables(String search) throws IOException {
		return serviceTable.searchTables(search);
	}
}
