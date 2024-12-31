package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.Optional;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
=======
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
>>>>>>> feature/get-maintenances

import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

<<<<<<< HEAD
@JsonIgnoreProperties(ignoreUnknown = true)
=======
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyPurchasingAgent")
>>>>>>> feature/get-maintenances
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
}