package be.alb_mar_hen.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MachineDAO;
import be.alb_mar_hen.daos.MaintenanceDAO;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.validators.ObjectValidator;

@Path("/machine")
public class MachineAPI {
	
	MachineDAO machineDAO = new MachineDAO(FactoryFlowConnection.getInstance());
	
    @POST
    @Path("/buyMachine")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyMachine(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());
            
            Machine machine = objectMapper.readValue(response, Machine.class);
            int purchasingAgentId = objectMapper.readValue(response, JsonNode.class).get("purchasingAgentId").asInt();
            machineDAO.create(machine, purchasingAgentId);
            
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error processing the machine purchase.")
                           .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getMachineById(@PathParam("id") int id) {
		try {
			System.out.println("id: " + id);
			Machine machine = machineDAO.find(id);
			System.out.println("machine: " + machine);
			ObjectValidator objValidator = new ObjectValidator();
			
			if (!objValidator.hasValue(machine)) {
				return RequestFactory.createNotFoundResponse("Machine not found.");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());

			String machineJson = objectMapper.writeValueAsString(machine);

			return RequestFactory.createOkResponse(machineJson);		
		} catch (Exception e) {
			e.printStackTrace();
			return RequestFactory.createServerErrorResponse("Error retrieving machine: " + e.getMessage());
		}
	}
   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMachines() {
        try {
            Collection<Machine> machines = machineDAO.findAll_terry();
            
            ObjectValidator objValidator = new ObjectValidator();
			if (!objValidator.hasValue(objValidator) ||  machines.isEmpty()) {
				return RequestFactory.createNotFoundResponse("No machines found.");
			}

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());

            String machinesJson = objectMapper.writeValueAsString(machines);

            return RequestFactory.createOkResponse(machinesJson);
        } catch (SQLException e) {
            return RequestFactory.createServerErrorResponse("Error retrieving machines: " + e.getMessage());
        } catch (Exception e) {
            return RequestFactory.createServerErrorResponse("Error retrieving machines: " + e.getMessage());
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
	public Response updateMachine(String machineJson) {
    	ObjectValidator objValidator = new ObjectValidator();
    	if (!objValidator.hasValue(machineJson)) {
    		return RequestFactory.createBadRequestResponse("No machine data provided.");
    	}
    	
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());

			System.out.println("machineJson: " + machineJson);
			Machine machine = objectMapper.readValue(machineJson, Machine.class);
			System.out.println("machine: " + machine);
			boolean isMachineUpdated = machine.updateInDatabase(machineDAO);
			if (!isMachineUpdated)
                return RequestFactory.createServerErrorResponse("Failed to update machine status.");
                
			return RequestFactory.createOkResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return RequestFactory.createServerErrorResponse("Error updating machine: " + e.getMessage());
		}
	}
}