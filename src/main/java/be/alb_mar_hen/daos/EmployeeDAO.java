package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Employee;

public class EmployeeDAO implements DAO<Employee>{
	private Connection connection = null;
	
	public EmployeeDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<Employee> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Employee find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Employee object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Employee object) {
		// TODO Auto-generated method stub
		return false;
	}
}
