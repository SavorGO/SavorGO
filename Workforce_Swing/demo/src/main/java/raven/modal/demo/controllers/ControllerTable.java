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
	public void createTable(String name) throws IOException {
		serviceTable.createTable(name);
	}
	public void updateTable(Long id, String name, EnumTableStatus status, LocalDateTime reservedTime) throws IOException, BusinessException {
		ModelTable table;
		table = serviceTable.getTableById(id);
		table.setName(name);
		table.setStatus(status);
		if(reservedTime == null) {
			
		} else {
			if(!reservedTime.equals(table.getReservedTime()) && reservedTime.isAfter(LocalDateTime.now().plusDays(7))) throw new BusinessException("Cannot reserve after 7 days");
			if(!reservedTime.equals(table.getReservedTime()) && reservedTime.isBefore(LocalDateTime.now())) throw new BusinessException("Cannot change reserve time before now");
			reservedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			table.setReservedTime(reservedTime);
		}
		serviceTable.updateTable(table);
	}
	public void removeTable(long id) throws IOException {
		serviceTable.removeTable(id);
	}
	public void removeTables(List<Long> ids) throws IOException{
		serviceTable.removeTables(ids);
	}
	public List<ModelTable> searchTables(String search) throws IOException {
		return serviceTable.searchTables(search);
	}
}
