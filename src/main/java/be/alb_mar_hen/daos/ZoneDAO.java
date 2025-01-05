package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Zone;

public class ZoneDAO implements DAO<Zone>{
	private Connection connection = null;
	
	public ZoneDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<Zone> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Zone find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Zone object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Zone object) {
		// TODO Auto-generated method stub
		return false;
	}
}
