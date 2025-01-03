package be.alb_mar_hen.daos;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.alb_mar_hen.javabeans.Order;
import be.alb_mar_hen.javabeans.Supplier;
import be.alb_mar_hen.utils.Conversion;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;
import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.MachineType;
import be.alb_mar_hen.javabeans.PurchasingAgent;
import oracle.jdbc.OracleTypes;


public class OrderDAO implements DAO<Order>{
	private Connection connection = null;
	MachineDAO machineDAO = new MachineDAO(connection);
	
	public OrderDAO(Connection connection) {
		if(connection != null) {
			this.connection = connection;
		}
	}
	
	@Override
	public List<Order> findall() throws SQLException {
	    String sql = "BEGIN ? := PKG_ORDERS.get_orders(); END;"; 
	    List<Order> orders = new ArrayList<>();

	    try (CallableStatement stmt = connection.prepareCall(sql)) {
	        stmt.registerOutParameter(1, OracleTypes.ARRAY, "PKG_ORDERS.ORDER_COLLECTION"); // Type Oracle de la collection
	        stmt.execute();

	        java.sql.Array orderArray = stmt.getArray(1);
	        ResultSet rsOrders = orderArray.getResultSet();

	        while (rsOrders.next()) {
	            Struct orderRow = (Struct) rsOrders.getObject(2);
	            orders.add(getOrderFromStruct(orderRow));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("Error while fetching orders: " + e.getMessage(), e);
	    }

	    return orders;
	} 
	
	private Order getOrderFromStruct(Struct orderRow) throws SQLException {
	    Object[] attributes = orderRow.getAttributes();
	    
	    Struct orderStruct = (Struct) attributes[0];
	    LocalDateTime orderDate = Conversion.extractLocalDateTime(
	    		(orderStruct.getAttributes()[3])); // ORDER_DATE

	    Struct machineStruct = (Struct) attributes[1];
	    Machine machine = machineDAO.getMachineFromResultSet_terry(machineStruct);

	    Struct supplierStruct = (Struct) attributes[2];
	    Supplier supplier = getSupplierFromStruct(supplierStruct, machine.getMachineType());
	    System.out.println(supplier);
	    Struct agentStruct = (Struct) attributes[3];
	    PurchasingAgent agent = getAgentFromStruct(agentStruct);
	    
	    return new Order(
	        orderDate, 
	        supplier,
	        agent, 
	        machine,
	        new NumericValidator(),
	        new DateValidator(),
	        new ObjectValidator()
	    );
	}
	
	private Supplier getSupplierFromStruct(Struct supplierStruct, MachineType machineType) throws SQLException {
	    Object[] attributes = supplierStruct.getAttributes();
	    
	    Optional<Integer> supplierId = Optional.of(((BigDecimal) attributes[0]).intValue()); // ID
	    String name = (String) attributes[2]; // NAME

	    return new Supplier(
	        supplierId,
	        name,
	        machineType,
	        new NumericValidator(), 
	        new StringValidator(), 
	        new ObjectValidator()
	    );
	}
	
	private PurchasingAgent getAgentFromStruct(Struct agentStruct) throws SQLException {
	    Object[] attributes = agentStruct.getAttributes(); // Récupérer les attributs du STRUCT

	    Optional<Integer> agentId = Optional.of(((BigDecimal) attributes[0]).intValue());
	    String matricule = (String) attributes[1]; // MATRICULE
	    String password = (String) attributes[2]; // PASSWORD
	    String firstName = (String) attributes[3]; // FIRST_NAME
	    String lastName = (String) attributes[4]; // LAST_NAME

	    return new PurchasingAgent(
	        agentId,
	        matricule,
	        password,
	        firstName,
	        lastName,
	        new StringValidator(),
	        new NumericValidator(),
	        new ObjectValidator(), 
	        new StringFormatter()
	    );
	}





	@Override
	public Order find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Order object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Order object) {
		// TODO Auto-generated method stub
		return false;
	}

}
