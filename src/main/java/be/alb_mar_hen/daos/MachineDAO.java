package be.alb_mar_hen.daos;

import java.math.BigDecimal;
import java.security.KeyStore.PrivateKeyEntry;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.loading.PrivateClassLoader;

import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.MaintenanceStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;


import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import be.alb_mar_hen.utils.Conversion;

public class MachineDAO implements DAO<Machine>{
	private Connection connection = null;
	
	public MachineDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Machine> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	    
	
	public Collection<Machine> findAll_terry() throws SQLException {
	    String sql = "BEGIN ? := PKG_MACHINES.get_machines(); END;";
	    Collection<Machine> machines = new ArrayList<>();

	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MACHINES.MACHINE_COLLECTION");
	        stmt.execute();

	        Array machineArray = stmt.getArray(1); // Récupération de l'ARRAY
	        ResultSet rsMachines = machineArray.getResultSet();

	        while (rsMachines.next()) {
	            // Récupération de l'objet STRUCT représentant une machine
	            Struct machineRow = (Struct) rsMachines.getObject(2);
	            machines.add(getMachineFromResultSet_terry(machineRow));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("Error while fetching machines: " + e.getMessage(), e);
	    }

	    return machines;
	}
	
	@Override
	public Machine find(int id) {
		String sql = "BEGIN ? := PKG_MACHINES.get_machine_by_id(?); END;";
		Machine machine = null;
		
		try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.STRUCT, "PKG_MACHINES.MACHINE_RECORD");
            stmt.setInt(2, id);
            stmt.execute();
            
            Struct machineRow = (Struct) stmt.getObject(1);
            machine = getMachineFromResultSet_terry(machineRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return machine;
	}
	
	 Machine getMachineFromResultSet_terry(Struct machineRow) throws SQLException {
	    // Récupérer les attributs de la machine (dans l'ordre défini dans MACHINE_RECORD)
	    Object[] machineAttributes = machineRow.getAttributes();

	    // Machine
	    Struct machineStruct = (Struct) machineAttributes[0];
	    Object[] machineData = machineStruct.getAttributes();
	    
	    int machineId = ((BigDecimal) machineData[0]).intValue();
	    MachineStatus machineStatus = MachineStatus.fromDatabaseValue(((BigDecimal) machineData[1]).intValue());
	    String machineName = (String) machineData[2];

	    // Machine Type
	    Struct machineTypeStruct = (Struct) machineAttributes[2];
	    Object[] machineTypeData = machineTypeStruct.getAttributes();
	    
	    int machineTypeId = ((BigDecimal) machineTypeData[0]).intValue();
	    String machineTypeName = (String) machineTypeData[1];
	    double machineTypePrice = ((BigDecimal) machineTypeData[2]).doubleValue();
	    int daysBeforeMaintenance = ((BigDecimal) machineTypeData[3]).intValue();

	    // Zones
	    Set<Zone> zones = getZonesFromResultSet_terry(machineAttributes[3], machineAttributes[1]);

	    // Construire la machine
	    Machine machine = new Machine(
	        Optional.of(machineId),
	        machineStatus,
	        machineName,
	        zones,
	        Optional.of(machineTypeId),
	        machineTypeName,
	        machineTypePrice,
	        daysBeforeMaintenance,
	        new NumericValidator(),
	        new ObjectValidator(),
	        new StringValidator()
	    );

	    // Maintenances
	    Set<Maintenance> maintenances = getMaintenancesFromResultSet_terry(machineAttributes[4], machine);
	    
	    maintenances.forEach(machine::addMaintenance);
	    return machine;
	}



	Set<Zone> getZonesFromResultSet_terry(Object zonesArray, Object siteArray) throws SQLException {
	    Set<Zone> zones = new HashSet<>();

	    // Site
	    Struct siteStruct = (Struct) siteArray;
	    Object[] siteAttributes = siteStruct.getAttributes();
	    
	    int siteId = ((BigDecimal) siteAttributes[0]).intValue();
	    String siteCity = (String) siteAttributes[1];

	    // Zones
	    Array zonesSqlArray = (Array) zonesArray;
	    if (zonesSqlArray != null) {
	        ResultSet rsZones = zonesSqlArray.getResultSet();
	        while (rsZones.next()) {
	            Struct zoneStruct = (Struct) rsZones.getObject(2);
	            Object[] zoneData = zoneStruct.getAttributes();

	            int zoneId = ((BigDecimal) zoneData[0]).intValue();
	            String zoneName = (String) zoneData[1];
	            ZoneColor zoneColor = ZoneColor.fromDatabaseValue(((BigDecimal) zoneData[2]).intValue());;

	            zones.add(
            		new Zone(
		                Optional.of(zoneId),
		                zoneColor,
		                zoneName,
		                Optional.of(siteId),
		                siteCity,
		                new NumericValidator(),
		                new ObjectValidator(),
		                new StringValidator()
            		)
            	);
	        }
	    }

	    return zones;
	}

