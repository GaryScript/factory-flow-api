package be.alb_mar_hen.daos;

import java.util.List;

public interface DAO<T> {
	public List<T> findAll() throws Exception;
	public T find(int id);
	public int create(T object);
	public boolean delete(int id);
	public boolean update(T object);
}
