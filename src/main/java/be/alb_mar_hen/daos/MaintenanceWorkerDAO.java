package be.alb_mar_hen.daos;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
import oracle.jdbc.OracleTypes;

public class MaintenanceWorkerDAO implements DAO<MaintenanceWorker>{
	private Connection connection = null;
	
	public MaintenanceWorkerDAO(Connection connection) {
		if (connection != null) {
			this.connection = connection;
		}
	}

	@Override
	public List<MaintenanceWorker> findAll() {
		String query = "BEGIN ? := PKG_MAINTENANCE_WORKERS_TERRY.get_maintenance_workers(); END;";
		List<MaintenanceWorker> maintenanceWorkers = new ArrayList<>();
		
		try (CallableStatement stmt = connection.prepareCall(query)) {
			stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_MAINTENANCE_WORKERS_TERRY.MAINTENANCE_WORKER_COLLECTION");
			stmt.execute();
			
			Array array = stmt.getArray(1);
			ResultSet rsMaintenanceWorker = array.getResultSet();
			
			while (rsMaintenanceWorker.next()) {
				Struct maintenanceWorkerRow = (Struct) rsMaintenanceWorker.getObject(2);
				maintenanceWorkers.add(getMaitenanceWorkerFromResultSet(maintenanceWorkerRow));
			}
		} catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing the maintenance workers. " + e.getMessage());
        }
		
		return maintenanceWorkers;
	}
	
	private MaintenanceWorker getMaitenanceWorkerFromResultSet(Struct maintenanceWorkerRow) throws SQLException {
		Object[] MaintenanceWorkerAttributes = maintenanceWorkerRow.getAttributes();
		
		// Create a new maintenance worker
		Struct maintenanceWorkerStruct = (Struct) MaintenanceWorkerAttributes[2];
		Object[] maintenanceWorkerData = maintenanceWorkerStruct.getAttributes();
		
		MaintenanceWorker maintenanceWorker = new MaintenanceWorker(
			Optional.of(((BigDecimal) maintenanceWorkerData[0]).intValue()),
			(String) maintenanceWorkerData[1],
			(String) maintenanceWorkerData[2],
			(String) maintenanceWorkerData[3],
			(String) maintenanceWorkerData[4],
			new StringValidator(),
			new NumericValidator(),
			new StringFormatter(),
			new ObjectValidator()
	    );
		
		Set<Maintenance> maintenances = getMaintenancesFromResultSet(MaintenanceWorkerAttributes, maintenanceWorker);
		maintenances.forEach(maintenance -> maintenanceWorker.addMaintenance(maintenance));

		return maintenanceWorker;
	}
	
	
	private Set<Maintenance> getMaintenancesFromResultSet(
		Object[] MaintenanceWorkerAttributes, 
		MaintenanceWorker maintenanceWorker
	) throws SQLException 
	{
		ObjectValidator objectValidator = new ObjectValidator();
		Set<Maintenance> maintenances = new HashSet<>();
		
		Array maintenancesArray = (Array) MaintenanceWorkerAttributes[0];
		if (!objectValidator.hasValue(maintenancesArray)) {
			return maintenances;
		}
		
		ResultSet rsMaintenances = maintenancesArray.getResultSet();
		while (rsMaintenances.next()) {
			Struct maintenanceRow = (Struct) rsMaintenances.getObject(2);
			Object[] maintenanceData = maintenanceRow.getAttributes();
			
			// Extract the maintenance
			maintenances.add(
				new Maintenance(
					Optional.of(((BigDecimal) maintenanceData[0]).intValue()),
					Conversion.extractLocalDateTime(maintenanceData[1]),
					Optional.ofNullable(Conversion.extractLocalDateTime(maintenanceData[2])),
					Optional.ofNullable(((BigDecimal) maintenanceData[3]).intValue()), 
					Optional.ofNullable((String) maintenanceData[4]),
					MaintenanceStatus.fromDatabaseValue(((BigDecimal) maintenanceData[5]).intValue()), 
					getMachineFromResultSet(
						MaintenanceWorkerAttributes[3],
						MaintenanceWorkerAttributes[5],
						MaintenanceWorkerAttributes[1],
						MaintenanceWorkerAttributes[4]
					), 
					maintenanceWorker,
					getMaintenanceResponsableFromResultSet(MaintenanceWorkerAttributes[6]), 
					new NumericValidator(), 
					new StringValidator(),
					new ObjectValidator(), 
					new DateValidator()
				)
			);
		}
		
		return maintenances;
	}
	
	private MaintenanceResponsable getMaintenanceResponsableFromResultSet(Object maintenanceReponsableRaw) throws SQLException {
		Struct maintenanceResponsableStruct = (Struct) maintenanceReponsableRaw;
		Object[] maintenanceResponsableData = maintenanceResponsableStruct.getAttributes();

		return new MaintenanceResponsable(
			Optional.of(((BigDecimal) maintenanceResponsableData[0]).intValue()),
			(String) maintenanceResponsableData[1], 
			(String) maintenanceResponsableData[2], 
			(String) maintenanceResponsableData[3],
			(String) maintenanceResponsableData[4],
			new ObjectValidator(),
			new StringValidator(),
			new NumericValidator(), 
			new StringFormatter()
		);
	}
	
	private Machine getMachineFromResultSet(
		Object machineRow,
		Object machineTypeRow,
		Object zonesArray,
		Object siteRow
	) throws SQLException 
	{
		Struct machineStruct = (Struct) machineRow;
		Struct machineTypeStruct = (Struct) machineTypeRow;
		
		Object[] machineData = machineStruct.getAttributes();
		Object[] machineTypeData = machineTypeStruct.getAttributes();
		
		return new Machine(
			Optional.of(((BigDecimal) machineData[0]).intValue()),
            MachineStatus.fromDatabaseValue(((BigDecimal) machineData[1]).intValue()), 
            (String) machineData[2], 
            getZonesFromResultSet_terry(zonesArray, siteRow),
            Optional.of(((BigDecimal) machineTypeData[0]).intValue()),
            (String) machineTypeData[1],
            ((BigDecimal) machineTypeData[2]).doubleValue(),
            ((BigDecimal) machineTypeData[3]).intValue(),
            new NumericValidator(), 
            new ObjectValidator(),
            new StringValidator()
		);
	}
	
	private Set<Zone> getZonesFromResultSet_terry(Object zonesArray, Object siteArray) throws SQLException {
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
	
	private Set<Zone> getZonesFromResultSet() {
		// TODO
		return null;
	}
	
	@Override
	public MaintenanceWorker find(int id) {
		String query = "BEGIN ? := PKG_MAINTENANCE_WORKERS_TERRY.get_maintenance_worker(?); END;";
		MaintenanceWorker maintenanceWorker = null;
		
		try (CallableStatement stmt = connection.prepareCall(query)) {
			stmt.registerOutParameter(1, OracleTypes.STRUCT, "PKG_MAINTENANCE_WORKERS_TERRY.MAINTENANCE_WORKER_RECORD");
			stmt.setInt(2, id);
			stmt.execute();
			
			Struct maintenanceWorkerRow = (Struct) stmt.getObject(1);
			maintenanceWorker = getMaitenanceWorkerFromResultSet(maintenanceWorkerRow);
			
		} catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing the maintenance workers. " + e.getMessage());
        }
		
		return maintenanceWorker;
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
