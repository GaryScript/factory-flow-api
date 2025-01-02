package be.alb_mar_hen.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MachineDAO;
import be.alb_mar_hen.daos.PurchasingAgentDAO;
import be.alb_mar_hen.javabeans.Machine;

@Path("/purchasingAgents")
public class PurchasingAgentAPI {
	
	PurchasingAgentDAO purchasingAgentDAO = new PurchasingAgentDAO(FactoryFlowConnection.getInstance());
	MachineDAO machineDAO = new MachineDAO(FactoryFlowConnection.getInstance());
	
	@POST
	@Path("/buyMachine")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response buyMachine(String response) {
		
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        
	    try {
	        Machine machine = objectMapper.readValue(response, Machine.class);
	        int purchasingAgentId = objectMapper.readValue(response, JsonNode.class).get("purchasingAgentId").asInt();
	        
	        boolean isCreated = machineDAO.createMachine(machine, purchasingAgentId) != 0;
	        
	        if (isCreated) {
	            ObjectNode successResponse = objectMapper.createObjectNode();
	            successResponse.put("status", "success");
	            successResponse.put("message", "Machine purchase successful.");
	            
	            return Response.status(Response.Status.CREATED)
	                           .entity(successResponse.toString()) 
	                           .build();
	        } else {
	            ObjectNode errorResponse = objectMapper.createObjectNode();
	            errorResponse.put("status", "failure");
	            errorResponse.put("message", "Failed to purchase the machine. Please try again.");
	            
	            return Response.status(Response.Status.BAD_REQUEST)
	                           .entity(errorResponse.toString())
	                           .build();
	        }
	    } catch (Exception e) {

	        e.printStackTrace();
	        
	        ObjectNode errorResponse = objectMapper.createObjectNode();
	        errorResponse.put("status", "failure");
	        errorResponse.put("message", "Error processing the machine purchase.");
	        
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(errorResponse.toString()) // Convertir l'ObjectNode en String JSON
	                       .build();
	    }
	}
}
