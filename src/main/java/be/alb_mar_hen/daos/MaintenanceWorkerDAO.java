package be.alb_mar_hen.daos;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Struct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.MaintenanceStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.MachineType;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.utils.Conversion;
import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

public class MaintenanceWorkerDAO implements DAO<MaintenanceWorker>{
	private Connection connection = null;
	
	public MaintenanceWorkerDAO(Connection connection) {
		if (connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<MaintenanceWorker> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MaintenanceWorker find(int id) {
		MaintenanceWorker currWorker = null;
		
		try {
			CallableStatement stmt = connection.prepareCall("{? = call Pkg_maintenance_workers.get_worker(?)}");
			
			stmt.registerOutParameter(1, java.sql.Types.STRUCT, "PKG_MAINTENANCE_WORKERS.WORKER_WITH_MAINTENANCES_RECORD");
			stmt.setInt(2, id);
			
			stmt.execute();
			
			Struct workerStruct = (Struct) stmt.getObject(1);
			Object[] currWorkerAttributes = workerStruct.getAttributes();
			
			Struct employeeStruct = (Struct) currWorkerAttributes[0];
			Object[] employeeAttributes = employeeStruct.getAttributes();
			
			int currWorkerId = ((BigDecimal) employeeAttributes[0]).intValue();
			String matricule = (String) employeeAttributes[1];
			String password = (String) employeeAttributes[2];
			String firstName = (String) employeeAttributes[3];
			String lastName = (String) employeeAttributes[4];
			
			currWorker = new MaintenanceWorker(Optional.of(currWorkerId), matricule, password, firstName, lastName, new StringValidator(), new NumericValidator(), new StringFormatter(), new ObjectValidator());
			
			Array maintenancesArray = (Array) currWorkerAttributes[1];
			Object[] maintenancesAttributes = (Object[]) maintenancesArray.getArray();
			ResultSet rsMaintenances = maintenancesArray.getResultSet();
			
			List<Maintenance> maintenancesList = new ArrayList<Maintenance>();
			
			while(rsMaintenances.next()) {
	        	Struct maintenanceData = (Struct) rsMaintenances.getObject(2);
	        	Object[] attributes = maintenanceData.getAttributes();
	            
                //Site
                ResultSet rsSite = ((Array)attributes[8]).getResultSet();
                
                int site_id = 0;
                String city = null;
                
				while (rsSite.next()) {
					Struct siteData = (Struct) rsSite.getObject(2);
					Object[] siteAttributes = siteData.getAttributes();

					site_id = ((BigDecimal) siteAttributes[0]).intValue();
					city = (String) siteAttributes[1];
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
                    
                    machineTypeObj = new MachineType(Optional.of(machineTypeId), machineTypeName, machineTypePrice, machineTypeDaysBeforeMaintenance, new NumericValidator(), new StringValidator(), new ObjectValidator());
				}
				
				
                //Machine
	        	ResultSet rsMachines = ((Array)attributes[6]).getResultSet();
	        	Machine machineObj = null;
	        	
	        	int machineId; 
                int machineStatus;
                String machineName;
	        	
	        	while(rsMachines.next()) {
	        		Struct machineData = (Struct) rsMachines.getObject(2);
                    Object[] machineAttributes = machineData.getAttributes();
                    
	        		machineId = ((BigDecimal) machineAttributes[0]).intValue();
                    machineStatus = ((BigDecimal) machineAttributes[1]).intValue();
                    machineName = (String) machineAttributes[2];;
                    
                    machineObj = new Machine(Optional.of(machineId), MachineStatus.fromDatabaseValue(machineStatus), machineName, zones.stream().findFirst().get(), machineTypeObj.getId(), machineTypeObj.getType(), machineTypeObj.getPrice(), machineTypeObj.getDaysBeforeMaintenance(),new NumericValidator(), new ObjectValidator(), new StringValidator());          
	        	}
	        	
	        	//Responsable
                ResultSet rsResponsable = ((Array)attributes[11]).getResultSet();
                MaintenanceResponsable responsable = null;
                
                while(rsResponsable.next()) {
                	Struct responsableData = (Struct) rsResponsable.getObject(2);
                	Object[] responsableAttributes = responsableData.getAttributes();
                	
                	int responsableId = ((BigDecimal) responsableAttributes[0]).intValue();
                	String responsableMatricule = (String) responsableAttributes[1];
                	String responsablePassword = (String) responsableAttributes[2];
                	String responsableFirstName = (String) responsableAttributes[3];
                	String responsableLastName = (String) responsableAttributes[4];
                	
                	responsable = new MaintenanceResponsable(Optional.of(responsableId), responsableMatricule, responsablePassword, responsableFirstName, responsableLastName, new ObjectValidator(), new StringValidator(), new NumericValidator(), new StringFormatter());
                }
                
                //Workers
                Set<MaintenanceWorker> workers = new HashSet<MaintenanceWorker>();
                ResultSet rsWorkers = ((Array)attributes[10]).getResultSet();
                
				while (rsWorkers.next()) {
					Struct workerData = (Struct) rsWorkers.getObject(2);
					Object[] workerAttributes = workerData.getAttributes();

					int workerId = ((BigDecimal) workerAttributes[0]).intValue();
					String workerMatricule = (String) workerAttributes[1];
					String workerPassword = (String) workerAttributes[2];
					String workerFirstName = (String) workerAttributes[3];
					String workerLastName = (String) workerAttributes[4];

					MaintenanceWorker worker = new MaintenanceWorker(Optional.of(workerId), workerMatricule,
							workerPassword, workerFirstName, workerLastName, new StringValidator(),
							new NumericValidator(), new StringFormatter(), new ObjectValidator());
					
					workers.add(worker);
				}
				
				//Maintenance
	        	int maintenanceId = ((BigDecimal) attributes[0]).intValue();
                LocalDateTime maintenanceStartDate = ((Timestamp) attributes[1]).toLocalDateTime();
                
                Optional<LocalDateTime> maintenanceEndDate = Optional.ofNullable(Conversion.extractLocalDateTime(attributes[2]));
				
				Optional<Integer> duration = Optional.ofNullable((BigDecimal) attributes[3]).map(BigDecimal::intValue);

				Optional<String> report = Optional.ofNullable((String) attributes[4]);
                
                int status = ((BigDecimal) attributes[5]).intValue();
                
                Maintenance maintenance = new Maintenance(Optional.of(maintenanceId), maintenanceStartDate, 
                		maintenanceEndDate, duration, report, 
                		MaintenanceStatus.fromDatabaseValue(status), 
                		machineObj, workers.stream().findFirst().get(), responsable, new NumericValidator(), new StringValidator(), new ObjectValidator(), new DateValidator());
                
				for (MaintenanceWorker worker : workers) {
					if (worker != null && maintenance.getMaintenanceWorkers().stream().noneMatch(w -> w.getId().get() == worker.getId().get())) {
						maintenance.addMaintenanceWorker(worker);
					}
				}
				
	        	maintenancesList.add(maintenance);
	        }
			
			for (Maintenance maintenance : maintenancesList) {
				currWorker.addMaintenance(maintenance);
			}
			
	        stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}	
		
		return currWorker;
	}

	@Override
	public int create(MaintenanceWorker object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(MaintenanceWorker object) {
		// TODO Auto-generated method stub
		return false;
	}
}
