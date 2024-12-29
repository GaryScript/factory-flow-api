package be.alb_mar_hen.daos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

public class EmployeeDAO implements DAO<Employee>{
	private Connection connection = null;
	
	public EmployeeDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	public Employee authenticate(String matricule, String password) throws SQLException {
        CallableStatement stmt = null;

        try {
            String call = "{CALL sp_check_user_authentication(?, ?, ?)}";
            stmt = connection.prepareCall(call);

            stmt.setString(1, matricule);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.STRUCT, "USER_AUTH_OBJ");

            stmt.execute();

            java.sql.Struct employeeStruct = (java.sql.Struct) stmt.getObject(3);
            Object[] attributes = employeeStruct.getAttributes();
            
            StringValidator stringValidator = new StringValidator();
            NumericValidator numericValidator = new NumericValidator();
            ObjectValidator objectValidator = new ObjectValidator();
            StringFormatter stringFormatter = new StringFormatter();
            
            Employee concreteEmployee = Employee.createEmployee(
            	    (String) attributes[4], // role
            	    Optional.ofNullable(((Number) attributes[0]).intValue()), // employeeId
            	    (String) attributes[1], // matricule
            	    null, // password (non inclus dans attributes)
            	    (String) attributes[2], // firstName
            	    (String) attributes[3], // lastName
            	    stringValidator,
            	    numericValidator,
            	    objectValidator,
            	    stringFormatter
            	);
            
            return concreteEmployee;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new SQLException("Invalid matricule or password.", e);
            } else if (e.getErrorCode() == 20002) {
                throw new SQLException("Role not found for the employee.", e);
            } else {
                throw new SQLException("An unexpected error occurred: " + e.getMessage(), e);
            }
        } finally {
            if (stmt != null) stmt.close();
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
