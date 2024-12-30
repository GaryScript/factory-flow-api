package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import be.alb_mar_hen.javabeans.Site;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

public class Site implements Serializable{
	private static final long serialVersionUID = 1253110849901374124L;
	// Validators
	StringValidator stringValidator;
	NumericValidator numericValidator;
	ObjectValidator objectValidator;
	
	// Attributes
	private Optional<Integer> id;
	private String city;
	
	// Constructors
	public Site() {}
	
	public Site(
		Optional<Integer> id, 
		String city,
		StringValidator stringValidator, 
		NumericValidator numericValidator,
		ObjectValidator objectValidator
	) {
		this.stringValidator = stringValidator;
		this.numericValidator = numericValidator;
		this.objectValidator = objectValidator;
		setId(id);
		setCity(city);
	}
	
	// Getters
	public Optional<Integer> getId() {
		return id;
	}
	
	public String getCity() {
		return city;
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
	
	public void setCity(String city) {
		if(!stringValidator.hasValue(city)) {
			throw new NullPointerException("City must have a value.");
		}
		
		this.city = city;
	}
	
	// Methods
	@Override
	public String toString() {
		return "Site [id=" + id.orElse(null) + ", city=" + city + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(city, id.orElse(0));
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
		
		Site other = (Site) obj;
		return Objects.equals(city, other.city) &&
			Objects.equals(id.orElse(0), other.id.orElse(0));
	}
	
	
}