	private Set<Maintenance> getMaintenancesFromResultSet_terry(Object maintenancesArray, Machine associtedMachine) throws SQLException {
	    Set<Maintenance> maintenances = new HashSet<>();

	    Array maintenancesSqlArray = (Array) maintenancesArray;
	    if (maintenancesSqlArray != null) {
	        ResultSet rsMaintenances = maintenancesSqlArray.getResultSet();

	        while (rsMaintenances.next()) {
	            Struct maintenanceStruct = (Struct) rsMaintenances.getObject(2);
	            Object[] maintenanceData = maintenanceStruct.getAttributes();

	            // Extract `maintenance` attributes
	            Struct maintenanceRow = (Struct) maintenanceData[0];
	            Object[] maintenanceRowData = maintenanceRow.getAttributes();
	            
	            int maintenanceId = ((BigDecimal) maintenanceRowData[0]).intValue();
	            LocalDateTime maintenanceStartDate = Conversion.extractLocalDateTime(maintenanceRowData[1]);
	            Optional<LocalDateTime> maintenanceEndDate = Optional.ofNullable(Conversion.extractLocalDateTime(maintenanceRowData[2]));
	            Optional<Integer> maintenanceDuration = Optional.ofNullable((BigDecimal) maintenanceRowData[3]).map(BigDecimal::intValue);
	            Optional<String> maintenanceReport = Optional.ofNullable((String) maintenanceRowData[4]);
	            int maintenanceStatus = ((BigDecimal) maintenanceRowData[5]).intValue();

	            // Extract `maintenance_responsable` attributes
	            Struct responsableStruct = (Struct) maintenanceData[2];
	            Object[] responsableRowData = responsableStruct.getAttributes();
	            MaintenanceResponsable responsable = new MaintenanceResponsable(
            		Optional.of(((BigDecimal) responsableRowData[0]).intValue()),
            		(String) responsableRowData[1],
            		(String) responsableRowData[2],
            		(String) responsableRowData[3], 
            		(String) responsableRowData[4],
            		new ObjectValidator(),
            		new StringValidator(),
            		new NumericValidator(),
            		new StringFormatter()
	            );

	            // Extract `maintenance_workers` (array of employees)
	            Array workersArray = (Array) maintenanceData[3];
	            Set<MaintenanceWorker> workers = new HashSet<>();
	            
	            if (workersArray != null) {
	                ResultSet rsWorkers = workersArray.getResultSet();
	                
	                while (rsWorkers.next()) {
	                    Struct workerStruct = (Struct) rsWorkers.getObject(2);
	                    Object[] workerRowData = workerStruct.getAttributes();

	                    workers.add(
                    		new MaintenanceWorker(
                				Optional.of(((BigDecimal) workerRowData[0]).intValue()), 
                				(String) workerRowData[1],
                				(String) workerRowData[2],
                				(String) workerRowData[3],
                				(String) workerRowData[4],
                				new StringValidator(),
                				new NumericValidator(),
                				new StringFormatter(),
                				new ObjectValidator()
            				)
                		);
	                }
	            }

	            // Create Maintenance object and add to the set
	            Maintenance maintenance = new Maintenance(
	                Optional.of(maintenanceId),
	                maintenanceStartDate,
	                maintenanceEndDate,
	                maintenanceDuration,
	                maintenanceReport,
	                MaintenanceStatus.fromDatabaseValue(maintenanceStatus),
	                associtedMachine,
	                workers,
	                responsable,
	                new NumericValidator(),
	                new StringValidator(),
	                new ObjectValidator(),
	                new DateValidator()
	            );

	            maintenances.add(maintenance);
	        }
	    }

	    return maintenances;
	}
	
	@Override
	public int create(Machine machine) {
		return 0; // using createMachine instead cause we need the 
		// purchasing agent id, which can't be gotten from the machine itself
	}
	
	public int create(Machine machine, int purchasingAgentId) throws SQLException {
        try {
        	String sqlString = "{CALL sp_create_machine_and_order(?, ?, ?, ?, ?)}";
        	
        	CallableStatement callableStatement = connection.prepareCall(sqlString);
        	
        	callableStatement.setInt(1, machine.getId().get());
            callableStatement.setString(2, machine.getName());
            callableStatement.setInt(3, machine.getMachineType().getId().get());
            callableStatement.setInt(4, purchasingAgentId);

            // Register output parameter
            callableStatement.registerOutParameter(5, Types.INTEGER);

            callableStatement.execute();

            return callableStatement.getInt(5);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Machine object) {
		try {
			CallableStatement stmt = connection.prepareCall("BEGIN ? := pkg_machines.update_machine(?, ?, ?, ?); END;");
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, object.getId().get());
			stmt.setInt(3, object.getStatus().ordinal() + 1);
			stmt.setString(4, object.getName());
			stmt.setInt(5, object.getMachineType().getId().get());
			
			stmt.execute();
			
			System.out.println("Nombre de lignes modifiées (machineAPI update()) = " + stmt.getInt(1));
			
			return stmt.getInt(1) == 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
