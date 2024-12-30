package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaintenanceResponsable extends Employee implements Serializable{
	//Constants
	private static final long serialVersionUID = 4406992306206077202L;

	// Validators
	private ObjectValidator objectValidator;
	
	
	// Constructors
	public MaintenanceResponsable() {
		super();
		objectValidator = new ObjectValidator();
	}
	
	public MaintenanceResponsable(
		Optional<Integer> id, 
		String matricule, 
		String password, 
		String firstName, 
		String lastName,
		ObjectValidator	objectValidator,
		StringValidator stringValidator, 
		NumericValidator numericValidator,
		StringFormatter stringFormatter
	) {
		super(id, matricule, password, firstName, lastName, stringValidator, numericValidator, objectValidator, stringFormatter);
		this.objectValidator = objectValidator;
	}
	
	// Getters

	// Override methods
	@Override
	public String toString() {
		return super.toString() + "MaintenanceResponsable";
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
















