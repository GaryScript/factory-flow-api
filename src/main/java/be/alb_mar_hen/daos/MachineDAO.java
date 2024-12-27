package be.alb_mar_hen.daos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.MaintenanceStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.Site;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;

import java.util.Set;

import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;


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

	        // Récupérer le tableau principal de machines
	        java.sql.Array machineArray = stmt.getArray(1);
	        Object[] machineResults = (Object[]) machineArray.getArray();

	        for (Object machineResult : machineResults) {
	            Struct machineRow = (Struct) machineResult;
	            Object[] machineAttributes = machineRow.getAttributes();

	            // Récupérer les attributs de la machine
	            Optional<Integer> machineId = Optional.ofNullable((Integer) machineAttributes[0]);
	            String machineTypeName = (String) machineAttributes[1];
	            double machineTypePrice = (Double) machineAttributes[2];
	            int machineTypeDaysBeforeMaintenance = (Integer) machineAttributes[3];
	            String machineName = (String) machineAttributes[4];
	            MachineStatus status = MachineStatus.valueOf((String) machineAttributes[5]);

	            // Zone et Site
	            Optional<Integer> zoneId = Optional.ofNullable((Integer) machineAttributes[6]);
	            String zoneName = (String) machineAttributes[7];
	            ZoneColor zoneColor = ZoneColor.valueOf((String) machineAttributes[8]);
	            Optional<Integer> siteId = Optional.ofNullable((Integer) machineAttributes[9]);
	            String siteCity = (String) machineAttributes[10];

	            // Construire l'objet Zone
	            NumericValidator numericValidator = new NumericValidator();
	            StringValidator stringValidator = new StringValidator();
	            ObjectValidator objectValidator = new ObjectValidator();
	            StringFormatter stringFormatter = new StringFormatter();

	            Zone zone = new Zone(
	                zoneId,
	                zoneColor,
	                zoneName,
	                siteId,
	                siteCity,
	                numericValidator,
	                objectValidator,
	                stringValidator
	            );

	            // Récupérer le tableau des maintenances
	            java.sql.Array maintenanceArray = (java.sql.Array) machineAttributes[11];
	            Object[] maintenanceResults = maintenanceArray != null ? (Object[]) maintenanceArray.getArray() : new Object[0];

	            List<Maintenance> maintenances = new ArrayList<>();

	            for (Object maintenanceResult : maintenanceResults) {
	                Struct maintenanceRow = (Struct) maintenanceResult;
	                Object[] maintenanceAttributes = maintenanceRow.getAttributes();

	                // Récupérer les attributs de la maintenance
	                Optional<Integer> maintenanceId = Optional.ofNullable((Integer) maintenanceAttributes[0]);
	                LocalDateTime maintenanceStartDate = Optional.ofNullable((Date) maintenanceAttributes[1])
	                    .map(date -> date.toLocalDate().atStartOfDay())
	                    .orElse(null);
	                Optional<LocalDateTime> maintenanceEndDate = Optional.ofNullable((Date) maintenanceAttributes[2])
	                    .map(date -> date.toLocalDate().atStartOfDay());
	                Optional<Integer> maintenanceDuration = Optional.ofNullable((Integer) maintenanceAttributes[3]);
	                Optional<String> maintenanceReport = Optional.ofNullable((String) maintenanceAttributes[4]);
	                MaintenanceStatus maintenanceStatus = MaintenanceStatus.valueOf((String) maintenanceAttributes[5]);

	                // Maintenance Responsable
	                Optional<Integer> maintenanceResponsableId = Optional.ofNullable((Integer) maintenanceAttributes[6]);
	                String maintenanceResponsableMatricule = (String) maintenanceAttributes[7];
	                String maintenanceResponsablePassword = (String) maintenanceAttributes[8];
	                String maintenanceResponsableFirstName = (String) maintenanceAttributes[9];
	                String maintenanceResponsableLastName = (String) maintenanceAttributes[10];

	                MaintenanceResponsable maintenanceResponsable = new MaintenanceResponsable(
	                    maintenanceResponsableId,
	                    maintenanceResponsableMatricule,
	                    maintenanceResponsablePassword,
	                    maintenanceResponsableFirstName,
	                    maintenanceResponsableLastName,
	                    objectValidator,
	                    stringValidator,
	                    numericValidator,
	                    stringFormatter
	                );

	                // Maintenance Worker
	                Optional<Integer> maintenanceWorkerId = Optional.ofNullable((Integer) maintenanceAttributes[11]);
	                String maintenanceWorkerMatricule = (String) maintenanceAttributes[12];
	                String maintenanceWorkerPassword = (String) maintenanceAttributes[13];
	                String maintenanceWorkerFirstName = (String) maintenanceAttributes[14];
	                String maintenanceWorkerLastName = (String) maintenanceAttributes[15];

	                MaintenanceWorker maintenanceWorker = new MaintenanceWorker(
	                    maintenanceWorkerId,
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
	                    maintenanceId,
	                    maintenanceStartDate,
	                    maintenanceEndDate,
	                    maintenanceDuration,
	                    maintenanceReport,
	                    maintenanceStatus,
	                    null, // La machine sera associée après
	                    maintenanceWorker,
	                    maintenanceResponsable,
	                    numericValidator,
	                    stringValidator,
	                    objectValidator,
	                    new DateValidator()
	                );

	                maintenances.add(maintenance);
	            }

	            // Construire la machine
	            Machine machine = new Machine(
	                machineId,
	                machineTypeName,
	                status,
	                machineName,
	                zone,
	                Optional.ofNullable((Integer) machineAttributes[0]),
	                machineTypeName,
	                machineTypePrice,
	                machineTypeDaysBeforeMaintenance,
	                numericValidator,
	                objectValidator,
	                stringValidator
	            );

	            // Associer les maintenances à la machine
	            for (Maintenance maintenance : maintenances) {
	                maintenance.setMachine(machine);
	            }

	            machines.add(machine);
	        }

	    } catch (SQLException e) {
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
	public int create(Machine object) {
		// TODO Auto-generated method stub
		return 0;
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
