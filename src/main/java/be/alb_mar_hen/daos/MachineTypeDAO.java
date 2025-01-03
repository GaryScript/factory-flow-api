package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.MachineType;

public class MachineTypeDAO implements DAO<MachineType>{
	public Connection connection = null;
	
	public MachineTypeDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<MachineType> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MachineType find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(MachineType object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MachineType object) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
