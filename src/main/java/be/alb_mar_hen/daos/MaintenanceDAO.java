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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.sql.Date;
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
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import oracle.jdbc.oracore.OracleType;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class MaintenanceDAO implements DAO<Maintenance>{
	private Connection connection = null;
	
	public MaintenanceDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Maintenance> findAll() {
		 List<Maintenance> maintenancesList = new ArrayList<>();

		    try {
		        CallableStatement stmt = connection.prepareCall("{ ? = call Pkg_maintenances.get_maintenances() }");

		        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MAINTENANCES.MAINTENANCES_COLLECTION");

		        stmt.execute();
		        
		        ResultSet rsMaintenances = stmt.getArray(1).getResultSet();
		        
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
		        stmt.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

	    return maintenancesList;
	}
	
	public List<Maintenance> findall(int wId) {
		 List<Maintenance> maintenancesList = new ArrayList<>();

		    try {
		        CallableStatement stmt = connection.prepareCall("{ ? = call Pkg_maintenances.get_maintenance(?) }");

		        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MAINTENANCES.MAINTENANCES_COLLECTION");
		        stmt.setInt(2, wId);
		        
		        stmt.execute();
		        
		        ResultSet rsMaintenances = stmt.getArray(1).getResultSet();
		        
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
		        stmt.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

	    return maintenancesList;
	}


	@Override
	public Maintenance find(int id) {
	    Maintenance maintenance = null;

	    try {
	        CallableStatement stmt = connection.prepareCall("{ ? = call Pkg_maintenances.get_maintenance(?) }");

	        stmt.registerOutParameter(1, OracleTypes.STRUCT, "PKG_MAINTENANCES.MAINTENANCE_RECORD");
	        stmt.setInt(2, id);

	        stmt.execute();

	        Struct maintenanceData = (Struct) stmt.getObject(1);
	        if (maintenanceData != null) {
	            Object[] attributes = maintenanceData.getAttributes();

	            // Site
	            ResultSet rsSite = ((Array) attributes[8]).getResultSet();
	            int site_id = 0;
	            String city = null;

	            while (rsSite.next()) {
	                Struct siteData = (Struct) rsSite.getObject(2);
	                Object[] siteAttributes = siteData.getAttributes();

	                site_id = ((BigDecimal) siteAttributes[0]).intValue();
	                city = (String) siteAttributes[1];
	            }

	            // Zones
	            Set<Zone> zones = new HashSet<>();
	            ResultSet rsZones = ((Array) attributes[7]).getResultSet();

	            while (rsZones.next()) {
	                Struct zoneData = (Struct) rsZones.getObject(2);
	                Object[] zoneAttributes = zoneData.getAttributes();

	                int zoneId = ((BigDecimal) zoneAttributes[0]).intValue();
	                String zoneName = (String) zoneAttributes[1];
	                int zoneColor = ((BigDecimal) zoneAttributes[2]).intValue();

	                Zone currZone = new Zone(Optional.of(zoneId), ZoneColor.fromDatabaseValue(zoneColor), zoneName,
	                        Optional.of(site_id), city, new NumericValidator(), new ObjectValidator(),
	                        new StringValidator());

	                zones.add(currZone);
	            }

	            // MachineType
	            MachineType machineTypeObj = null;
	            ResultSet rsMachineType = ((Array) attributes[9]).getResultSet();

	            while (rsMachineType.next()) {
	                Struct machineTypeData = (Struct) rsMachineType.getObject(2);
	                Object[] machineTypeAttributes = machineTypeData.getAttributes();

	                int machineTypeId = ((BigDecimal) machineTypeAttributes[0]).intValue();
	                String machineTypeName = (String) machineTypeAttributes[1];
	                double machineTypePrice = ((BigDecimal) machineTypeAttributes[2]).doubleValue();
	                int machineTypeDaysBeforeMaintenance = ((BigDecimal) machineTypeAttributes[3]).intValue();

	                machineTypeObj = new MachineType(Optional.of(machineTypeId), machineTypeName, machineTypePrice,
	                        machineTypeDaysBeforeMaintenance, new NumericValidator(), new StringValidator(),
	                        new ObjectValidator());
	            }

	            // Machine
	            ResultSet rsMachines = ((Array) attributes[6]).getResultSet();
	            Machine machineObj = null;

	            while (rsMachines.next()) {
	                Struct machineData = (Struct) rsMachines.getObject(2);
	                Object[] machineAttributes = machineData.getAttributes();

	                int machineId = ((BigDecimal) machineAttributes[0]).intValue();
	                int machineStatus = ((BigDecimal) machineAttributes[1]).intValue();
	                String machineName = (String) machineAttributes[2];

	                machineObj = new Machine(Optional.of(machineId), MachineStatus.fromDatabaseValue(machineStatus),
	                        machineName, zones.stream().findFirst().orElse(null), machineTypeObj.getId(),
	                        machineTypeObj.getType(), machineTypeObj.getPrice(),
	                        machineTypeObj.getDaysBeforeMaintenance(), new NumericValidator(), new ObjectValidator(),
	                        new StringValidator());
	            }

	            // Responsable
	            ResultSet rsResponsable = ((Array) attributes[11]).getResultSet();
	            MaintenanceResponsable responsable = null;

	            while (rsResponsable.next()) {
	                Struct responsableData = (Struct) rsResponsable.getObject(2);
	                Object[] responsableAttributes = responsableData.getAttributes();

	                int responsableId = ((BigDecimal) responsableAttributes[0]).intValue();
	                String responsableMatricule = (String) responsableAttributes[1];
	                String responsablePassword = (String) responsableAttributes[2];
	                String responsableFirstName = (String) responsableAttributes[3];
	                String responsableLastName = (String) responsableAttributes[4];

	                responsable = new MaintenanceResponsable(Optional.of(responsableId), responsableMatricule,
	                        responsablePassword, responsableFirstName, responsableLastName, new ObjectValidator(),
	                        new StringValidator(), new NumericValidator(), new StringFormatter());
	            }

	            // Workers
	            Set<MaintenanceWorker> workers = new HashSet<>();
	            ResultSet rsWorkers = ((Array) attributes[10]).getResultSet();

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

	            // Maintenance
	            int maintenanceId = ((BigDecimal) attributes[0]).intValue();
	            LocalDateTime maintenanceStartDate = ((Timestamp) attributes[1]).toLocalDateTime();
	            Optional<LocalDateTime> maintenanceEndDate = Optional
	                    .ofNullable(Conversion.extractLocalDateTime(attributes[2]));

	            Optional<Integer> duration = Optional.ofNullable((BigDecimal) attributes[3]).map(BigDecimal::intValue);

	            Optional<String> report = Optional.ofNullable((String) attributes[4]);

	            int status = ((BigDecimal) attributes[5]).intValue();

	            maintenance = new Maintenance(Optional.of(maintenanceId), maintenanceStartDate, maintenanceEndDate,
	                    duration, report, MaintenanceStatus.fromDatabaseValue(status), machineObj,
	                    workers.stream().findFirst().orElse(null), responsable, new NumericValidator(), new StringValidator(),
	                    new ObjectValidator(), new DateValidator());

	            for (MaintenanceWorker worker : workers) {
	                if (worker != null && maintenance.getMaintenanceWorkers().stream()
	                        .noneMatch(w -> w.getId().get() == worker.getId().get())) {
	                    maintenance.addMaintenanceWorker(worker);
	                }
	            }
	        }

	        stmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return maintenance;
	}


	@Override
	public int create(Maintenance maintenance) {
		System.out.println("Maintenance DAO API" + maintenance);
		try {
			// Collection de maintenance worker ids
			Collection<Integer> workerIds = 
				maintenance
					.getMaintenanceWorkers()
					.stream()
					.map(w -> w.getId().get())
					.toList();
			
			// Créer un descripteur pour le type ARRAY défini dans Oracle
		    ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("PKG_MAINTENANCES.MAINTENANCE_WORKER_IDS", connection);

		    // Créer l'objet ARRAY avec les données
		    ARRAY workerIdsArray = new ARRAY(descriptor, connection, workerIds.toArray());
			for (Integer integer : workerIds) {
				System.out.println("Worker id : " + integer);
			}
			CallableStatement stmt = connection.prepareCall(
				"BEGIN ? := PKG_MAINTENANCES.insert_maintenance(?, ?, ?, ?, ?, ?, ?, ?);  END;"
			);
			
			stmt.registerOutParameter(1, OracleTypes.INTEGER);
			stmt.setTimestamp(2, Timestamp.valueOf(maintenance.getStartDateTime()));
			stmt.setTimestamp(
				3, 
				maintenance.getEndDateTime().isPresent() 
					? Timestamp.valueOf(maintenance.getEndDateTime().get()) 
					: null
			);
			stmt.setObject(4, maintenance.getDuration().orElse(null), java.sql.Types.INTEGER);
			stmt.setString(5, maintenance.getReport().orElse(null));
			System.out.println("maintenance status : " + maintenance.getStatus().ordinal() + 1);
			stmt.setInt(6, maintenance.getStatus().ordinal() + 1);
			stmt.setInt(7, maintenance.getMachine().getId().get());
			System.out.println("maintenance responsable id : " + maintenance.getMaintenanceResponsable().getId().get());
			stmt.setInt(8, maintenance.getMaintenanceResponsable().getId().get());
			stmt.setArray(9, workerIdsArray);
			
			stmt.execute();
			
			int maintenanceId = stmt.getInt(1);
			System.out.println("Maintenance id : " + maintenanceId);
			
			return maintenanceId;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Maintenance object) {
		try {
			CallableStatement stmt = connection.prepareCall("{ ? = call Pkg_maintenances.update_maintenance(?, ?, ?, ?, ?, ?) }");
			
			stmt.registerOutParameter(1, OracleTypes.INTEGER);
			stmt.setInt(2, object.getId().get());
			stmt.setTimestamp(3, Timestamp.valueOf(object.getStartDateTime()));
			stmt.setTimestamp(4, object.getEndDateTime().isPresent() ? Timestamp.valueOf(object.getEndDateTime().get()) : null);
			stmt.setObject(5, object.getDuration().orElse(null), java.sql.Types.INTEGER);
			stmt.setString(6, object.getReport().get());
			System.out.println("maintenance statusdzzdzdzdzd : " + object.getStatus().ordinal() + 1);
			stmt.setInt(7, object.getStatus().ordinal() + 1);
			
			stmt.execute();
			
			System.out.println("Nombre de lignes affectées  : " + stmt.getInt(1));
			
			return stmt.getInt(1) == 1;	
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}