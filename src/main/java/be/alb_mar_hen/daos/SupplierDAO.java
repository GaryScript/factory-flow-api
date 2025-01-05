package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Supplier;

public class SupplierDAO implements DAO<Supplier>{
	private Connection connection = null;
	
	public SupplierDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Supplier> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Supplier find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Supplier object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Supplier object) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
