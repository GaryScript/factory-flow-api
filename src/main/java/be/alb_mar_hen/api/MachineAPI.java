package be.alb_mar_hen.api;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.enumerations.MachineStatus;
import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.models.Machine;
import be.alb_mar_hen.models.Maintenance;
import be.alb_mar_hen.models.MaintenanceResponsable;
import be.alb_mar_hen.models.MaintenanceWorker;
import be.alb_mar_hen.models.Site;
import be.alb_mar_hen.models.Zone;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@Path("/machines")
public class MachineAPI {

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMachines() {
	    Connection connection = null;
	    CallableStatement stmt = null;

	    try {
	        connection = FactoryFlowConnection.getInstance();

	        // call the sql procedure
	        String call = "{CALL fetch_machine_data(?)}";
	        stmt = connection.prepareCall(call);
	        stmt.registerOutParameter(1, Types.ARRAY, "MACHINE_PACKAGE.MACHINE_DATA_TAB");
	        stmt.execute();

	        // claims data
	        java.sql.Array array = stmt.getArray(1);
	        Object[] results = (Object[]) array.getArray();

	        // Create a list to hold the machines
	        List<Machine> machines = new ArrayList<>();

	        // Loop through the results and map them to objects
	        for (Object result : results) {
	            Struct row = (Struct) result;
	            Object[] attributes = row.getAttributes();

	            // Create Machine object using the constructor
	            Machine machine = new Machine(
	                Optional.ofNullable((Integer) attributes[0]), // id
	                (String) attributes[1], // type
	                MachineStatus.valueOf((String) attributes[2]), // status (assuming the status is of MachineStatus enum)
	                (String) attributes[4], // name
	                new Zone( // zone object
	                    Optional.ofNullable((Integer) attributes[6]),
	                    ZoneColor.valueOf((String) attributes[8]), // assuming ZoneColor is an enum
	                    (String) attributes[7],
	                    Optional.ofNullable((Integer) attributes[9]),
	                    (String) attributes[10],
	                    new NumericValidator(),
	                    new ObjectValidator(),
	                    new StringValidator()
	                ),
	                Optional.ofNullable((Integer) attributes[3]), // machineTypeId
	                (String) attributes[11], // machineTypeName
	                (Double) attributes[12], // machineTypePrice
	                (Integer) attributes[13], // machineTypeDaysBeforeMaintenance
	                new HashSet<>(), // maintenances (empty for now)
	                new HashSet<>(), // zones (empty for now)
	                new NumericValidator(),
	                new ObjectValidator(),
	                new StringValidator()
	            );

	            // Add the machine to the list
	            machines.add(machine);
	        }

	        // Use ObjectMapper to convert the list to JSON
	        ObjectMapper mapper = new ObjectMapper();
	        String jsonResponse = mapper.writeValueAsString(machines);

	        // Return the response with machines data in JSON format
	        return Response.status(Status.OK).entity(jsonResponse).build();

	    } catch (SQLException | JsonProcessingException e) {
	        e.printStackTrace();
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("{\"error\": \"An error occurred while fetching machines: " + e.getMessage() + "\"}")
	                .build();
	    } finally {
	        try {
	            if (stmt != null) stmt.close();
	            // if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

}
