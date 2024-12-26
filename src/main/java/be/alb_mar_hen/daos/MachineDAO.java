package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Machine;

public class MachineDAO implements DAO<Machine>{
	private Connection connection = null;
	
	public MachineDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Machine> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Machine find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Machine object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Machine object) {
		// TODO Auto-generated method stub
		return false;
	}
}
