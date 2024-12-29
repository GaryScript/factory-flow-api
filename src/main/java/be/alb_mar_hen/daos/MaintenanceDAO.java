package be.alb_mar_hen.daos;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import be.alb_mar_hen.javabeans.Maintenance;
import oracle.jdbc.internal.OracleTypes;
import oracle.jdbc.oracore.OracleType;

public class MaintenanceDAO implements DAO<Maintenance>{
	private Connection connection = null;
	
	public MaintenanceDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Maintenance> findall() {
		 List<Maintenance> maintenancesList = new ArrayList<>();

		    try {
		        // Appel de la fonction PL/SQL qui retourne une collection
		        CallableStatement stmt = connection.prepareCall("{ ? = call Pkg_maintenances.get_maintenances() }");

		        // Enregistrer le type de retour comme TABLE et spécifier le nom complet du type
		        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MAINTENANCES.MAINTENANCES_TABLE");

		        // Exécuter la fonction
		        stmt.execute();

		        
		        ResultSet rs = stmt.getArray(1).getResultSet();
		        
		        while(rs.next()) {
		        	Struct struct = (Struct) rs.getObject(2);
		        	Object[] attributes = struct.getAttributes();
		        	
		        	int maintenanceId = ((BigDecimal) attributes[0]).intValue(); 
		        	LocalDate startDate = ((Timestamp) attributes[1]).toLocalDateTime().toLocalDate();
		        	LocalDate endDate = ((Timestamp) attributes[2]).toLocalDateTime().toLocalDate();
		        	int duration = ((BigDecimal) attributes[3]).intValue();
		        	String report = (String) attributes[4];
		        	int status = ((BigDecimal) attributes[5]).intValue();
		        	int machineId = ((BigDecimal) attributes[5]).intValue();
		        	int maintenanceResponsableId = ((BigDecimal) attributes[6]).intValue();
		        	
		        	System.out.println("start date : " + startDate);
		        }
		        
		        stmt.close();
		    } catch (SQLException e) {
		        e.printStackTrace();  // Gérer les erreurs SQL
		    }

	    return maintenancesList;
	}

	@Override
	public Maintenance find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Maintenance object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Maintenance object) {
		// TODO Auto-generated method stub
		return false;
	}
}