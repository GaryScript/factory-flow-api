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

            java.sql.Array array = stmt.getArray(1);
            Object[] results = (Object[]) array.getArray();

            for (Object result : results) {
                Struct row = (Struct) result;
                Object[] attributes = row.getAttributes();

                Optional<Integer> machineId = Optional.ofNullable((Integer) attributes[0]);
                String machineTypeName = (String) attributes[1];
                double machineTypePrice = (Double) attributes[2];
                int machineTypeDaysBeforeMaintenance = (Integer) attributes[3];
                String machineName = (String) attributes[4];
                MachineStatus status = MachineStatus.valueOf((String) attributes[5]);
                
                // Zone et Site
                Optional<Integer> zoneId = Optional.ofNullable((Integer) attributes[6]);
                String zoneName = (String) attributes[7];
                ZoneColor zoneColor = ZoneColor.valueOf((String) attributes[8]);
                Optional<Integer> siteId = Optional.ofNullable((Integer) attributes[9]);
                String siteCity = (String) attributes[10];
                
                // Maintenance
                Optional<Integer> maintenanceId = Optional.ofNullable((Integer) attributes[11]);
                LocalDateTime maintenanceStartDate = Optional.ofNullable((Date) attributes[12])
                        .map(date -> date.toLocalDate().atStartOfDay()) // Convert to LocalDate, then to LocalDateTime
                        .orElse(null);
                Optional<LocalDateTime> maintenanceEndDate = Optional.ofNullable((Date) attributes[13])
                        .map(date -> date.toLocalDate().atStartOfDay()); // Convert to LocalDate, then to LocalDateTime

                Optional<Integer> maintenanceDuration = Optional.ofNullable((Integer) attributes[14]);
                Optional<String> maintenanceReport = Optional.ofNullable((String) attributes[15]);
                MaintenanceStatus maintenanceStatus = MaintenanceStatus.valueOf((String) attributes[16]);
                
                // Maintenance Responsable
                Optional<Integer> maintenanceResponsableId = Optional.ofNullable((Integer) attributes[17]);
                String maintenanceResponsableMatricule = (String) attributes[18];
                String maintenanceResponsablePassword = (String) attributes[19];
                String maintenanceResponsableFirstName = (String) attributes[20];
                String maintenanceResponsableLastName = (String) attributes[21];

                // Maintenance Worker
                Optional<Integer> maintenanceWorkerId = Optional.ofNullable((Integer) attributes[22]);
                String maintenanceWorkerMatricule = (String) attributes[23];
                String maintenanceWorkerPassword = (String) attributes[24];
                String maintenanceWorkerFirstName = (String) attributes[25];
                String maintenanceWorkerLastName = (String) attributes[26];

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

                Site site = zone.getSite();

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
                
                Machine machine = new Machine(
                        machineId, 
                        machineTypeName, 
                        status, 
                        machineName, 
                        zone,
                        Optional.ofNullable((Integer) attributes[0]),
                        machineTypeName, 
                        machineTypePrice, 
                        machineTypeDaysBeforeMaintenance, 
                        numericValidator, 
                        objectValidator, 
                        stringValidator
                    );

                Maintenance maintenance = new Maintenance(
                    maintenanceId, 
                    maintenanceStartDate, 
                    maintenanceEndDate, 
                    maintenanceDuration, 
                    maintenanceReport, 
                    maintenanceStatus, 
                    machine, 
                    maintenanceWorker, 
                    maintenanceResponsable, 
                    numericValidator, 
                    stringValidator, 
                    objectValidator,
                    new DateValidator()
                );
                
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
