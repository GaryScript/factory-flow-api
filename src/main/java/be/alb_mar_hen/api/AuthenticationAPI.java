package be.alb_mar_hen.api;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

import be.alb_mar_hen.daos.FactoryFlowConnection;


@Path("/login")
public class AuthenticationAPI{
	
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

	    Connection connection = null;
	    CallableStatement stmt = null;

	    try {
	        connection = FactoryFlowConnection.getInstance();

	        String call = "{CALL sp_check_user_authentication(?, ?, ?, ?, ?, ?)}";
	        stmt = connection.prepareCall(call);
	        stmt.setString(1, matricule);
	        stmt.setString(2, password);
	        stmt.registerOutParameter(3, java.sql.Types.VARCHAR); // firstName
	        stmt.registerOutParameter(4, java.sql.Types.VARCHAR); // lastName
	        stmt.registerOutParameter(5, java.sql.Types.VARCHAR); // role
	        stmt.registerOutParameter(6, java.sql.Types.INTEGER); // employee_id

	        stmt.execute();

	        String firstName = stmt.getString(3);
	        String lastName = stmt.getString(4);
	        String role = stmt.getString(5);
	        int employeeId = stmt.getInt(6);

	        JSONObject responseJson = new JSONObject();
	        responseJson.put("employeeId", employeeId); 
	        responseJson.put("matricule", matricule);
	        responseJson.put("firstName", firstName);
	        responseJson.put("lastName", lastName);
	        responseJson.put("role", role);

	        return Response.status(Status.OK).entity(responseJson.toString()).build();

	    } catch (SQLException e) {
	        String errorMessage;

	        switch (e.getErrorCode()) {
	            case 20001:
	                errorMessage = "Invalid matricule or password.";
	                break;
	            case 20002:
	                errorMessage = "Role not found for the employee.";
	                break;
	            default:
	                errorMessage = "An unexpected error occurred: " + e.getMessage();
	        }

	        return Response.status(Status.UNAUTHORIZED)
	                .entity("{\"error\": \"" + errorMessage + "\"}")
	                .build();

	    } finally {
	        try {
	            if (stmt != null) stmt.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
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
