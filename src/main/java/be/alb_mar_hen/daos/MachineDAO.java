package be.alb_mar_hen.daos;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.MaintenanceStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;


import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;
import be.alb_mar_hen.utils.Conversion;

public class MachineDAO implements DAO<Machine>{
	private Connection connection = null;
	
	public MachineDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Machine> findall() throws SQLException {
	    List<Machine> machines = new ArrayList<>();
	    CallableStatement stmt = null;

	    try {
	        String call = "{CALL fetch_machine_data(?)}";
	        stmt = connection.prepareCall(call);
	        stmt.registerOutParameter(1, Types.ARRAY, "MACHINE_PACKAGE.MACHINE_DATA_TAB");
	        stmt.execute();

	        java.sql.Array machineArray = stmt.getArray(1);
	        Object[] machineResults = (Object[]) machineArray.getArray();

	        for (Object machineResult : machineResults) {
	            Struct machineRow = (Struct) machineResult;
	            Object[] machineAttributes = machineRow.getAttributes();

	            int machineId = Conversion.extractInt(machineAttributes[0]);
	            String machineTypeName = (String) machineAttributes[1];
	            double machineTypePrice = Conversion.extractDouble(machineAttributes[2]);
	            int machineTypeDaysBeforeMaintenance = Conversion.extractInt(machineAttributes[3]);
	            String machineName = (String) machineAttributes[4];
	            MachineStatus status = MachineStatus.fromString((String) machineAttributes[5]);

	            int zoneId = Conversion.extractInt(machineAttributes[6]);
	            String zoneName = (String) machineAttributes[7];
	            ZoneColor zoneColor = ZoneColor.fromDatabaseValue((String) machineAttributes[8]);
	            int siteId = Conversion.extractInt(machineAttributes[9]);
	            String siteCity = (String) machineAttributes[10];

	            NumericValidator numericValidator = new NumericValidator();
	            StringValidator stringValidator = new StringValidator();
	            ObjectValidator objectValidator = new ObjectValidator();
	            StringFormatter stringFormatter = new StringFormatter();

	            Zone zone = new Zone(
	                Optional.of(zoneId),
	                zoneColor,
	                zoneName,
	                Optional.of(siteId),
	                siteCity,
	                numericValidator,
	                objectValidator,
	                stringValidator
	            );

	            java.sql.Array maintenanceArray = (java.sql.Array) machineAttributes[11];
	            Object[] maintenanceResults = maintenanceArray != null ? (Object[]) maintenanceArray.getArray() : new Object[0];

	            List<Maintenance> maintenances = new ArrayList<>();

	            for (Object maintenanceResult : maintenanceResults) {
	                Struct maintenanceRow = (Struct) maintenanceResult;
	                Object[] maintenanceAttributes = maintenanceRow.getAttributes();

	                int maintenanceId = Conversion.extractInt(maintenanceAttributes[0]);
	                LocalDateTime maintenanceStartDate = Conversion.extractLocalDateTime(maintenanceAttributes[1]);
	                LocalDateTime maintenanceEndDate = Conversion.extractLocalDateTime(maintenanceAttributes[2]);
	                int maintenanceDuration = Conversion.extractInt(maintenanceAttributes[3]);
	                String maintenanceReport = null;
	                Object maintenanceReportObject = maintenanceAttributes[4]; 
	                if (maintenanceReportObject instanceof Clob) {
	                    Clob clob = (Clob) maintenanceReportObject;
	                    maintenanceReport = clob.getSubString(1, (int) clob.length());
	                }
	                String statusValue = (String) maintenanceAttributes[5];
	                MaintenanceStatus maintenanceStatus = MaintenanceStatus.fromString(statusValue);

	                int maintenanceResponsableId = Conversion.extractInt(maintenanceAttributes[6]);
	                String maintenanceResponsableMatricule = (String) maintenanceAttributes[7];
	                String maintenanceResponsablePassword = (String) maintenanceAttributes[8];
	                String maintenanceResponsableFirstName = (String) maintenanceAttributes[9];
	                String maintenanceResponsableLastName = (String) maintenanceAttributes[10];

	                MaintenanceResponsable maintenanceResponsable = new MaintenanceResponsable(
	                    Optional.of(maintenanceResponsableId),
	                    maintenanceResponsableMatricule,
	                    maintenanceResponsablePassword,
	                    maintenanceResponsableFirstName,
	                    maintenanceResponsableLastName,
	                    objectValidator,
	                    stringValidator,
	                    numericValidator,
	                    stringFormatter
	                );

	                int maintenanceWorkerId = Conversion.extractInt(maintenanceAttributes[11]);
	                String maintenanceWorkerMatricule = (String) maintenanceAttributes[12];
	                String maintenanceWorkerPassword = (String) maintenanceAttributes[13];
	                String maintenanceWorkerFirstName = (String) maintenanceAttributes[14];
	                String maintenanceWorkerLastName = (String) maintenanceAttributes[15];

	                MaintenanceWorker maintenanceWorker = new MaintenanceWorker(
	                    Optional.of(maintenanceWorkerId),
	                    maintenanceWorkerMatricule,
	                    maintenanceWorkerPassword,
	                    maintenanceWorkerFirstName,
	                    maintenanceWorkerLastName,
	                    stringValidator,
	                    numericValidator,
	                    stringFormatter,
	                    objectValidator
	                );

	                Maintenance maintenance = new Maintenance(
	                    Optional.of(maintenanceId),
	                    maintenanceStartDate,
	                    Optional.ofNullable(maintenanceEndDate),
	                    Optional.of(maintenanceDuration),
	                    Optional.ofNullable(maintenanceReport),
	                    maintenanceStatus,
	                    null, 
	                    maintenanceWorker,
	                    maintenanceResponsable,
	                    numericValidator,
	                    stringValidator,
	                    objectValidator,
	                    new DateValidator()
	                );

	                maintenances.add(maintenance);
	            }

	            Machine machine = new Machine(
	                Optional.of(machineId),
	                status,
	                machineName,
	                zone,
	                Optional.of(machineId),
	                machineTypeName,
	                machineTypePrice,
	                machineTypeDaysBeforeMaintenance,
	                numericValidator,
	                objectValidator,
	                stringValidator
	            );

	            for (Maintenance maintenance : maintenances) {
	                maintenance.setMachine(machine);
	                machine.addMaintenance(maintenance);
	            }

	            machines.add(machine);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("Error while fetching machines: " + e.getMessage(), e);
	    } finally {
	        if (stmt != null) stmt.close();
	    }

	    return machines;
	}
	
	@Override
	public Machine find() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int create(Machine machine) {
		return 0; // using createMachine instead cause we need the 
		// purchasing agent id, which can't be gotten from the machine itself
	}
	
	public int createMachine(Machine machine, int purchasingAgentId) throws SQLException {
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
            // Log the error message
            System.err.println("Error executing sp_create_machine_and_order: " + e.getMessage());
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
		// TODO Auto-generated method stub
		return false;
	}
}
