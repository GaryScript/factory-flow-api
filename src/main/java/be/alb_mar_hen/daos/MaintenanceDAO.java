package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Maintenance;

public class MaintenanceDAO implements DAO<Maintenance>{
	private Connection connection = null;
	
	public MaintenanceDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Maintenance> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Maintenance find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Maintenance object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Maintenance object) {
		// TODO Auto-generated method stub
		return false;
	}
}