package be.alb_mar_hen.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MaintenanceWorkerDAO;
import be.alb_mar_hen.javabeans.MaintenanceWorker;

@Path("/maintenanceWorker")
public class MaintenanceWorkerAPI {
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorker(@PathParam("id") int workerId) {
        MaintenanceWorkerDAO maintenanceWorkerDAO = new MaintenanceWorkerDAO(FactoryFlowConnection.getInstance());
        MaintenanceWorker worker = MaintenanceWorker.find(maintenanceWorkerDAO, workerId);
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        
		try {
			String jsonString = objectMapper.writeValueAsString(worker);
			return Response.status(Response.Status.OK).entity(jsonString).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }
}