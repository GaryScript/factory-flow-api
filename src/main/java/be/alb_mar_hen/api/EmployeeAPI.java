package be.alb_mar_hen.api;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.EmployeeDAO;
import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;
import be.alb_mar_hen.javabeans.PurchasingAgent;


@Path("/Employee")
public class EmployeeAPI{
	
	@POST
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticateUser(String jsonInput) {
	    JSONObject input = new JSONObject(jsonInput);
	    String matricule = input.optString("matricule");
	    String password = input.optString("password");

	    if (matricule.isEmpty() || password.isEmpty()) {
	        return Response.status(Status.BAD_REQUEST)
	                .entity("{\"error\": \"Matricule and password are required.\"}")
	                .build();
	    }

	    try {
	        Connection connection = FactoryFlowConnection.getInstance();
	        EmployeeDAO employeeDAO = new EmployeeDAO(connection);

	        Employee employee = employeeDAO.authenticate(matricule, password);

	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new Jdk8Module()); 

	        String employeeJson = objectMapper.writeValueAsString(employee);
	        JSONObject employeeJsonObject = new JSONObject(employeeJson);
	        
	        String role = "";
	        if (employee instanceof MaintenanceResponsable) {
	            role = "Maintenance Responsable";
	        } else if (employee instanceof MaintenanceWorker) {
	            role = "Maintenance Worker";
	        } else if (employee instanceof PurchasingAgent) {
	            role = "Purchasing Agent";
	        }

	        employeeJsonObject.put("role", role);

	        return Response.ok(employeeJsonObject.toString()).build();

	    } catch (SQLException e) {
	        if (e.getErrorCode() == 20001) {
	            return Response.status(Status.UNAUTHORIZED)
	                    .entity("{\"error\": \"Invalid matricule or password.\"}")
	                    .build();
	        } else {
	            return Response.status(Status.INTERNAL_SERVER_ERROR)
	                    .entity("{\"error\": \"An error occurred while authenticating the user: " + e.getMessage() + "\"}")
	                    .build();
	        }
	    } catch (JsonProcessingException e) {
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("{\"error\": \"An error occurred while processing the response.\"}")
	                .build();
	    } catch (Exception e) {
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("{\"error\": \"An unexpected error occurred: " + e.getMessage() + "\"}")
	                .build();
	    }
	}

	// to check if the api is responding
	@GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        String message = "{\"message\": \"Authentication service is up and running!\"}";
        return Response.ok(message).build();
    }
}
