package be.alb_mar_hen.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MachineDAO;
import be.alb_mar_hen.daos.PurchasingAgentDAO;
import be.alb_mar_hen.javabeans.PurchasingAgent;

@Path("/purchasingAgents")
public class PurchasingAgentAPI {
	
	PurchasingAgentDAO purchasingAgentDAO = new PurchasingAgentDAO(FactoryFlowConnection.getInstance());
	MachineDAO machineDAO = new MachineDAO(FactoryFlowConnection.getInstance());
	
	@POST
	@Path("/buyMachine")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response buyMachine(String requestBody) {
	    try {
	        String jsonResponse = PurchasingAgent.buyMachine(requestBody);

	        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
	    } catch (IllegalArgumentException e) {
	        ObjectNode errorResponse = new ObjectMapper().createObjectNode();
	        errorResponse.put("status", "failure");
	        errorResponse.put("message", e.getMessage());
	        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse.toString()).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        ObjectNode errorResponse = new ObjectMapper().createObjectNode();
	        errorResponse.put("status", "failure");
	        errorResponse.put("message", "An unexpected error occurred.");
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse.toString()).build();
	    }
	}

}
