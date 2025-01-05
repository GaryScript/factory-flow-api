package be.alb_mar_hen.daos;

import java.sql.Connection;
import java.util.List;

import be.alb_mar_hen.javabeans.Site;

public class SiteDAO implements DAO<Site>{
	private Connection connection = null;
	
	public SiteDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<Site> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Site find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Site object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Site object) {
		// TODO Auto-generated method stub
		return false;
	}
}
