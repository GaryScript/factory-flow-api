package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.MaintenanceWorker;

public class MaintenanceWorkerDAO implements DAO<MaintenanceWorker>{
	private Connection connection = null;
	
	public MaintenanceWorkerDAO(Connection connection) {
		if (connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<MaintenanceWorker> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MaintenanceWorker find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(MaintenanceWorker object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MaintenanceWorker object) {
		// TODO Auto-generated method stub
		return false;
	}
}
