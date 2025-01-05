package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.MaintenanceResponsable;

public class MaintenanceResponsableDAO implements DAO<MaintenanceResponsable>{
	private Connection connection = null;
	
	public MaintenanceResponsableDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<MaintenanceResponsable> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MaintenanceResponsable find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(MaintenanceResponsable object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MaintenanceResponsable object) {
		// TODO Auto-generated method stub
		return false;
	}
}
