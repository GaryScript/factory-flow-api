package be.alb_mar_hen.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.OrderDAO;
import be.alb_mar_hen.javabeans.Order;

@Path("/order")
public class OrderAPI {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrders() {
	    try {
	        Connection connection = FactoryFlowConnection.getInstance();
	        OrderDAO orderDAO = new OrderDAO(connection);
	        List<Order> orders = orderDAO.findAll();
	        
	        if (orders.isEmpty()) {
	            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "No orders found.").build();
	        }

	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new Jdk8Module()); 

	        String ordersJson = objectMapper.writeValueAsString(orders);

	        return Response
	            .status(Response.Status.OK)
	            .entity(ordersJson)
	            .build();

	    } catch (SQLException e) {
	    	e.printStackTrace();
	        return Response
	            .status(
	                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
	                "Database error: " + e.getMessage()
	            )
	            .build();

	    } catch (Exception e) {
	    	e.printStackTrace();
	        return Response
	            .status(
	                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
	                "Error processing data: " + e.getMessage()
	            )
	            .build();
	    }
	}
}
