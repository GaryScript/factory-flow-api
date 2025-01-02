package be.alb_mar_hen.javabeans;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import be.alb_mar_hen.daos.FactoryFlowConnection;
import be.alb_mar_hen.daos.MachineDAO;
import be.alb_mar_hen.daos.PurchasingAgentDAO;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchasingAgent extends Employee implements Serializable{
	private static final long serialVersionUID = -371441893465098035L;

	// Constructors
	public PurchasingAgent() {}
	
	public PurchasingAgent(
		Optional<Integer> id, 
		String matricule, 
		String password, 
		String firstName, 
		String lastName,
		StringValidator stringValidator, 
		NumericValidator numericValidator,
		ObjectValidator objectValidator,
		StringFormatter stringFormatter
	) {                  
		super(
			id,
			matricule, 
			password,
			firstName,
			lastName, 
			stringValidator,
			numericValidator,
			objectValidator, 
			stringFormatter
		);
	}
	
	// Override methods
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
    public boolean equals(Object object) {
    	if(!(object instanceof Employee)) {
    		return false;
    	}

    	return super.equals((Employee) object);
    }
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public static String buyMachine(String requestBody) throws Exception {
        try {
        	ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());
            
            Machine machine = objectMapper.readValue(requestBody, Machine.class);
            JsonNode requestJson = objectMapper.readTree(requestBody);
            int purchasingAgentId = requestJson.get("purchasingAgentId").asInt();

            MachineDAO machineDAO = new MachineDAO(FactoryFlowConnection.getInstance());
            boolean isCreated = machineDAO.create(machine, purchasingAgentId) != 0;

            ObjectNode responseJson = objectMapper.createObjectNode();
            if (isCreated) {
                responseJson.put("status", "success");
                responseJson.put("message", "Machine purchase successful.");
            } else {
                responseJson.put("status", "failure");
                responseJson.put("message", "Failed to purchase the machine.");
            }

            return responseJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid JSON input: " + e.getMessage());
        }
    }
}