package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.PurchasingAgent;

public class PurchasingAgentDAO implements DAO<PurchasingAgent>{
	private Connection connection = null;
	
	public PurchasingAgentDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<PurchasingAgent> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PurchasingAgent find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(PurchasingAgent object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(PurchasingAgent object) {
		// TODO Auto-generated method stub
		return false;
	}
}
