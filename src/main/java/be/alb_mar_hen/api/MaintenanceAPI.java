package be.alb_mar_hen.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MaintenanceDAO;
import be.alb_mar_hen.javabeans.Maintenance;

@Path("/maintenance")
public class MaintenanceAPI {
	MaintenanceDAO maintenanceDAO = new MaintenanceDAO(FactoryFlowConnection.getInstance());
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaintenance() {
		try {
			List<Maintenance> maintenances = maintenanceDAO.findall();
			
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new Jdk8Module());
	        
	        String jsonString = objectMapper.writeValueAsString(maintenances);
	        
	        System.out.println("Json : " + jsonString);
	        
			return Response.status(Status.OK)
					.entity(jsonString)
					.build();
		}catch (Exception e) {
			return Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		}
    }
}
