package be.alb_mar_hen.api;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MachineDAO;
import be.alb_mar_hen.javabeans.Machine;

@Path("/machines")
public class MachineAPI {

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

}