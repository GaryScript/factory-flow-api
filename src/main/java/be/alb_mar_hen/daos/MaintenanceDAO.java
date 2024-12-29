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
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.MachineType;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;
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
		        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MAINTENANCES.MAINTENANCES_COLLECTION");

		        // Exécuter la fonction
		        stmt.execute();

		        
		        ResultSet rsMaintenances = stmt.getArray(1).getResultSet();
		        
		        while(rsMaintenances.next()) {
		        	Struct maintenanceData = (Struct) rsMaintenances.getObject(2);
		        	Object[] attributes = maintenanceData.getAttributes();
		        	
		        	
		        	//Maintenance
		        	int maintenanceId = ((BigDecimal) attributes[0]).intValue();
	                LocalDate maintenanceStartDate = ((Timestamp) attributes[1]).toLocalDateTime().toLocalDate();
	                LocalDate maintenanceEndDate = ((Timestamp) attributes[2]).toLocalDateTime().toLocalDate();
	                int duration = ((BigDecimal) attributes[3]).intValue();
	                String report = (String) attributes[4];
	                int status = ((BigDecimal) attributes[5]).intValue();
	                
	                //Site
	                ResultSet rsSite = ((Array)attributes[8]).getResultSet();
	                
	                int site_id = 0;
	                String city = null;
	                
					while (rsSite.next()) {
						Struct siteData = (Struct) rsSite.getObject(2);
						Object[] siteAttributes = siteData.getAttributes();

						site_id = ((BigDecimal) siteAttributes[0]).intValue();
						city = (String) siteAttributes[1];

						System.out.println("Site ID: " + site_id);
						System.out.println("City: " + city);
					}
					
					//Zones
					Set<Zone> zones = new HashSet<Zone>();
					ResultSet rsZones = ((Array)attributes[7]).getResultSet();
					
					while (rsZones.next()) {
						Struct zoneData = (Struct) rsZones.getObject(2);
						Object[] zoneAttributes = zoneData.getAttributes();

						int zoneId = ((BigDecimal) zoneAttributes[0]).intValue();
						String zoneName = (String) zoneAttributes[1];
						int zoneColor = ((BigDecimal) zoneAttributes[2]).intValue();

						System.out.println("Zone ID: " + zoneId);
						System.out.println("Zone Name: " + zoneName);
						System.out.println("Zone Color: " + zoneColor);
						
						Zone currZone = new Zone(Optional.of(zoneId), ZoneColor.fromDatabaseValue(zoneColor), zoneName,Optional.of(site_id), city, new NumericValidator(), new ObjectValidator(), new StringValidator());
					
						zones.add(currZone);
					}
					
					//MachineType
					MachineType machineTypeObj = null;
					ResultSet rsMachineType = ((Array)attributes[9]).getResultSet();
					
					while(rsMachineType.next()) {
						Struct machineTypeData = (Struct) rsMachineType.getObject(2);
                        Object[] machineTypeAttributes = machineTypeData.getAttributes();
                        
                        int machineTypeId = ((BigDecimal) machineTypeAttributes[0]).intValue();
                        String machineTypeName = (String) machineTypeAttributes[1];
                        double machineTypePrice = ((BigDecimal) machineTypeAttributes[2]).doubleValue();
                        int machineTypeDaysBeforeMaintenance = ((BigDecimal) machineTypeAttributes[3]).intValue();
                        
                        System.out.println("Machine Type ID: " + machineTypeId);
                        System.out.println("Machine Type Name: " + machineTypeName);
                        System.out.println("Machine Type Price: " + machineTypePrice);
                        System.out.println("Machine Type Days Before Maintenance: " + machineTypeDaysBeforeMaintenance);
                        
                        machineTypeObj = new MachineType(Optional.of(machineTypeId), machineTypeName, machineTypePrice, machineTypeDaysBeforeMaintenance, new NumericValidator(), new StringValidator(), new ObjectValidator());
					}
					
					
	                //Machine
		        	ResultSet rsMachines = ((Array)attributes[6]).getResultSet();
		        	Machine machineObj = null;
		        	System.out.println("Maintenance id : " + maintenanceId);
		        	
		        	int machineId; 
                    int machineStatus;
                    String machineName;
		        	
		        	while(rsMachines.next()) {
		        		Struct machineData = (Struct) rsMachines.getObject(2);
	                    Object[] machineAttributes = machineData.getAttributes();
	                    
		        		System.out.println("Machines: ");
		        		machineId = ((BigDecimal) machineAttributes[0]).intValue();
	                    machineStatus = ((BigDecimal) machineAttributes[1]).intValue();
	                    machineName = (String) machineAttributes[2];

	                    System.out.println("Machine ID: " + machineId);
	                    System.out.println("Machine Name: " + machineName);
	                    System.out.println("Machine Type: " + machineStatus);
	                    
	                    Iterator<Zone> zonesIterator = zones.iterator();
	                    
	                    Zone zone = null;
	                    if(zonesIterator.hasNext()) {
	                    	zone = zonesIterator.next();
	                    }
	                    
	                    machineObj = new Machine(Optional.of(machineId), machineTypeObj.getType(), MachineStatus.fromDatabaseValue(machineStatus), machineName, zone, machineTypeObj.getId(), machineTypeObj.getType(), machineTypeObj.getPrice(), machineTypeObj.getDaysBeforeMaintenance(),new NumericValidator(), new ObjectValidator(), new StringValidator());
		        	}
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