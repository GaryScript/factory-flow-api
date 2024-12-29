package be.alb_mar_hen.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MaintenanceDAO;
import be.alb_mar_hen.javabeans.Maintenance;

@Path("/maintenance")
public class MaintenanceAPI {
	MaintenanceDAO maintenanceDAO = new MaintenanceDAO(FactoryFlowConnection.getInstance());
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getMaintenance() {
		List<Maintenance> maintenances = maintenanceDAO.findall();
		
		return "Maintenance API";
	}
}
