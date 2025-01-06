package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.alb_mar_hen.javabeans.MachineType;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyMachineType")
public class MachineType implements Serializable{
	private static final long serialVersionUID = 2550265953796667462L;
	// Validators
	private NumericValidator numericValidator;
	private StringValidator stringValidator;
	private ObjectValidator objectValidator;
	
	// Attributes
	private Optional<Integer> id;
	@JsonProperty("type")
	private String typeName;
	private double price;
	private int daysBeforeMaintenance;

	// Constructors
	public MachineType() {
		numericValidator = new NumericValidator();
		stringValidator = new StringValidator();
		objectValidator = new ObjectValidator();
	}
	
	public MachineType(
		Optional<Integer> id, 
		String type, 
		double price, 
		int daysBeforeMaintenance, 
		NumericValidator numericValidator, 
		StringValidator stringValidator,
		ObjectValidator objectValidator
	) {
		this.numericValidator = numericValidator;
		this.stringValidator = stringValidator;
		this.objectValidator = objectValidator;
		setId(id);
		setTypeName(type);
		setPrice(price);
		setDaysBeforeMaintenance(daysBeforeMaintenance);
	}

	// Getters
	public Optional<Integer> getId() {
		return id;
	}

	public String getType() {
		return typeName;
	}
	
	public double getPrice() {
		return price;
	}
	
	public int getDaysBeforeMaintenance() {
		return daysBeforeMaintenance;
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
	
	void setTypeName(String typeName) {
		if (!stringValidator.hasValue(typeName)) {
			throw new IllegalArgumentException("The type name must have a value.");
		}
		
		this.typeName = typeName;
	}
	
	void setPrice(double price) {
		if (!numericValidator.isPositive(price)) {
			throw new IllegalArgumentException("The price must be positive.");
		}

		this.price = price;
	}
	
	void setDaysBeforeMaintenance(int daysBeforeMaintenance) {
		if (!numericValidator.isPositive(daysBeforeMaintenance)) {
			throw new IllegalArgumentException("The days before maintenance must be positive.");
		}

		this.daysBeforeMaintenance = daysBeforeMaintenance;
	}

	// Override methods
	@Override
	public String toString() {
		return "MachineType [id=" + id.orElse(null) + 
			", typeName=" + typeName + 
			", price=" + price + 
			", daysBeforeMaintenance=" + daysBeforeMaintenance + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(daysBeforeMaintenance, id.orElse(0), numericValidator, price, stringValidator, typeName);
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
		
		MachineType other = (MachineType) obj;
		return daysBeforeMaintenance == other.daysBeforeMaintenance 
			&& Objects.equals(id.orElse(0), other.id.orElse(0))
			&& Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price)
			&& Objects.equals(typeName, other.typeName);
	}
}
