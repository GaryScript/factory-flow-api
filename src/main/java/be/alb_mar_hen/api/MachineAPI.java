package be.alb_mar_hen.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@Path("/machines")
public class MachineAPI {
	
	MachineDAO machineDAO = new MachineDAO(FactoryFlowConnection.getInstance());
	

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMachines() {
        try {
        	Connection connection = FactoryFlowConnection.getInstance();
            MachineDAO machineDAO = new MachineDAO(connection);
            List<Machine> machines = machineDAO.findall();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());

            String machinesJson = objectMapper.writeValueAsString(machines);

            return Response.ok(machinesJson).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error retrieving machines")
                           .build();
        }
    }
    
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
            machineDAO.createMachine(machine, purchasingAgentId);
            
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error processing the machine purchase.")
                           .build();
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMachines_terry() {
        try {
        	Connection connection = FactoryFlowConnection.getInstance();
            MachineDAO machineDAO = new MachineDAO(connection);
            Collection<Machine> machines = machineDAO.findAll_terry();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());

            String machinesJson = objectMapper.writeValueAsString(machines);

            return Response.status(Response.Status.OK).entity(machinesJson).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing data: " + e.getMessage()).build();
        }
    }
}