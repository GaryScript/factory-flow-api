package be.alb_mar_hen.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MaintenanceWorkerDAO;
import be.alb_mar_hen.javabeans.MaintenanceWorker;
import be.alb_mar_hen.validators.ObjectValidator;

@Path("/maintenanceWorker")
public class MaintenanceWorkerAPI {
	MaintenanceWorkerDAO maintenanceWorkerDAO = new MaintenanceWorkerDAO(FactoryFlowConnection.getInstance());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaintenanceWorker() {
		try {
			List<MaintenanceWorker> maintenanceWorkers = 
				MaintenanceWorker.getMaintenancesFromDatabase(maintenanceWorkerDAO);
			
			ObjectValidator objectValidator = new ObjectValidator();
			if (!objectValidator.hasValue(maintenanceWorkers) || maintenanceWorkers.isEmpty()) {
				return RequestFactory.createNotFoundResponse("No maintenance worker found.");
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());
			
			String maintenanceWorkersJson = objectMapper.writeValueAsString(maintenanceWorkers);
			
			return RequestFactory.createOkResponse(maintenanceWorkersJson);
		} catch (Exception e) {
			e.printStackTrace();
			return RequestFactory.createServerErrorResponse("Error processing the maintenance worker. " + e.getMessage());
		}
	}
}
