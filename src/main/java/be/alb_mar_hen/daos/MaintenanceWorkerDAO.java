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

import javax.swing.filechooser.FileNameExtensionFilter;

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
	private ObjectValidator objectValidator = new ObjectValidator();
	
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
	            maintenanceWorkers.add(parseMaintenanceWorker(maintenanceWorkerRow));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error processing the maintenance workers. " + e.getMessage());
	    }

	    return maintenanceWorkers;
	}

	private MaintenanceWorker parseMaintenanceWorker(Struct maintenanceWorkerRow) throws SQLException {
	    Object[] maintenanceWorkerAttributes = maintenanceWorkerRow.getAttributes();

	    // Extract employee details
	    Struct employeeStruct = (Struct) maintenanceWorkerAttributes[0];
	    Object[] employeeData = employeeStruct.getAttributes();
	    MaintenanceWorker maintenanceWorker = new MaintenanceWorker(
	        Optional.of(((BigDecimal) employeeData[0]).intValue()), // employee_id
	        (String) employeeData[1], // matricule
	        (String) employeeData[2], // password
	        (String) employeeData[3], // firstName
	        (String) employeeData[4], // lastName
	        new StringValidator(),
	        new NumericValidator(),
	        new StringFormatter(),
	        new ObjectValidator()
	    );

	    // Extract maintenances
	    Array maintenancesArray = (Array) maintenanceWorkerAttributes[1];
	    if (objectValidator.hasValue(maintenancesArray)) {
	        ResultSet rsMaintenances = maintenancesArray.getResultSet();
	        while (rsMaintenances.next()) {
	            Struct maintenanceRow = (Struct) rsMaintenances.getObject(2);
	            Maintenance maintenance = parseMaintenance(maintenanceRow, maintenanceWorker);
	            maintenanceWorker.addMaintenance(maintenance);
	        }
	    }

	    return maintenanceWorker;
	}

	private Maintenance parseMaintenance(Struct maintenanceRow, MaintenanceWorker maintenanceWorker) throws SQLException {
	    Object[] maintenanceRecordData = maintenanceRow.getAttributes();

	    // Extract machine details
	    Struct machineStruct = (Struct) maintenanceRecordData[0];
	    Struct machineTypeStruct = (Struct) maintenanceRecordData[1];
	    Struct maintenanceResponsableStruct = (Struct) maintenanceRecordData[2];
	    Struct siteStruct = (Struct) maintenanceRecordData[3];
	    Struct maintenanceStruct = (Struct) maintenanceRecordData[5];
	    Array zonesArray = (Array) maintenanceRecordData[4];
	    
	    Object[] maintenanceData = maintenanceStruct.getAttributes();

	    return new Maintenance(
	        Optional.of(((BigDecimal) maintenanceData[0]).intValue()),                			// maintenance_id
	        Conversion.extractLocalDateTime(maintenanceData[1]),                      			// start_date
	        Optional.ofNullable(Conversion.extractLocalDateTime(maintenanceData[2])), 			// end_date
	        Optional.ofNullable(((BigDecimal) maintenanceData[3]).intValue()),        			// duration
	        Optional.ofNullable((String) maintenanceData[4]),                                   // report
	        MaintenanceStatus.fromDatabaseValue(((BigDecimal) maintenanceData[5]).intValue()),  // status
	        parseMachine(machineStruct, machineTypeStruct, zonesArray, siteStruct), 			// machine
	        maintenanceWorker,																	// maintenance_worker
	        parseMaintenanceResponsable(maintenanceResponsableStruct), 							// maintenance_responsable
	        new NumericValidator(),
	        new StringValidator(),
	        new ObjectValidator(),
	        new DateValidator()
	    );
	}

	private Machine parseMachine(
		Struct machineStruct, 
		Struct machineTypeStruct,
		Array zonesArray, 
		Struct siteStruct
	) throws SQLException 
	{
	    Object[] machineData = machineStruct.getAttributes();
	    Object[] machineTypeData = machineTypeStruct.getAttributes();
	    
	    return new Machine(
	        Optional.of(((BigDecimal) machineData[0]).intValue()),                      // machine_id
	        MachineStatus.fromDatabaseValue(((BigDecimal) machineData[1]).intValue()),  // status
	        (String) machineData[2],                                                    // name
	        parseZones(zonesArray, siteStruct),  										// zones
	        Optional.of(((BigDecimal) machineTypeData[0]).intValue()), 					// machine_type_id
	        (String) machineTypeData[1], 												// machine_type_name
	        ((BigDecimal) machineTypeData[2]).doubleValue(), 							// price
	        ((BigDecimal) machineTypeData[3]).intValue(), 								// days_before_maintenance
	        new NumericValidator(),
	        new ObjectValidator(),
	        new StringValidator()
	    );
	}

	private Set<Zone> parseZones(Array zonesArray, Struct siteStruct) throws SQLException {
	    Set<Zone> zones = new HashSet<>();

	    // Extract site details
	    Object[] siteData = siteStruct.getAttributes();
	    int siteId = ((BigDecimal) siteData[0]).intValue();
	    String siteCity = (String) siteData[1];

	    // Extract zones
	    if (zonesArray != null) {
	        ResultSet rsZones = zonesArray.getResultSet();
	        while (rsZones.next()) {
	            Struct zoneStruct = (Struct) rsZones.getObject(2);
	            Object[] zoneData = zoneStruct.getAttributes();
	            System.out.println("zoneData: " + zoneData[1]);
	            zones.add(
            		new Zone(
		                Optional.of(((BigDecimal) zoneData[0]).intValue()), 				// zone_id
		                ZoneColor.fromDatabaseValue(((BigDecimal) zoneData[2]).intValue()), // color
		                (String) zoneData[1], 												// name
		                Optional.of(siteId), 												// site_id
		                siteCity, 														    // site_name
		                new NumericValidator(),
		                new ObjectValidator(),
		                new StringValidator()
	            	)
	            );
	        }
	    }

	    return zones;
	}

	private MaintenanceResponsable parseMaintenanceResponsable(Struct maintenanceResponsableStruct) throws SQLException {
	    Object[] responsableData = maintenanceResponsableStruct.getAttributes();

	    return new MaintenanceResponsable(
	        Optional.of(((BigDecimal) responsableData[0]).intValue()),  // id
	        (String) responsableData[1],                                // matricule
	        (String) responsableData[2], 								// password
	        (String) responsableData[3],								// firstName
	        (String) responsableData[4], 								// lastName
	        new ObjectValidator(),
	        new StringValidator(),
	        new NumericValidator(),
	        new StringFormatter()
	    );
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
			maintenanceWorker = parseMaintenanceWorker(maintenanceWorkerRow);
			
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
