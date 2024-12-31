package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.alb_mar_hen.daos.DAO;
import be.alb_mar_hen.daos.EmployeeDAO;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.javabeans.MaintenanceWorker;
import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyEmployee")
public abstract class Employee implements Serializable{
	private static final long serialVersionUID = -1185141045732080158L;
	public final static String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
	public final static String NAME_REGEX = "^[\\p{L}'][ \\p{L}'-]*[\\p{L}]$";
	
	// Validators
	private StringValidator stringValidator; 
	private NumericValidator numericValidator;
	private ObjectValidator objectValidator;
	
	// Formatters
	private StringFormatter stringFormatter;
	
	// Attributes
	private Optional<Integer> id;
	private String matricule;
	private String password;
	private String firstName;
	private String lastName;
	
	// Constructors
	public Employee() {
		stringValidator = new StringValidator();
		numericValidator = new NumericValidator();
		objectValidator = new ObjectValidator();
		stringFormatter = new StringFormatter();
	}
	
	public Employee(
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
		this.stringValidator = stringValidator;
		this.numericValidator = numericValidator;
		this.objectValidator = objectValidator;
		this.stringFormatter = stringFormatter;
		setId(id);
		setMatricule(matricule);
		setPassword(password);
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	// Getters
	public Optional<Integer> getId() {
		return id;
	}
	
	public String getMatricule() {
		return matricule;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	// Setters
	public void setId(Optional<Integer> id) {
		if (!objectValidator.hasValue(id)) {
			throw new NullPointerException("Id must have a value.");
		}
		
	    if (!numericValidator.isPositiveOrEqualToZero(id)) {
	        throw new IllegalArgumentException("Id must be greater than or equal to 0");
	    }
	    
	    this.id = id;
	}

	
	public void setMatricule(String matricule) {
		if(!stringValidator.hasValue(matricule)) {			
			throw new NullPointerException("Matricule must have a value.");
		}
		
		this.matricule = matricule;
	}
	
	public void setPassword(String password) {
		if(!stringValidator.hasValue(password)) {
			throw new NullPointerException("The password must have a value.");
		}
		
		if(!stringValidator.matchRegEx(PASSWORD_REGEX, password)) {
			throw new IllegalArgumentException("Password must have at least 8 characters, contains at least a number and at least a letter.");
		}
		
		this.password = password;
	}
	
	public void setFirstName(String firstName) {
		if(!stringValidator.hasValue(firstName)) {
			throw new NullPointerException("The first name must have a value.");
		}
		
		if(!stringValidator.matchRegEx(NAME_REGEX, firstName)) {
			throw new IllegalArgumentException("The first name is incorrect.");
		}
		
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		if(!stringValidator.hasValue(lastName)) {
			throw new NullPointerException("The last name must have a value.");
		}
		
		if(!stringValidator.matchRegEx(NAME_REGEX, lastName)) {
			throw new IllegalArgumentException("The last name is incorrect.");
		}
		
		this.lastName = lastName;
	}
	
	// Private methods
	private String getFirstNameFormatted() {
		return stringFormatter.firstToUpper(firstName);
	}
	
	private String getLastNameFormatted() {
		return lastName.toUpperCase();
	}
	
	// Public methods
	public String getFullNameFormatted() {
		return getLastNameFormatted() + " " + getFirstNameFormatted();
	}

	// Override methods
	@Override
	public String toString() {
	    return "Person [id=" + id.orElse(null)
	        + ", matricule=" + matricule 
	        + ", password=" + password 
	        + ", firstName=" + firstName 
	        + ", lastName=" + lastName + "]";
	}

	@Override
	public int hashCode() {
	    return Objects.hash(firstName, id.orElse(0), lastName, matricule, password);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    
	    if (
    		!objectValidator.hasValue(obj) || 
    		getClass() != obj.getClass()
		) {
	        return false;
	    }
	    
	    Employee other = (Employee) obj;
	    return Objects.equals(firstName, other.firstName) 
	        && Objects.equals(id.orElse(null), other.id.orElse(null))
	        && Objects.equals(lastName, other.lastName)
	        && Objects.equals(matricule, other.matricule)
	        && Objects.equals(password, other.password);
	}
	
	public static Employee createEmployee(
	        String role,
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
	        switch (role) {
	            case "Maintenance Responsable":
	                return new MaintenanceResponsable(
	                    id,
	                    matricule,
	                    password,
	                    firstName,
	                    lastName,
	                    objectValidator,
	                    stringValidator,
	                    numericValidator,
	                    stringFormatter
	                );
	            case "Maintenance Worker":
	                return new MaintenanceWorker(
	                    id,
	                    matricule,
	                    password,
	                    firstName,
	                    lastName,
	                    stringValidator,
	                    numericValidator,
	                    stringFormatter,
	                    objectValidator
	                );
	            case "Purchasing Agent":
	                return new PurchasingAgent(
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
	            default:
	                throw new IllegalArgumentException("Unknown role: " + role);
	        }
	    }
}
