package be.alb_mar_hen.api;

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
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MaintenanceDAO;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;

@Path("/maintenance")
public class MaintenanceAPI {
	MaintenanceDAO maintenanceDAO = new MaintenanceDAO(FactoryFlowConnection.getInstance());
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaintenance() {
		try {
			List<Maintenance> maintenances = maintenanceDAO.findAll();
			
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new Jdk8Module());
	        
	        String jsonString = objectMapper.writeValueAsString(maintenances);
	        
			return Response.status(Status.OK)
					.entity(jsonString)
					.build();
			
		}catch (Exception e) {
			e.printStackTrace();
			return Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.build();
		}
    }
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaintenance(@PathParam("id") int id) {
		if(id <= 0) {
            return Response.status(Status.BAD_REQUEST).build();
        }
		
		try {
			Maintenance maintenance = maintenanceDAO.find(id);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());
			
			String jsonString = objectMapper.writeValueAsString(maintenance);

			return Response.status(Status.OK).entity(jsonString).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateMaintenance(String maintenanceJson) {
		if (maintenanceJson == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}	
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());
			
			System.out.println("maintenance json" + maintenanceJson);
			Maintenance maintenance = objectMapper.readValue(maintenanceJson, Maintenance.class);

			if(maintenance.updateInDatabase(maintenanceDAO)) {
				return Response.status(Status.OK).build();
            }else {
            	return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMaintenance(String maintenanceJson) {
		System.out.println("maintenanceJson: " + maintenanceJson);
		ObjectValidator objectValidator = new ObjectValidator();
		
		if (!objectValidator.hasValue(maintenanceJson)) {
			return RequestFactory.createBadRequestResponse("The request body is empty.");
		}	
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());
			
			Maintenance maintenance = objectMapper.readValue(maintenanceJson, Maintenance.class);
			
			int id = maintenance.insertInDatabase(maintenanceDAO);
			NumericValidator numericValidator = new NumericValidator();
			if(!numericValidator.isPositive(id)) {
				return RequestFactory.createServerErrorResponse("The maintenance could not be added to the database.");
            }
			
			JSONObject path = new JSONObject();
			path.put("path", "/maintenance/" + id);
			
			return RequestFactory.createOkResponse(path.toString());
            
		} catch (Exception e) {
			e.printStackTrace();
			return RequestFactory.createServerErrorResponse("The maintenance could not be added to the database.");
		}
	}
}